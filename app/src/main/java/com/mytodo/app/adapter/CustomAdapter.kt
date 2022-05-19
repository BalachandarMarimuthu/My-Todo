package com.mytodo.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mytodo.app.R
import com.mytodo.app.databinding.ListItemBinding
import com.mytodo.app.model.Todo

class CustomAdapter(var todo: List<Todo>, var context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Todo) {
            binding.todo.text = data.todo
            if (data.complete == "true")
                binding.checkBox.setImageResource(R.drawable.check_box_tick)
            else
                binding.checkBox.setImageResource(R.drawable.check_box)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(todo[position])
        val data = todo[position]
        holder.binding.content.setOnClickListener {
            val db = Firebase.firestore
            db.collection("todo")
                .whereEqualTo("todo", data.todo)
                .get()
                .addOnSuccessListener { result ->
                    val myTODO =
                        hashMapOf("complete" to if (data.complete == "true") "false" else "true")
                    db.collection("todo")
                        .document(result.documents.first().id)
                        .update(myTODO.toMutableMap() as MutableMap<String, Any>)
                }
        }
        holder.binding.more.setOnClickListener {
            val db = Firebase.firestore
            db.collection("todo")
                .whereEqualTo("todo", data.todo)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("todo")
                        .document(result.documents.first().id)
                        .delete()
                }
        }
    }

    override fun getItemCount() = todo.size
}
