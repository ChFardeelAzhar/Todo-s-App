package com.example.to_dosapp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.to_dosapp.R
import com.example.to_dosapp.data.TodoModel
import com.example.to_dosapp.data.viewModel.TodoViewModel
import com.example.to_dosapp.util.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TodoViewModel = hiltViewModel()
) {

    val state = viewModel.listItems.value
    var showDialog = remember { mutableStateOf(false) } // for inserting a new item
    var searchText = remember { mutableStateOf("") }

    var isSearching = remember {
        mutableStateOf(false)
    }

    val currentTodoItemState = remember {
        mutableStateOf<TodoModel?>(null)
    }

    var showDeleteDialog = remember {
        mutableStateOf(false)
    }

    var showUpdateDialog = remember {
        mutableStateOf(false)
    }

    var showLoadingIndicator = remember {
        mutableStateOf(false)
    }

    val itemToDelete = remember {
        mutableStateOf<String>("")
    }

//    val completedTasks = remember {
//        mutableSetOf<TodoModel>()
//    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filterContacts = state.items.filter { item ->
        item.title.contains(searchText.value, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            if (!isSearching.value) {
                TopAppBar(
                    title = {
                        Text(
                            text = "To-Do's",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.padding(start = 5.dp, top = 20.dp)
                )
            }
        },
        floatingActionButton = {
            if (!isSearching.value) {
                FloatingActionButton(
                    onClick = {
                        showDialog.value = true
                    },
                    shape = CircleShape
                ) {
                    Image(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {

            if (!isSearching.value) {

                SearchBar(
                    query = "",
                    onQueryChange = {
                        isSearching.value = true
                    },
                    onSearch = {

                    },
                    enabled = false,
                    active = false,
                    onActiveChange = { },
                    placeholder = {
                        Text(
                            text = "Search to-do",
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp) // Icon size responsive
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp) // Add padding if needed for the search bar
                        .clickable {
                            isSearching.value = true
                        },
                    content = {}
                )

            }

            Spacer(modifier = Modifier.size(15.dp))

            if (!isSearching.value && state.items.isNullOrEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.todos_icon),
                            contentDescription = "null",
                            modifier = Modifier
                                .size(85.dp)
                                .alpha(.6f)
                        )
                        Text(
                            text = "No To-Do Found",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.alpha(.4f)
                        )
                    }
                }

            } else if (!isSearching.value) {

                val completedTodos = state.items.filter { todo -> todo.isCompleted }
                val inCompletedTodos = state.items.filter { todo -> !todo.isCompleted }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {

                    items(inCompletedTodos, key = { it.key }) { todoItem ->
                        SingleTodoItem(
                            item = TodoModel(
                                title = todoItem.title,
                                isCompleted = todoItem.isCompleted,
                                key = todoItem.key
                            ),
                            onUpdateClick = { item ->
                                currentTodoItemState.value = item
                                showUpdateDialog.value = true
                            },
                            onDeleteClick = { key ->
                                itemToDelete.value = key
                                showDeleteDialog.value = true

                            },
                            viewModel = viewModel
                        )
                    }

                    if (!completedTodos.isNullOrEmpty()) {
                        item {
                            Text(text = "Completed", fontSize = 12.sp)
                        }
                    }

                    items(completedTodos, key = { item->
                        item.key
                    }) { todoItem ->

                        SingleTodoItem(
                            item = TodoModel(
                                title = todoItem.title,
                                isCompleted = todoItem.isCompleted,
                                key = todoItem.key
                            ),
                            onUpdateClick = { item ->
                                currentTodoItemState.value = item
                                showUpdateDialog.value = true
                            },
                            onDeleteClick = { key ->
                                scope.launch {

                                    withContext(Dispatchers.Main) {
                                        itemToDelete.value = key
                                        showDeleteDialog.value = true
                                    }
                                }
                            },
                            viewModel = viewModel
                        )
                    }


                }

            }

        }

        if (showDialog.value) {
            AddTodoDialog(item = TodoModel(), showDialog = showDialog)
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (!state.error.isNullOrEmpty()) {
            Toast.makeText(context, "${state.error}", Toast.LENGTH_SHORT).show()
        }

        if (showUpdateDialog.value && currentTodoItemState.value != null) {
            AddTodoDialog(item = currentTodoItemState.value!!, showDialog = showUpdateDialog)
        }

        if (showDeleteDialog.value && itemToDelete.value != null) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = "Do you want to Delete Task?")
                },
//                text = {
//                    Text(text = "Do you want to Delete Task?")
//
//                },

                onDismissRequest = { showDeleteDialog.value = false },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog.value = false
                        }
                    ) {
                        Text(text = "No")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch(Dispatchers.Main) {
                            viewModel.deleteTodo(itemToDelete.value).collect { resultState ->
                                when (resultState) {
                                    is ResultState.Success -> {
                                        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT)
                                            .show()
                                        showDeleteDialog.value = false
                                        showLoadingIndicator.value = false
                                        itemToDelete.value = ""
                                    }

                                    is ResultState.Failure -> {
                                        Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                                        showDeleteDialog.value = false
                                        showLoadingIndicator.value = false

                                    }

                                    ResultState.Loading -> {
                                        showLoadingIndicator.value = true
                                    }
                                }
                            }
                        }
                    }) {
                        Text(text = "Yes")
                    }
                }
            )
        }

        if (isSearching.value) {

            val focusRequester = remember {
                FocusRequester()
            }

            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background.copy(alpha = .5f)),
                contentAlignment = Alignment.TopCenter
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {


                        Image(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 4.dp)
                                .clickable {
                                    isSearching.value = false
                                    searchText.value = ""
                                }
                        )


                        SearchBar(
                            query = searchText.value,
                            onQueryChange = { text ->
                                searchText.value = text
                            },
                            onSearch = {

                            },
                            active = false,
                            onActiveChange = { },
                            placeholder = {
                                Text(
                                    "Search to-do",
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = Color.Gray
                                )
                            },
                            trailingIcon = {
                                if (searchText.value.isNotEmpty()) {
                                    IconButton(onClick = { searchText.value = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Icon",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            content = {}
                        )
                    }


                    if (!searchText.value.isNullOrEmpty() && filterContacts.isNullOrEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.search_icon),
                                    contentDescription = "null",
                                    modifier = Modifier
                                        .size(85.dp)
                                        .alpha(.6f)
                                )
                                Text(
                                    text = "No To-Do Found",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.alpha(.4f)
                                )
                            }
                        }

                    } else if (searchText.value.isNullOrEmpty()) {
                        /*
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.todos_icon),
                                    contentDescription = "null",
                                    modifier = Modifier
                                        .size(85.dp)
                                        .alpha(.6f)
                                )
                                Text(
                                    text = "No To-Do Found",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.alpha(.4f)
                                )
                            }
                        }
                         */
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .wrapContentHeight()
                        ) {
                            items(filterContacts, key = { it.key }) { item ->
                                SingleTodoItem(
                                    item = TodoModel(
                                        title = item.title,
                                        isCompleted = item.isCompleted,
                                        key = item.key
                                    ),
                                    onUpdateClick = { item ->
                                        currentTodoItemState.value = item
                                        showUpdateDialog.value = true
                                    },
                                    onDeleteClick = { key ->
                                        itemToDelete.value = key
                                        showDeleteDialog.value = true

                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }


            }
        }

    }

    if (showLoadingIndicator.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }


}


