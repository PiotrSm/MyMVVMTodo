package com.homeworkshop.mymvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.homeworkshop.mymvvmtodo.R
import com.homeworkshop.mymvvmtodo.data.SortOrder
import com.homeworkshop.mymvvmtodo.databinding.FragmentTasksBinding
import com.homeworkshop.mymvvmtodo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

            //dodawanie usuwania na swipe w left or right
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }

            }
            ).attachToRecyclerView(recyclerViewTasks)
        }
        //drugim argumentem jest lambda ale poniewż jest to ostatni argument możemy użyć składni {} i wyrzucić to na zwenątrz
        viewModel.tasks.observe(viewLifecycleOwner) {
            //za każdym razem jak sie zmieni lista tasków uaktualniamy adapter
            taskAdapter.submitList(it) // submitList jest metodą ListAdaptera, która jest wywołuje porównanie starej i nowej listy i update starej
        }

        //przechwytywanie eventu zdarzenia wysłanego z viewModelu
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect { event ->
                when(event){
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage ->{
                        Snackbar.make(requireView(),"Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                }
            }
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
        //musimy dodać zczytywanie ustawień hideCompleted i sortOrder z dataStore
        //lifecycleScope żyje tak długo jak żyje fragment
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideComleted
        }
    }

    //funkcja uruchomiana kiedy któryś z elementów menu został kliknięty
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}