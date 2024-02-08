package com.example.firestoreproject.classes

data class Note (val title: String, val description: String) {
    constructor(): this("", "") // public no-arg constructor needed by Firestore
}