@Composable
fun SingleTodoItem(
    item: TodoModel,
    viewModel: TodoViewModel,
    onUpdateClick: (TodoModel) -> Unit,
    onDeleteClick: (String) -> Unit

) {

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onDeleteClick(item.key)
                    },
                    onTap = {
                        onUpdateClick(item)
                    },
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val isCompletedTask = remember {
                mutableStateOf(item.isCompleted)
            }

            Checkbox(
                checked = isCompletedTask.value,
                onCheckedChange = { completed ->
                    isCompletedTask.value = completed

                    scope.launch(Dispatchers.Main) {
                        viewModel.updateTodo(
                            item.copy(
                                title = item.title,
                                isCompleted = isCompletedTask.value,
                                key = item.key
                            )
                        ).collect {
                            when (it) {
                                is ResultState.Success -> {
                                    if (isCompletedTask.value) {
                                        Toast.makeText(
                                            context,
                                            "Task Completed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                is ResultState.Failure -> {
                                    Toast.makeText(
                                        context,
                                        it.error.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                ResultState.Loading -> {
                                    showDialog = true
                                }

                            }
                        }
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.scale(.7f)
            )

            Text(
                text = item.title,
                fontWeight = if (isCompletedTask.value) FontWeight.Normal else FontWeight.SemiBold,
                textDecoration = if (isCompletedTask.value) TextDecoration.LineThrough else TextDecoration.None,
                color = if (isCompletedTask.value) MaterialTheme.colorScheme.onBackground.copy(alpha = .5f) else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }

    if (showDialog) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchBarClick: () -> Unit
) {

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    SearchBar(
        query = query,
        onQueryChange = { text ->
            onQueryChange(text)
        },
        onSearch = {

        },
        active = false,
        onActiveChange = { },
        placeholder = { Text("Search to-do") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Icon",
                        tint = Color.Gray
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSearchBarClick()
            }
            .focusRequester(focusRequester),
        content = {}
    )
}

