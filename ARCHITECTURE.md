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
| **Model** | *(da aggiungere)* | Repository, sorgenti dati esterne. |

Il ViewModel non ha riferimenti alla View: comunica solo esponendo uno `StateFlow`.

## State Management: StateFlow

Lo stato di ogni schermata è modellato come un **data class** (`*UiState`) che contiene tutto ciò che la UI deve mostrare. Il ViewModel espone questo stato tramite `StateFlow`:

```kotlin
// Privato e mutabile — solo il ViewModel può modificarlo
private val _uiState = MutableStateFlow(HomeUiState())

// Pubblico e in sola lettura — la UI può solo osservarlo
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
```

La UI osserva il flusso con `collectAsStateWithLifecycle()`, che smette automaticamente di ascoltare quando la schermata non è visibile (risparmio batteria).

## Navigazione: Navigation Compose

La navigazione tra schermate usa **Navigation Compose** (`androidx.navigation.compose`). Tutte le destinazioni sono definite come costanti in `ui/navigation/AppNavigation.kt` nell'oggetto `Routes`, per evitare stringhe magiche sparse nel codice.

## Pattern: Single Activity

L'app ha una sola `Activity` (`MainActivity`). Compose e Navigation gestiscono internamente tutto il routing tra schermate, senza creare nuove Activity per ogni schermata (pattern obsoleto).

## Build

| Voce | Valore |
|---|---|
| Android Gradle Plugin | 9.2.1 |
| Kotlin | 2.1.0 |
| Compose BOM | 2024.12.01 |
| Min SDK | 33 (Android 13) |
| Target/Compile SDK | 36.1 (Android 16 QPR1) |

`targetSdk = compileSdk` mantiene i due valori automaticamente allineati.

Il plugin `kotlin.android` non viene applicato separatamente perché AGP 9.x integra già il supporto Kotlin internamente. Il plugin `kotlin.plugin.compose` è necessario per il compilatore Compose (introdotto come plugin standalone da Kotlin 2.0).

## Struttura cartelle

```
app/src/main/java/dev/enokk/schedato/
├── MainActivity.kt
└── ui/
    ├── theme/
    │   ├── Theme.kt
    │   └── Type.kt
    ├── navigation/
    │   └── AppNavigation.kt       ← Routes + NavHost
    └── screens/
        └── <nome_schermata>/
            ├── <Nome>Screen.kt    ← @Composable, solo UI
            └── <Nome>ViewModel.kt ← logica + UiState
```

Ogni nuova schermata segue questo stesso schema: una cartella dedicata con un file Screen e un file ViewModel.

## Convenzioni

- Package name: `dev.enokk.schedato`
- `namespace` e `applicationId` in `build.gradle.kts` devono coincidere col package
- Temi XML in `res/values/themes.xml` sono ridotti al minimo (parent `android:Theme.Material.Light.NoActionBar`): il look reale è interamente gestito da Compose
