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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPriority: EditText
    private lateinit var editTextTags: EditText
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
        editTextTags = findViewById(R.id.edit_text_tags)

        textViewData = findViewById(R.id.text_view_data)
        addButton = findViewById(R.id.button_add)
        loadButton = findViewById(R.id.load_button)

        loadButton.setOnClickListener{
            loadNotes()
        }

        addButton.setOnClickListener {
            addNote()
        }

        updateArray()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()

        if (editTextPriority.text.toString().isEmpty()) {
            editTextPriority.setText("0")
        }

        val tagsArray = editTextTags.text.toString().trim().split(",")
        val tags = if (tagsArray.size == 1 && tagsArray[0] == "") null else tagsArray.toMutableList()

        val priority = editTextPriority.text.toString().toInt()

        val note  = Note(title, description, priority, tags)
        noteBookRef.add(note)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not saved.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotes() {
        noteBookRef
            .whereArrayContains("tags", "Fantasio")
            .get()
            .addOnSuccessListener {
                var data = ""
                for (documentSnapshot in it) {
                    val note = documentSnapshot.toObject(Note::class.java)
                    note.id = documentSnapshot.id

                    data += "\n\nID: ${note.id}\n"
                    note.tags?.let {
                        for (tag in note.tags) {
                            data += "- $tag\n"
                        }
                    }

                }
                textViewData.text = data
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateArray() {
        //noteBookRef.document("XdERfuHXMVjB4AX7F1S3").update("tags", FieldValue.arrayUnion("new tag"))
        noteBookRef.document("XdERfuHXMVjB4AX7F1S3").update("tags", FieldValue.arrayRemove("new tag"))
    }
}