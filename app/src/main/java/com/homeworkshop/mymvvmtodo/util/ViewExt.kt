package com.homeworkshop.mymvvmtodo.util

import androidx.appcompat.widget.SearchView

//inline nie jest konieczne dla funkcjonalności , wypływa tylko na wydajność
//jest to funkcja rozszeżająca do SearchView która w argumencie przyjmuje inną funkcję z argumentem typu String
inline fun SearchView.onQueryTextChanged(crossinline listener:(String) -> Unit){
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        // nie używamy tej funkcji. To ma działać jak tekst jest wprowadzany
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
        //Ta funkcja działa jak tekst jest wprowadzany
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty()) // orEmpty wstawi pusty String jeżeli będzie null
            return true
        }

    })
}