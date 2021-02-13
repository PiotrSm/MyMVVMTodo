package com.homeworkshop.mymvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.homeworkshop.mymvvmtodo.data.PreferencesManager
import com.homeworkshop.mymvvmtodo.data.SortOrder
import com.homeworkshop.mymvvmtodo.data.Task
import com.homeworkshop.mymvvmtodo.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao, // wstrzykujemy dao w konstruktorze
    private val preferencesManager: PreferencesManager // wstrzykujemy preferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    //zamiast tych dwóch wartości użyjemy zmiennej preferencesFlow zczytanej w preferencesManagerze z dataStore
//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)


    //To rozwiązanie jest dobre jeżeli jest tylko jedno flow. Jeżeli jest więcej strzeba zastosować combine
//    private val tasksFlow = searchQuery.flatMapLatest {
//        taskDao.getTasks(it)
//    }

//    //stosując combine możemy przesłać kilka flows
//    //jeżeli zmienimy którąś z wartości lambda wyśle wszystkie
//    private val tasksFlow = combine(
//        searchQuery,
//        sortOrder,
//        hideCompleted
//    ){ query, sortOrder, hideCompleted ->
//        Triple(query,sortOrder,hideCompleted) //chcemy aby funkcja zwróciła 3 wartości ale można zwracać tylko 1 dlatego opakowujemy je
//    }.flatMapLatest { (query,sortOrder,hideCompleted) ->
//        taskDao.getTasks(query,sortOrder,hideCompleted)
//    }

    val preferencesFlow = preferencesManager.preferencesFlow

    //Kanał który reprezentuje te TaskEvent
    private val taskEventChannel = Channel<TasksEvent>()
    val taskEvent =
        taskEventChannel.receiveAsFlow() // zamienia nasz Kanał w Flow z którego możemy potem wybierać dane

    //Użycie preferencesFlow zamiast dwóch lokalnych zmiennych do ustawień
    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, preferences ->
        Pair(query, preferences)
    }.flatMapLatest { (query, preferences) ->
        taskDao.getTasks(query, preferences.sortOrder, preferences.hideComleted)
    }

    //metody updatujące ustawienia sortOrder i hideCompleted
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        //wkładamy event do tego kanału aby został przechwycony przez fragment
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    //metoda wywoływana z fragmentu dla cofnięcia usuniecia taska
    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    val tasks = tasksFlow.asLiveData()

    // stosujemy Flow pod LiveData bo Flow jest bardziej flexible i dostarcza wielu potrzebnych metod
//    val tasks = taskDao.getTasks("bla").asLiveData() // pobieranie listy taskow które będą przechowywane w ViewModelu

    /**
     * klasaa która reprezentuje różne rodzaje eventów które chcemy wysyłać do fragmentu
     */
    sealed class TasksEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
    }
}
