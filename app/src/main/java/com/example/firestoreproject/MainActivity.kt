package com.example.firestoreproject

import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.firestoreproject.classes.Note
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import org.w3c.dom.DocumentType

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPriority: EditText
    private lateinit var addButton: Button
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRef: CollectionReference = db.collection("Notebook")
    private var lastResult: DocumentSnapshot? = null

    private lateinit var loadButton: Button
    private lateinit var textViewData: TextView

    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        editTextPriority = findViewById(R.id.edit_text_priority)

        textViewData = findViewById(R.id.text_view_data)
        addButton = findViewById(R.id.button_add)
        loadButton = findViewById(R.id.load_button)

        loadButton.setOnClickListener{
            loadNotes()
        }

        addButton.setOnClickListener {
            addNote()
        }
    }

    override fun onStart() {
        super.onStart()
        noteBookRef.orderBy("priority")
            .addSnapshotListener(this) { snapshot, error ->
                error?.let {
                    Log.d("debugging", "Failed to attach SnapshotListener")
                    return@addSnapshotListener
                }

                snapshot?.let {
                    Log.d("debugging", "Some documents changed")
                    for (dc in it.documentChanges){
                        val id = dc.document.id
                        val oldIndex = dc.oldIndex
                        val newIndex = dc.newIndex

                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.d("debugging", "A document has been added")
                                textViewData.append("\nAdded: $id\nOld Index: $oldIndex\nNew Index: $newIndex\n\n")
                            }
                            DocumentChange.Type.REMOVED -> {
                                Log.d("debugging", "A document has been removed")
                                textViewData.append("\nRemoved: $id\nOld Index: $oldIndex\nNew Index: $newIndex\n\n")
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Log.d("debugging", "A document has been modified")
                                textViewData.append("\nModified: $id\nOld Index: $oldIndex\nNew Index: $newIndex\n\n")
                            }
                        }
                    }
                }
            }
    }

    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()

        if (editTextPriority.text.toString().isEmpty()) {
            editTextPriority.setText("0")
        }
        val priority = editTextPriority.text.toString().toInt()

        val note  = Note(title, description, priority)
        noteBookRef.add(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not saved.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotes() {
        val query = if (lastResult == null) {
            Log.d("debugging", "Fetching the first page")
            noteBookRef.orderBy("priority")
                .limit(3)
        } else {
            Log.d("debugging", "Fetching the next page")
            noteBookRef.orderBy("priority")
                .startAfter(lastResult as DocumentSnapshot)
                .limit(3)
        }
        query.get()
            .addOnSuccessListener {
                Log.d("debugging", "Successfully fetched some data")
                var data = ""
                for (queryDocument in it) {
                    val note = queryDocument.toObject(Note::class.java)
                    note.id = queryDocument.id

                    val title = note.title
                    val description = note.description
                    val priority = note.priority
                    val id = note.id

                    data += "Id: $id\nTitle: $title\nDescription: $description\nPriority: $priority\n\n"
                }
                if (it.size() > 0) {
                    Log.d("debugging", "Fetched a non empty page")
                    data += "________________\n\n"
                    textViewData.append(data)
                    lastResult = it.documents[it.size() -1]
                } else {
                    Log.d("debugging", "Fetched an empty page")
                }
            }
    }
}