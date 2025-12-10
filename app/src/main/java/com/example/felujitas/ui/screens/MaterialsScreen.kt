package com.example.felujitas.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.felujitas.data.entity.Material
import com.example.felujitas.data.entity.Currency
import com.example.felujitas.data.entity.ReceiptType
import com.example.felujitas.ui.viewmodel.MaterialViewModel
import java.text.NumberFormat
import java.util.*

//anyagok listázása szűréssel, hozzáadás/szerkesztés lehetőséggel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreen(
    viewModel: MaterialViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Anyagok") },
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
                            "Anyag hozzáadása",
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
            RoomFilterSection(
                rooms = uiState.rooms,
                selectedRoomId = uiState.selectedRoomId,
                onRoomSelected = { viewModel.setRoomFilter(it) }
            )

            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.materials) { material ->
                    MaterialItem(
                        material = material,
                        roomName = when {
                            material.roomId == null -> "Minden Szoba"
                            else -> uiState.rooms.find { it.id == material.roomId }?.name ?: ""
                        },
                        onToggleAcquired = { viewModel.toggleAcquired(material) },
                        onClick = { selectedMaterial = material }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        EditMaterialDialog(
            rooms = uiState.rooms,
            material = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { material ->
                viewModel.addMaterial(material)
                showAddDialog = false
            }
        )
    }

    selectedMaterial?.let { material ->
        EditMaterialDialog(
            rooms = uiState.rooms,
            material = material,
            onDismiss = { selectedMaterial = null },
            onConfirm = { updatedMaterial ->
                viewModel.updateMaterial(updatedMaterial)
                selectedMaterial = null
            },
            onPhotoSelected = { uri ->
                viewModel.updateReceiptPhoto(material, uri)
            }
        )
    }
}

//szoba választás legördülő menüből
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFilterSection(
    rooms: List<com.example.felujitas.data.entity.Room>,
    selectedRoomId: Long?,
    onRoomSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = rooms.find { it.id == selectedRoomId }?.name ?: "Minden Szoba",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
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
    }
}

//anyag adatok megjelenitése
@Composable
fun MaterialItem(
    material: Material,
    roomName: String,
    onToggleAcquired: () -> Unit,
    onClick: () -> Unit
) {
    val currencySymbol = when (material.currency) {
        Currency.HUF -> "Ft"
        Currency.EUR -> "€"
    }

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
            material.receiptPhotoUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Számla",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Checkbox(
                checked = material.acquired,
                onCheckedChange = { onToggleAcquired() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = material.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (material.receiptType == ReceiptType.FULL_RECEIPT) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "📄",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = roomName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                material.quantity?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                material.store?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${material.price.toInt()} $currencySymbol",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (material.receiptType == ReceiptType.FULL_RECEIPT) {
                    Text(
                        text = "Teljes számla",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

//felugró ablak az anyag összes adatának megadásához/módosításához
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialDialog(
    rooms: List<com.example.felujitas.data.entity.Room>,
    material: Material?,
    onDismiss: () -> Unit,
    onConfirm: (Material) -> Unit,
    onPhotoSelected: ((Uri?) -> Unit)? = null
) {
    var name by remember { mutableStateOf(material?.name ?: "") }
    var selectedRoom by remember {
        mutableStateOf(
            when {
                material?.roomId == null -> null
                else -> rooms.find { it.id == material.roomId }
            }
        )
    }
    var quantity by remember { mutableStateOf(material?.quantity ?: "") }
    var price by remember { mutableStateOf(material?.price?.toString() ?: "") }
    var currency by remember { mutableStateOf(material?.currency ?: Currency.HUF) }
    var receiptType by remember { mutableStateOf(material?.receiptType ?: ReceiptType.ITEM) }
    var store by remember { mutableStateOf(material?.store ?: "") }
    var acquired by remember { mutableStateOf(material?.acquired ?: false) }
    var photoUri by remember { mutableStateOf<Uri?>(material?.receiptPhotoUri?.let { Uri.parse(it) }) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
        onPhotoSelected?.invoke(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (material == null) "Anyag hozzáadása" else "Anyag szerkesztése") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Név") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedRoom?.name ?: "Minden szoba",
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
                            DropdownMenuItem(
                                text = { Text("Minden szoba (Általános)") },
                                onClick = {
                                    selectedRoom = null
                                    expanded = false
                                }
                            )
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
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Számla típusa:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = receiptType == ReceiptType.ITEM,
                                onClick = { receiptType = ReceiptType.ITEM },
                                label = { Text("Tétel") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = receiptType == ReceiptType.FULL_RECEIPT,
                                onClick = { receiptType = ReceiptType.FULL_RECEIPT },
                                label = { Text("Teljes számla") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Text(
                            text = when (receiptType) {
                                ReceiptType.ITEM -> "💡 Egy tétel a számlából"
                                ReceiptType.FULL_RECEIPT -> "💡 Teljes számla összege"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (receiptType == ReceiptType.ITEM) {
                    item {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Mennyiség") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Ár") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Pénznem:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = currency == Currency.HUF,
                                onClick = { currency = Currency.HUF },
                                label = { Text("HUF (Ft)") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = currency == Currency.EUR,
                                onClick = { currency = Currency.EUR },
                                label = { Text("EUR (€)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = store,
                        onValueChange = { store = it },
                        label = { Text("Bolt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, "Fotó hozzáadása")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Számla fotó")
                    }
                }

                photoUri?.let { uri ->
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Számla",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = {
                                    photoUri = null
                                    onPhotoSelected?.invoke(null)
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, "Fotó törlése")
                            }
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = acquired,
                            onCheckedChange = { acquired = it }
                        )
                        Text("Beszerzett")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Material(
                            id = material?.id ?: 0,
                            name = name,
                            roomId = selectedRoom?.id,  // NULL ha "minden szooba"
                            quantity = if (receiptType == ReceiptType.ITEM) quantity.ifBlank { null } else null,
                            price = price.toDoubleOrNull() ?: 0.0,
                            currency = currency,
                            receiptType = receiptType,
                            store = store.ifBlank { null },
                            receiptPhotoUri = photoUri?.toString(),
                            acquired = acquired
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}