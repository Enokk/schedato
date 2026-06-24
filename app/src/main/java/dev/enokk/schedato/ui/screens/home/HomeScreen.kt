package dev.enokk.schedato.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.enokk.schedato.model.Character
import kotlin.math.roundToInt

// Stato locale del drag: tutto quello che serve per renderizzare la card "volante"
// e rilevare se è sopra il cestino. Non va nel ViewModel perché è puro stato UI.
private data class DragState(
    val character: Character,
    val touchPoint: Offset  // posizione assoluta del dito (centra l'avatar e fa l'hit-test sul cestino)
)

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNewCharacterClick: () -> Unit,
    onNewCharacterConfirm: (String) -> Unit,
    onDialogDismiss: () -> Unit,
    onDeleteRequested: (Character) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDeleteDismissed: () -> Unit
) {
    var dragState by remember { mutableStateOf<DragState?>(null) }
    var trashBounds by remember { mutableStateOf(Rect.Zero) }

    // derivedStateOf ricalcola solo quando dragState o trashBounds cambiano,
    // evitando ricomposizioni inutili su ogni frame del drag
    val isOverTrash by remember {
        derivedStateOf {
            val state = dragState ?: return@derivedStateOf false
            trashBounds != Rect.Zero && trashBounds.contains(state.touchPoint)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            floatingActionButton = {
                if (dragState == null) {
                    FloatingActionButton(onClick = onNewCharacterClick) {
                        Icon(Icons.Default.Add, contentDescription = "Nuovo personaggio")
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.characters.isEmpty()) {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.characters, key = { it.id }) { character ->
                            DraggableCharacterCard(
                                character = character,
                                isBeingDragged = dragState?.character?.id == character.id,
                                onDragStart = { touchPoint ->
                                    dragState = DragState(character, touchPoint)
                                },
                                onDrag = { delta ->
                                    dragState = dragState?.let { it.copy(touchPoint = it.touchPoint + delta) }
                                },
                                onDragEnd = {
                                    if (isOverTrash) dragState?.character?.let(onDeleteRequested)
                                    dragState = null
                                },
                                onDragCancel = { dragState = null }
                            )
                        }
                    }
                }
            }
        }

        // Cestino: appare dal basso quando inizia il drag
        AnimatedVisibility(
            visible = dragState != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            TrashZone(
                isActive = isOverTrash,
                modifier = Modifier.onGloballyPositioned { coords ->
                    val topLeft = coords.positionInWindow()
                    trashBounds = Rect(
                        offset = topLeft,
                        size = Size(
                            coords.size.width.toFloat(),
                            coords.size.height.toFloat()
                        )
                    )
                }
            )
        }

        // Avatar circolare che segue il dito — piccolo per non oscurare il cestino
        dragState?.let { state ->
            val avatarSize = 72.dp

            Surface(
                modifier = Modifier
                    .size(avatarSize)
                    .offset {
                        val avatarSizePx = avatarSize.toPx()
                        IntOffset(
                            (state.touchPoint.x - avatarSizePx / 2).roundToInt(),
                            (state.touchPoint.y - avatarSizePx / 2).roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = 0.95f
                        scaleX = 1.1f
                        scaleY = 1.1f
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    // Dialog di conferma eliminazione
    uiState.characterPendingDelete?.let { character ->
        AlertDialog(
            onDismissRequest = onDeleteDismissed,
            title = { Text("Elimina personaggio") },
            text = { Text("Vuoi eliminare «${character.name}»? L'operazione è irreversibile.") },
            confirmButton = {
                TextButton(onClick = onDeleteConfirmed) {
                    Text("Elimina", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismissed) { Text("Annulla") }
            }
        )
    }

    if (uiState.showNewCharacterDialog) {
        NewCharacterDialog(
            onConfirm = onNewCharacterConfirm,
            onDismiss = onDialogDismiss
        )
    }
}

// Wrapper che aggiunge long-press + drag a qualsiasi CharacterCard.
// Registra la posizione della card sullo schermo e passa le coordinate al genitore.
@Composable
private fun DraggableCharacterCard(
    character: Character,
    isBeingDragged: Boolean,
    onDragStart: (touchPoint: Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    var cardTopLeft by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                cardTopLeft = coords.positionInWindow()
            }
            .pointerInput(character.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { touchInCard ->
                        onDragStart(cardTopLeft + touchInCard)
                    },
                    onDrag = { change, delta ->
                        change.consume()
                        onDrag(delta)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .graphicsLayer { alpha = if (isBeingDragged) 0.3f else 1f }
    ) {
        CharacterCard(character = character, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TrashZone(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "trashBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "trashContent"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isActive) 36.dp else 26.dp,
        label = "trashIconSize"
    )

    Surface(
        modifier = modifier.size(width = 140.dp, height = 84.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = "Elimina",
                color = contentColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CharacterCard(
    character: Character,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            val twoLineHeight = with(LocalDensity.current) {
                (MaterialTheme.typography.titleMedium.lineHeight.toPx() * 2).toDp()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = twoLineHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${character.race} • ${character.characterClass}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AssistChip(
                onClick = {},
                label = { Text("Liv. ${character.level}") }
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Nessun personaggio", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Tocca + per crearne uno",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NewCharacterDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo personaggio") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Crea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}
