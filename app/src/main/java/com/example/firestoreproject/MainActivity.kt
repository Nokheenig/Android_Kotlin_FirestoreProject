package com.example.firestoreproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.firestoreproject.classes.Note
import com.example.firestoreproject.databinding.ActivityMainBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRef: CollectionReference = db.collection("Notebook")
    private var lastResult: DocumentSnapshot? = null



    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        mBinding.loadButton.setOnClickListener{
            loadNotes()
        }

        mBinding.buttonAdd.setOnClickListener {
            addNote()
        }

        updateObjects()
    }

    private fun addNote() {
        val title = mBinding.editTextTitle.text.toString()
        val description = mBinding.editTextDescription.text.toString()

        if (mBinding.editTextPriority.text.toString().isEmpty()) {
            mBinding.editTextPriority.setText("0")
        }

        val tagsArray = mBinding.editTextTags.text.toString().trim().split(",")
        val tags = if (tagsArray.size == 1 && tagsArray[0] == "") null else mutableMapOf<String, Boolean>()

        tags?.let {
            for (tag in tagsArray) {
                tags[tag] = true
            }
        }


        val priority = mBinding.editTextPriority.text.toString().toInt()

        val note  = Note(title, description, priority, tags)
        noteBookRef.document("3kon1hXzPJrn5LV3UfyF").collection("Note SubCollection")
            .add(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not saved.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotes() {
        noteBookRef
            .document("3kon1hXzPJrn5LV3UfyF")
            .collection("Note SubCollection")
            .get()
            .addOnSuccessListener {
                var data = ""
                for (documentSnapshot in it) {
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.id = documentSnapshot.id

                    data += "\n\nID: ${note.id}\n"
                    note.tags?.let {
                        for (tag in note.tags.keys) {
                            data += "- $tag\n"
                        }
                    }

                }
                mBinding.textViewData.text = data
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateObjects() {
        noteBookRef.document("iWmyFGWMbGJgWOs2t8Zi")
            .update("tags.tag1", true)
        noteBookRef.document("iWmyFGWMbGJgWOs2t8Zi")
            .update("tags.tag2", true)
        noteBookRef.document("iWmyFGWMbGJgWOs2t8Zi")
            .update("tags.tag3", FieldValue.delete())
    }
}