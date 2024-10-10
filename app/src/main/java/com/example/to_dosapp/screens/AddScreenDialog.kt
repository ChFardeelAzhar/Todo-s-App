package com.example.to_dosapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.to_dosapp.data.TodoModel
import com.example.to_dosapp.data.viewModel.TodoViewModel
import com.example.to_dosapp.util.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    item: TodoModel,
    showDialog: MutableState<Boolean>,
    viewModel: TodoViewModel = hiltViewModel(),

    ) {

    var title = remember {
        mutableStateOf(item.title ?: "")
    }

    val isCompleted = remember {
        mutableStateOf(item?.isCompleted ?: false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    val showLoadingProgressBar = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { showDialog.value = false },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    placeholder = { Text("Add a to-do item") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .focusRequester(focusRequester)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = .7f))
                    ,
                    textStyle = TextStyle(fontSize = 14.sp)
                )

                TextButton(
                    onClick = {

                        if (item.key.isNullOrEmpty()) {
                            if (!title.value.isNullOrEmpty()) {
                                scope.launch(Dispatchers.Main) {
                                    viewModel.addTodo(
                                        TodoModel(
                                            title = title.value,
                                            isCompleted = isCompleted.value
                                        )
                                    ).collect {
                                        when (it) {
                                            is ResultState.Success -> {
                                                showDialog.value = false
                                                Toast.makeText(
                                                    context,
                                                    "Task added",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }

                                            is ResultState.Failure -> {
                                                Toast.makeText(
                                                    context,
                                                    "${it.error.message.toString()}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showDialog.value = false
                                            }

                                            ResultState.Loading -> {
                                                showLoadingProgressBar.value = true
                                            }
                                        }
                                    }


                                }
                            } else {
                                showDialog.value = false
                            }
                        } else {
                            scope.launch(Dispatchers.Main) {
                                viewModel.updateTodo(
                                    TodoModel(
                                        title = title.value,
                                        isCompleted = isCompleted.value,
                                        key = item.key
                                    )
                                ).collect {
                                    when (it) {
                                        is ResultState.Success -> {
                                            showDialog.value = false
                                            Toast.makeText(
                                                context,
                                                "Task Updated",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()

                                        }

                                        is ResultState.Failure -> {
                                            showDialog.value = false
                                            Toast.makeText(
                                                context,
                                                "${it.error.message.toString()}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showDialog.value = false
                                        }

                                        ResultState.Loading -> {
                                            showLoadingProgressBar.value = true
                                        }
                                    }
                                }


                            }

                        }

                    }
                ) {
                    Text("Save")
                }

            }
        },

        )


    /*
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        text = {
            Column {
                // Rounded TextField without borders
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    placeholder = { Text("Add a to-do item") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    enabled = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .focusRequester(focusRequester)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = .3f)),
                    textStyle = TextStyle(fontSize = 14.sp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!title.value.isNullOrEmpty()){
                        scope.launch(Dispatchers.Main) {
                            viewModel.addTodo(
                                TodoModel(
                                    title = title.value,
                                    isCompleted = isCompleted.value
                                )
                            ).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        showDialog.value = false
                                        Toast.makeText(context, "Task added", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    is ResultState.Failure -> {
                                        Toast.makeText(
                                            context,
                                            "${it.error.message.toString()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showDialog.value = false
                                    }

                                    ResultState.Loading -> {
                                        showLoadingProgressBar.value = true
                                    }
                                }
                            }


                        }
                    }else{
                        showDialog.value = false
                    }
                }
            ) {
                Text("Save")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
     */

    if (showLoadingProgressBar.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }


}
