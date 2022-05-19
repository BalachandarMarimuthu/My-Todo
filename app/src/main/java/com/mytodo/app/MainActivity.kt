package com.mytodo.app

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.mytodo.app.adapter.CustomAdapter
import com.mytodo.app.databinding.ActivityMainBinding
import com.mytodo.app.model.Todo

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.add.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.add_todo, null)
            dialog.setContentView(view)
            val btn = view.findViewById<Button>(R.id.add)
            val todo = view.findViewById<EditText>(R.id.todo)
            btn.setOnClickListener {
                if (todo.text.toString().trim().isNotEmpty()) {
                    val myTODO =
                        hashMapOf("todo" to todo.text.toString().trim(), "complete" to "false")
                    db.collection("todo")
                        .add(myTODO)
                        .addOnSuccessListener {
                            getData()
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                }
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.show()
        }

        db.collection("todo").addSnapshotListener { snapshot, e ->
            getData()
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        db.collection("todo").get().addOnSuccessListener { result ->
            println("data - us")
            val da = result.toObjects<Todo>()
            println(da.size)
            setupList(da.reversed())
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents.", exception)
        }
    }

    private fun setupList(da: List<Todo>) {
        if (da.isNotEmpty()) {
            binding.noData.visibility = GONE
            binding.todo.visibility = VISIBLE
            binding.todo.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.todo.adapter = CustomAdapter(da, this@MainActivity)
        } else {
            binding.noData.visibility = VISIBLE
            binding.todo.visibility = GONE
        }
    }
}
