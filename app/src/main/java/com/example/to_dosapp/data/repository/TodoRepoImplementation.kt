package com.example.to_dosapp.data.repository

import androidx.compose.runtime.snapshots.readable
import com.example.to_dosapp.data.TodoModel
import com.example.to_dosapp.util.ResultState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.xml.transform.Result
import kotlin.math.sin

class TodoRepoImplementation @Inject constructor(
    private val database: DatabaseReference
) : TodoRepository {

    override fun insertTodo(item: TodoModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val keyReference = database.push()
        val itemWithKey = item.copy(key = keyReference.key ?: "")

        keyReference.setValue(itemWithKey).addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(ResultState.Success("Task Added"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose {
            close()
        }

    }

    override fun getAllTodos(): Flow<ResultState<List<TodoModel>>> = callbackFlow {

        trySend(ResultState.Loading)

        val eventListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val todoItems =
                    mutableListOf<TodoModel>()  // empty list is using foreach loop to manually update list

                // with map
//
//                val items = snapshot.children.mapNotNull { item ->
//                    item.getValue(TodoModel::class.java)
//                }
//                trySend(ResultState.Success(items))



                // with foreach Loop

                snapshot.children.forEach { item ->
                    val singleTodoItem = item.getValue(TodoModel::class.java)
                    if (singleTodoItem != null) {
                        todoItems.add(singleTodoItem)
                    }

                }
                trySend(ResultState.Success(todoItems))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        database.addValueEventListener(eventListener)

        awaitClose {
            database.removeEventListener(eventListener)
            close()
        }

    }

    override fun deleteTodo(key: String): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        database.child(key).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(ResultState.Success("Task Deleted"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose {
            close()
        }
    }

    override fun updateTodo(item: TodoModel): Flow<ResultState<String>> = callbackFlow {

//        trySend(ResultState.Loading)

        val hashMap = HashMap<String, Any>()
        hashMap["title"] = item.title
        hashMap["isCompleted"] = item.isCompleted
        hashMap["key"] = item.key

        database.child(item.key).updateChildren(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(ResultState.Success("Task Updated"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Failure(it))
        }

        awaitClose { close() }
    }

}