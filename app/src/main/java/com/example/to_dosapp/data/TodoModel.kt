package com.example.to_dosapp.data

import android.app.DownloadManager.Query
import com.google.firebase.database.PropertyName

data class TodoModel(
    val title: String = "",
    @get:PropertyName("isCompleted") @set:PropertyName("isCompleted") var isCompleted: Boolean = false,
    val key: String = ""
) {
    fun doesMatchQuery(query: String): Boolean {

        /*
        val matchCombinations = listOf<String>(
            "$title"
            // else if by date "$date" in future if add
        )
        return matchCombinations.any { combinations->
            combinations.contains(query)
        }
         */

        return title.contains(query, ignoreCase = false)

    }
}