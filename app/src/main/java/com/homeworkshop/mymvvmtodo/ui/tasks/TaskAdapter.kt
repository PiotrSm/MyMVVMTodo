package com.homeworkshop.mymvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeworkshop.mymvvmtodo.data.Task
import com.homeworkshop.mymvvmtodo.databinding.ItemTaskBinding

//ListAdapter updatuje całą listę nie pojedyńczy wpis
//ListAdapter robi porównanie starej i nowej listy w background thread
//Argumentem konstruktora musi być kalsa ItemCalback która zawiera metody porównujące elementy list
class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }

    //ItemTaskBinding jest automatycznie generowany na podstawie odpowiedniego layoutu i zawiera cały layout
    class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //funkcja ta przypisze odpiwiednie dane do każego tasku - wywołujemy ją w onBindViewHolder
        fun bind(task: Task) {
            binding.apply {
                //ustawiamy wszystkie pola tego layoutu
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPririty.isVisible = task.important
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        //w metodzie porównujemy całe elementy - wszystkie pola tasku dzięki temu że jest to klasa typu data
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

    }
}