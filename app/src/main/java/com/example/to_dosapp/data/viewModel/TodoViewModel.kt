package com.example.to_dosapp.data.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dosapp.data.TodoModel
import com.example.to_dosapp.data.repository.TodoRepoImplementation
import com.example.to_dosapp.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepoImplementation
) : ViewModel() {

    private val _listItems: MutableState<TodoListState> = mutableStateOf(TodoListState())
    val listItems: State<TodoListState> get() = _listItems

//    private val _currentItemState: MutableState<TodoModel> =
//        mutableStateOf<TodoModel>(TodoModel(title = "", isCompleted = false, key = ""))
//    val currentItemState: State<TodoModel> get() = _currentItemState
//
//    fun updateModel(item: TodoModel) {
//        _currentItemState.value = item
//    }

    init {
        fetchTodoItems()
    }

    private fun fetchTodoItems() {
        viewModelScope.launch {
            repository.getAllTodos().collect { resultState ->
                when (resultState) {
                    is ResultState.Success -> {
                        _listItems.value = TodoListState(
                            items = resultState.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Failure -> {
                        _listItems.value = TodoListState(
                            error = resultState.error.message.toString(),
                            isLoading = false
                        )
                    }

                    ResultState.Loading -> {
                        _listItems.value = TodoListState(
                            isLoading = true
                        )
                    }
                }

            }
        }
    }


    fun addTodo(item: TodoModel) = repository.insertTodo(item)

    fun deleteTodo(key: String) = repository.deleteTodo(key)

    fun updateTodo(item: TodoModel) = repository.updateTodo(item)


}

data class TodoListState(
    val items: List<TodoModel> = emptyList(),
    val error: String = "",
    val isLoading: Boolean = false
)

