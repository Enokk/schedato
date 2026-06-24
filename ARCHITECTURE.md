# Scelte architetturali — Schedato

## UI: Jetpack Compose

Invece dei tradizionali layout XML, l'interfaccia è descritta interamente in codice Kotlin con **Jetpack Compose** (approccio dichiarativo, analogo a React/SwiftUI). È la direzione ufficiale di Google dal 2021 e quella raccomandata per tutti i nuovi progetti Android.

Ogni schermata è una funzione annotata con `@Composable` che descrive *come appare* lo stato corrente, non *come si aggiorna*.

## Design System: Material 3

I componenti UI (`Button`, `Text`, `Scaffold`, ecc.) provengono da **Material Design 3** (`androidx.compose.material3`), l'ultima versione del design system di Google. Il tema è definito in `ui/theme/Theme.kt`.

## Architettura: MVVM

Ogni schermata è composta da tre elementi:

| Livello | Classe | Responsabilità |
|---|---|---|
| **View** | `*Screen.kt` | Disegna lo stato. Non contiene logica. |
| **ViewModel** | `*ViewModel.kt` | Contiene logica e stato UI. Sopravvive alla rotazione dello schermo. |
| **Model** | `*Repository.kt` + Room | Repository, sorgenti dati esterne. |

Il ViewModel non ha riferimenti alla View: comunica solo esponendo uno `StateFlow`.

## State Management: StateFlow + combine

Lo stato di ogni schermata è modellato come un **data class** (`*UiState`) che contiene tutto ciò che la UI deve mostrare. Il ViewModel espone questo stato tramite `StateFlow`.

Quando lo stato deriva da più sorgenti (es. lista dal DB + visibilità di un dialog), si usa `combine` per fonderle in un unico flusso:

```kotlin
val uiState = combine(repository.characters, _showDialog) { characters, showDialog ->
    HomeUiState(characters = characters, showNewCharacterDialog = showDialog)
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = HomeUiState()
)
```

`stateIn` con `WhileSubscribed(5_000)` mantiene il flusso attivo 5 secondi dopo l'ultima sottoscrizione, gestendo le rotazioni schermo senza rifare la query al DB.

La UI osserva con `collectAsStateWithLifecycle()`, che smette automaticamente di ascoltare quando la schermata non è visibile (risparmio batteria).

**Stato drag (solo UI):** lo stato del drag-to-delete non passa per il ViewModel perché è puramente visivo e non sopravvive alla rotazione. Viene gestito con `remember { mutableStateOf(...) }` locale alla composable.

## Navigazione: Navigation Compose

La navigazione tra schermate usa **Navigation Compose** (`androidx.navigation.compose`). Tutte le destinazioni sono definite come costanti in `ui/navigation/AppNavigation.kt` nell'oggetto `Routes`, per evitare stringhe magiche sparse nel codice.

## Pattern: Single Activity

L'app ha una sola `Activity` (`MainActivity`). Compose e Navigation gestiscono internamente tutto il routing tra schermate, senza creare nuove Activity per ogni schermata (pattern obsoleto).

## Persistenza: Room + KSP

I dati vengono salvati in un database SQLite locale tramite **Room**. Il layer è così strutturato:

- `*Entity.kt` — classe annotata `@Entity`, mappa una tabella. Separata dal domain model per disaccoppiare lo schema DB dal resto.
- `*Dao.kt` — interfaccia annotata `@Dao` con le query (restituisce `Flow` per reattività automatica).
- `AppDatabase.kt` — singleton `RoomDatabase`, creato in `SchedatoApplication`.
- `*Repository.kt` — unico punto di accesso ai dati per i ViewModel; converte le entity nel domain model.

**KSP** (Kotlin Symbol Processor) genera il codice boilerplate di Room a compile-time leggendo le annotazioni. Sostituisce il vecchio `kapt`.

**Nota AGP 9.x:** è richiesta la property `android.disallowKotlinSourceSets=false` in `gradle.properties` perché KSP registra le sorgenti generate via `kotlin.sourceSets`, API non ancora supportata nativamente da AGP 9.x.

## Preferenze utente: DataStore

Le impostazioni dell'utente (es. tema) vengono salvate tramite **DataStore Preferences**, la soluzione moderna di Jetpack in sostituzione di `SharedPreferences`.

- `UserPreferencesRepository` espone le preferenze come `Flow` e le scrive tramite `suspend fun`.
- L'istanza del DataStore è creata come extension property su `Context` (pattern raccomandato da Google per garantire una sola istanza per processo).
- Le preferenze sono lette in `MainActivity` e passate a `SchedatoTheme`, così il tema è applicato globalmente prima che qualsiasi schermata venga composta.

## Tema

Il tema dell'app è controllato dall'enum `AppTheme` (`LIGHT`, `DARK`, `SYSTEM`). `SchedatoTheme` riceve il valore corrente da `MainActivity`, che lo osserva dal repository tramite `collectAsStateWithLifecycle`. `SYSTEM` delega a `isSystemInDarkTheme()`, gli altri due forzano la palette corrispondente indipendentemente dal sistema.

## Build

| Voce | Valore |
|---|---|
| Android Gradle Plugin | 9.2.1 |
| Kotlin | 2.1.0 |
| KSP | 2.1.0-1.0.29 |
| Compose BOM | 2024.12.01 |
| Room | 2.7.1 |
| DataStore | 1.1.1 |
| Min SDK | 33 (Android 13) |
| Target/Compile SDK | 36.1 (Android 16 QPR1) |

`targetSdk = compileSdk` mantiene i due valori automaticamente allineati.

Il plugin `kotlin.android` non viene applicato separatamente perché AGP 9.x integra già il supporto Kotlin internamente. Il plugin `kotlin.plugin.compose` è necessario per il compilatore Compose (introdotto come plugin standalone da Kotlin 2.0).

## Struttura cartelle

```
app/src/main/java/dev/enokk/schedato/
├── SchedatoApplication.kt         ← Application, espone db e repository
├── MainActivity.kt
├── model/
│   ├── Character.kt               ← domain model
│   └── AppTheme.kt                ← enum per la scelta del tema
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── CharacterDao.kt
│   │   └── CharacterEntity.kt     ← entity Room + funzioni di mapping
│   └── repository/
│       ├── CharacterRepository.kt
│       └── UserPreferencesRepository.kt ← DataStore per le preferenze
└── ui/
    ├── theme/
    │   ├── Theme.kt
    │   └── Type.kt
    ├── navigation/
    │   └── AppNavigation.kt       ← Routes + NavHost + wiring ViewModel factory
    └── screens/
        └── <nome_schermata>/
            ├── <Nome>Screen.kt    ← @Composable, solo UI
            └── <Nome>ViewModel.kt ← logica + UiState
```

Ogni nuova schermata segue questo schema. Il ViewModel riceve il repository tramite factory (`viewModelFactory { initializer { ... } }`); la factory viene costruita in `AppNavigation` leggendo il repository da `SchedatoApplication`.

## Convenzioni

- Package name: `dev.enokk.schedato`
- `namespace` e `applicationId` in `build.gradle.kts` devono coincidere col package
- Temi XML in `res/values/themes.xml` sono ridotti al minimo (parent `android:Theme.Material.Light.NoActionBar`): il look reale è interamente gestito da Compose
