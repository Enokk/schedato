package dev.enokk.schedato.ui.screens.characterdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Image
import dev.enokk.schedato.R
import dev.enokk.schedato.model.AppClass
import dev.enokk.schedato.model.AppRace
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private val raceOptions: List<Pair<AppRace, Int>> = AppRace.entries.map { it to it.labelRes }
private val classOptions: List<Pair<AppClass, Int>> = AppClass.entries.map { it to it.labelRes }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onNameChange: (String) -> Unit,
    onRaceChange: (AppRace?) -> Unit,
    onClassChange: (AppClass?) -> Unit,
    onLevelChange: (Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onSaved: () -> Unit = onBack
) {
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.originalName ?: stringResource(R.string.home_new_character_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            if (uiState.isCreateMode) {
                uiState.race?.let { race ->
                    SelectionBadge(
                        drawableRes = race.drawableRes,
                        label = stringResource(R.string.detail_label_race),
                        value = stringResource(race.labelRes),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            } else {
                PickerField(
                    selectedOption = uiState.race,
                    options = raceOptions,
                    labelRes = R.string.detail_label_race,
                    onOptionSelected = onRaceChange
                )
            }
            if (uiState.isCreateMode) {
                uiState.characterClass?.let { cls ->
                    SelectionBadge(
                        drawableRes = cls.drawableRes,
                        label = stringResource(R.string.detail_label_class),
                        value = stringResource(cls.labelRes),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                PickerField(
                    selectedOption = uiState.characterClass,
                    options = classOptions,
                    labelRes = R.string.detail_label_class,
                    onOptionSelected = onClassChange
                )
            }
            LevelStepper(
                level = uiState.level,
                onLevelChange = onLevelChange
            )
            Button(
                onClick = onSave,
                enabled = uiState.name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.detail_save))
            }
        }
    }
}

@Composable
private fun <T> PickerField(
    selectedOption: T?,
    options: List<Pair<T, Int>>,
    labelRes: Int,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedLabel = selectedOption?.let { opt ->
        stringResource(options.first { it.first == opt }.second)
    } ?: ""

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(labelRes)) },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )
        // Overlay trasparente per catturare i click senza disabilitare il TextField
        Box(modifier = Modifier.matchParentSize().clickable { showDialog = true })
    }

    if (showDialog) {
        val listState = rememberLazyListState()
        val selectedIndex = selectedOption?.let { opt ->
            options.indexOfFirst { it.first == opt }
        } ?: -1

        // Scroll all'elemento selezionato solo dopo che la lista è misurata
        LaunchedEffect(Unit) {
            snapshotFlow { listState.layoutInfo.totalItemsCount }
                .filter { it > 0 }
                .first()
            if (selectedIndex >= 0) listState.scrollToItem(selectedIndex)
        }

        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = stringResource(labelRes),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                    Box(modifier = Modifier.heightIn(max = 380.dp)) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 14.dp)
                        ) {
                            items(options) { (option, optLabelRes) ->
                                val isSelected = option == selectedOption
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = stringResource(optLabelRes),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    trailingContent = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else null,
                                    modifier = Modifier.clickable {
                                        onOptionSelected(option)
                                        showDialog = false
                                    }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                        PickerScrollbar(
                            state = listState,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .fillMaxHeight()
                                .width(6.dp)
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionBadge(
    drawableRes: Int,
    label: String,
    value: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Surface(color = containerColor, shape = MaterialTheme.shapes.medium) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = contentColor)
                Text(text = value, style = MaterialTheme.typography.titleMedium, color = contentColor)
            }
        }
    }
}

// state.layoutInfo/firstVisibleItemIndex cambiano ad ogni frame di scroll: letti dentro
// la draw scope di Canvas (non nel corpo del composable) causano solo redraw, non recomposition.
@Composable
private fun PickerScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val thumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)

    Canvas(modifier = modifier) {
        val totalItems = state.layoutInfo.totalItemsCount
        val visibleItems = state.layoutInfo.visibleItemsInfo
        if (totalItems == 0 || visibleItems.isEmpty()) return@Canvas
        val visibleFraction = visibleItems.size.toFloat() / totalItems
        if (visibleFraction >= 1f) return@Canvas

        val scrollFraction = state.firstVisibleItemIndex.toFloat() /
            (totalItems - visibleItems.size).coerceAtLeast(1)

        drawRoundRect(color = trackColor, cornerRadius = CornerRadius(size.width / 2))
        val thumbHeight = (size.height * visibleFraction).coerceAtLeast(40f)
        val thumbTop = (scrollFraction * (size.height - thumbHeight))
            .coerceIn(0f, size.height - thumbHeight)
        drawRoundRect(
            color = thumbColor,
            topLeft = Offset(0f, thumbTop),
            size = Size(size.width, thumbHeight),
            cornerRadius = CornerRadius(size.width / 2)
        )
    }
}

@Composable
private fun LevelStepper(
    level: Int,
    onLevelChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.detail_label_level),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onLevelChange(level - 1) },
                enabled = level > 1
            ) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(
                onClick = { onLevelChange(level + 1) },
                enabled = level < 20
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}
