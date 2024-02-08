package com.example.firestoreproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.firestoreproject.classes.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var addButton: Button
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRef: CollectionReference = db.collection("Notebook")

    private lateinit var loadButton: Button
    private lateinit var textViewData: TextView

    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)

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

        noteBookRef.addSnapshotListener { documentSnapshots, error ->
            error?.let {
                return@addSnapshotListener
            }
            documentSnapshots?.let {
                var data = ""

                for (documentSnapshot in it){
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.id = documentSnapshot.id
                    val title = note.title
                    val description = note.description

                    data+= "ID: ${note.id}\nTitle: $title\nDescription: $description\n\n"
                }
                textViewData.text = data
            }
        }
    }

    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()

        val note  = Note(title, description)
        noteBookRef.add(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not saved.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotes() {
        noteBookRef.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                var data = ""

                for (documentSnapshot in queryDocumentSnapshots){
                    val note = documentSnapshot.toObject(Note::class.java)
                    val title = note.title
                    val description = note.description

                    data+= "Title: $title\nDescription: $description\n\n"
                }
                textViewData.text = data
            }
    }
}