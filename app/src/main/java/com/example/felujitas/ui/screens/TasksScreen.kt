package com.example.felujitas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.felujitas.data.entity.Task
import com.example.felujitas.data.entity.TaskStatus
import com.example.felujitas.ui.viewmodel.TaskViewModel


//feladatok képernyő, szűrés és kezelési lehetőségek
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Feladatok") },
                actions = {
                    FilledIconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "Feladat Hozzáadása",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSection(
                rooms = uiState.rooms,
                selectedRoomId = uiState.selectedRoomId,
                showOnlyOpen = uiState.showOnlyOpen,
                onRoomSelected = { viewModel.setRoomFilter(it) },
                onToggleShowOnlyOpen = { viewModel.toggleShowOnlyOpen() }
            )

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tasks) { task ->
                    TaskItem(
                        task = task,
                        roomName = uiState.rooms.find { it.id == task.roomId }?.name ?: "",
                        onStatusClick = { viewModel.cycleTaskStatus(task) },
                        onClick = { selectedTask = task },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        EditTaskDialog(
            task = null,
            rooms = uiState.rooms,
            onDismiss = { showAddDialog = false },
            onConfirm = { task ->
                viewModel.addTask(task)
                showAddDialog = false
            }
        )
    }

    selectedTask?.let { task ->
        EditTaskDialog(
            task = task,
            rooms = uiState.rooms,
            onDismiss = { selectedTask = null },
            onConfirm = { updatedTask ->
                viewModel.updateTask(updatedTask)
                selectedTask = null
            }
        )
    }
}

//szűrő komponensek
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    rooms: List<com.example.felujitas.data.entity.Room>,
    selectedRoomId: Long?,
    showOnlyOpen: Boolean,
    onRoomSelected: (Long?) -> Unit,
    onToggleShowOnlyOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = rooms.find { it.id == selectedRoomId }?.name ?: "Minden Szoba",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Minden Szoba") },
                    onClick = {
                        onRoomSelected(null)
                        expanded = false
                    }
                )
                rooms.forEach { room ->
                    DropdownMenuItem(
                        text = { Text(room.name) },
                        onClick = {
                            onRoomSelected(room.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(
                "Nyitott",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
                checked = showOnlyOpen,
                onCheckedChange = { onToggleShowOnlyOpen() }
            )
        }
    }
}

//feladatok listájának megjelenitése
@Composable
fun TaskItem(
    task: Task,
    roomName: String,
    onStatusClick: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onStatusClick()
            }) {
                Icon(
                    imageVector = when (task.status) {
                        TaskStatus.OPEN -> Icons.Default.RadioButtonUnchecked
                        TaskStatus.DOING -> Icons.Default.MoreHoriz
                        TaskStatus.DONE -> Icons.Default.CheckCircle
                    },
                    contentDescription = "Állapot",
                    tint = when (task.status) {
                        TaskStatus.OPEN -> MaterialTheme.colorScheme.outline
                        TaskStatus.DOING -> MaterialTheme.colorScheme.primary
                        TaskStatus.DONE -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = roomName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                task.notes?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            task.dueDate?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//feladatok hozzáadása/szerkesztése
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task?,
    rooms: List<com.example.felujitas.data.entity.Room>,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var selectedRoom by remember {
        mutableStateOf(rooms.find { it.id == task?.roomId })
    }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: "") }
    var notes by remember { mutableStateOf(task?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Feladat hozzáadása" else "Feladat szerkesztése") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Cím") },
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedRoom?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Szoba") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text(room.name) },
                                onClick = {
                                    selectedRoom = room
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Határidő (ÉÉÉÉ-HH-NN)") },
                    placeholder = { Text("2024-12-31") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Megjegyzések") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text(
                    text = "💡 Formátum: ÉÉÉÉ-HH-NN (pl. 2024-12-31)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedRoom?.let { room ->
                        onConfirm(
                            Task(
                                id = task?.id ?: 0,
                                title = title,
                                roomId = room.id,
                                dueDate = dueDate.ifBlank { null },
                                notes = notes.ifBlank { null },
                                status = task?.status ?: TaskStatus.OPEN
                            )
                        )
                    }
                },
                enabled = title.isNotBlank() && selectedRoom != null
            ) {
                Text(if (task == null) "Hozzáadás" else "Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}