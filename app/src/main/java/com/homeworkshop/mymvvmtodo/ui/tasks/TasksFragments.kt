package com.homeworkshop.mymvvmtodo.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.homeworkshop.mymvvmtodo.R
import com.homeworkshop.mymvvmtodo.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragments:Fragment(R.layout.fragment_tasks) {

    //To wstrzyknięcie jest możliwe bo całą klase zakeklarowaliśmy jako @AndroidEntryPoint
    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //pobieramy reprezentację layoutu, nie musimy robić inflate bo widok już jest inflated
        val binding = FragmentTasksBinding.bind(view)

        //tworzymy instację taskAdaptera
        val taskAdapter = TaskAdapter()

        //pobieramy recyclerView z layoutu i robimy dla niego setup
        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
            //drugim argumentem jest lambda ale poniewż jest to ostatni argument możemy użyć składni {} i wyrzucić to na zwenątrz
        viewModel.tasks.observe(viewLifecycleOwner){
            //za każdym razem jak sie zmieni lista tasków uaktualniamy adapter
            taskAdapter.submitList(it) // submitList jest metodą ListAdaptera, która jest wywołuje porównanie starej i nowej listy i update starej
        }
    }
}