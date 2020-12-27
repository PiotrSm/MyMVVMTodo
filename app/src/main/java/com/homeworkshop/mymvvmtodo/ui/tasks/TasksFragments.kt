package com.homeworkshop.mymvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.homeworkshop.mymvvmtodo.R
import com.homeworkshop.mymvvmtodo.databinding.FragmentTasksBinding
import com.homeworkshop.mymvvmtodo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragments : Fragment(R.layout.fragment_tasks) {

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
        viewModel.tasks.observe(viewLifecycleOwner) {
            //za każdym razem jak sie zmieni lista tasków uaktualniamy adapter
            taskAdapter.submitList(it) // submitList jest metodą ListAdaptera, która jest wywołuje porównanie starej i nowej listy i update starej
        }
        //bez tego menu nie będzie widoczne
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        //funkcja onQueryTextChanged przyjmuje String argument ale nie musimy go podawać w nawiasach () przy funkcji można użyć tego w ciele jako lambda
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    //funkcja uruchomiana kiedy któryś z elementów menu został kliknięty
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }
            R.id.action_hide_completed_tasks ->{
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.action_delete_all_completed_tasks ->{

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}