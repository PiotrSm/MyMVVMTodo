package com.homeworkshop.mymvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.homeworkshop.mymvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao // wstrzykujemy dao w konstruktorze
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    //To rozwiązanie jest dobre jeżeli jest tylko jedno flow. Jeżeli jest więcej strzeba zastosować combine
//    private val tasksFlow = searchQuery.flatMapLatest {
//        taskDao.getTasks(it)
//    }

    //stosując combine możemy przesłać kilka flows
    //jeżeli zmienimy którąś z wartości lambda wyśle wszystkie
    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ){ query, sortOrder, hideCompleted ->
        Triple(query,sortOrder,hideCompleted) //chcemy aby funkcja zwróciła 3 wartości ale można zwracać tylko 1 dlatego opakowujemy je
    }.flatMapLatest { (query,sortOrder,hideCompleted) ->
        taskDao.getTasks(query,sortOrder,hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

        // stosujemy Flow pod LiveData bo Flow jest bardziej flexible i dostarcza wielu potrzebnych metod
//    val tasks = taskDao.getTasks("bla").asLiveData() // pobieranie listy taskow które będą przechowywane w ViewModelu
}
//reprezentuje różne stany
enum class SortOrder{BY_NAME,BY_DATE}