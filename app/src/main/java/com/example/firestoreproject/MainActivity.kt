package com.example.firestoreproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.firestoreproject.classes.Note
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var saveButton: Button
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference = db.collection("Notebook").document("My first note")

    private lateinit var loadButton: Button
    private lateinit var updateTitleButton: Button
    private lateinit var deleteDescriptionButton: Button
    private lateinit var deleteNoteButton: Button
    private lateinit var textViewData: TextView

    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        saveButton = findViewById(R.id.button_save)

        textViewData = findViewById(R.id.text_view_data)
        loadButton = findViewById(R.id.load_button)
        updateTitleButton = findViewById(R.id.button_update_title)
        deleteDescriptionButton = findViewById(R.id.button_delete_description)
        deleteNoteButton = findViewById(R.id.button_delete_note)

        deleteDescriptionButton.setOnClickListener {
            deleteDescription()
        }
        deleteNoteButton.setOnClickListener {
            deleteNote()
        }

        updateTitleButton.setOnClickListener{
            updateTitle()
        }

        loadButton.setOnClickListener{
            loadData()
        }

        saveButton.setOnClickListener {
            save()
        }
    }

    override fun onStart() {
        super.onStart()

        docRef.addSnapshotListener(this) { document, error ->
            error?.let {
                return@addSnapshotListener
            }
            document?.let {
                if (it.exists()) {

                    val note = it.toObject(Note::class.java)

                    textViewData.text = "Title: ${note?.title}\nDescription: ${note?.description}"
                } else {
                    textViewData.text = ""
                    Toast.makeText(this@MainActivity, "The document doesn't exist", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun save() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()

        val note  = Note(title, description)
        docRef.set(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not saved.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData() {
        docRef.get()
            .addOnSuccessListener {document ->
                if (document.exists()) {
                    val title = document.getString((KEY_TITLE))
                    val description = document.getString(KEY_DESCRIPTION)

//                    val note = mutableMapOf<String, Any>()
//                    note.put(KEY_TITLE, title!!)
//                    note.put(KEY_DESCRIPTION, description!!)

                    textViewData.text = "Title: $title\nDescription: $description"
                } else {
                    Toast.makeText(this@MainActivity, "The document doesn't exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Failed to load the note data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTitle() {
        val title = editTextTitle.text.toString()
        val note = mutableMapOf<String, Any>()
        note[KEY_TITLE] = title

        docRef.set(note, SetOptions.merge())
    }

    private fun deleteDescription() {
        val note = mutableMapOf<String, Any>()
        note[KEY_DESCRIPTION] = FieldValue.delete() // Equivalent to: note.put(KEY_DESCRIPTION, FieldValue.delete())
        docRef.update(note)
    }

    private fun deleteNote() {
        docRef.delete()
    }
}