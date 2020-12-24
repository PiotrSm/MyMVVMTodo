package com.homeworkshop.mymvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.homeworkshop.mymvvmtodo.data.TaskDao

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao // wstrzykujemy dao w konstruktorze
): ViewModel() {
        // stosujemy Flow pod LiveData bo Flow jest bardziej flexible i dostarcza wielu potrzebnych metod
    val tasks = taskDao.getTasks().asLiveData() // pobieranie listy taskow które będą przechowywane w ViewModelu
}