package com.example.recyclerview

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), RecyclerInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var adapter: RecyclerAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    val database = Firebase.database
    lateinit var reference: DatabaseReference
    private  val TAG = "MainActivity"
    var recyclerDataClass: ArrayList<RecyclerDataClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reference = database.reference
        recyclerView = findViewById(R.id.rv)
        floatingActionButton = findViewById(R.id.fab)
        adapter = RecyclerAdapter(recyclerDataClass, this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value: RecyclerDataClass? = snapshot.getValue<RecyclerDataClass>()
                value?.key = snapshot.key?:""
                value?.let {
                    recyclerDataClass.add(value)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val value: RecyclerDataClass? = snapshot.getValue<RecyclerDataClass>()
                value?.let {
                    value.key = snapshot?.key?:""
                    var index = -1

                    for(position in 0 until recyclerDataClass.size){
                        if(recyclerDataClass[position].key == snapshot.key){
                            index = position
                        }
                    }
                    if(index>=0) {
                        recyclerDataClass.removeAt(index)
                        recyclerDataClass.add(index, value)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val value: RecyclerDataClass? = snapshot.getValue<RecyclerDataClass>()
                value?.let {
                    value.key = snapshot?.key?:""
                    var index = -1

                    for(position in 0 until recyclerDataClass.size){
                        if(recyclerDataClass[position].key == snapshot.key){
                            index = position
                        }
                    }
                    if(index>=0) {
                        recyclerDataClass.removeAt(index)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        floatingActionButton.setOnClickListener {
            showDialog(null)
        }

    }

    private fun showDialog(recyclerDataClass:RecyclerDataClass?) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog)
        val customView: View = layoutInflater.inflate(R.layout.layout_dialog, null)
        dialog.setContentView(customView)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val etTitle = dialog.findViewById(R.id.etTitle) as EditText
        val etMessage = dialog.findViewById(R.id.etMessage) as EditText
        val btnAddValue = dialog.findViewById(R.id.btnAddValue) as Button
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        recyclerDataClass?.let {
            etTitle.setText(recyclerDataClass.title?:"")
            etMessage.setText(recyclerDataClass.message?:"")
            btnAddValue.setText(resources.getString(R.string.update_value))
            tvTitle.setText(resources.getString(R.string.update_value))
        }
        btnAddValue.setOnClickListener {
            if (etTitle.text.toString().isNullOrEmpty()) {
                etTitle.error = resources.getString(R.string.add_title)
                return@setOnClickListener
            }
            if (etMessage.text.toString().isNullOrEmpty()) {
                etMessage.error = resources.getString(R.string.add_message)
                return@setOnClickListener
            } else {
                recyclerDataClass?.let {
                    database.getReference(recyclerDataClass.key).setValue(
                        RecyclerDataClass(
                            etTitle.text.toString(),
                            etMessage.text.toString()
                        )
                    )
                }?:kotlin.run {
                reference.push().setValue(
                    RecyclerDataClass(
                        etTitle.text.toString(),
                        etMessage.text.toString()
                    )
                ) }
                adapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun recyclerInterface(recyclerDataClass: RecyclerDataClass) {
        showDialog(recyclerDataClass)
    }
}