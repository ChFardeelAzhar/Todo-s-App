package com.example.to_dosapp.data.repository

import com.example.to_dosapp.data.TodoModel
import com.example.to_dosapp.util.ResultState
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun insertTodo(item: TodoModel): Flow<ResultState<String>>

    fun getAllTodos(): Flow<ResultState<List<TodoModel>>>

    fun deleteTodo(key: String): Flow<ResultState<String>>

    fun updateTodo(item: TodoModel): Flow<ResultState<String>>

}