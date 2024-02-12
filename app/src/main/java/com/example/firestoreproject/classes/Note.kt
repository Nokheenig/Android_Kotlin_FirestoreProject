package com.example.firestoreproject.classes

import com.google.firebase.firestore.Exclude

data class Note (val title: String, val description: String, val priority: Int, val tags: MutableMap<String,Boolean>?) {
    constructor(): this("", "", 0, null) // public no-arg constructor needed by Firestore

    @Exclude
    var id: String = ""
}