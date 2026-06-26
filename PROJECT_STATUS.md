# Schedato — Stato del progetto

## Cos'è l'app

**Schedato** è un'app Android per gestire le schede personaggio di **Dungeons & Dragons 5a edizione**, in sostituzione delle schede cartacee.

L'obiettivo finale è avere un'app mobile che consenta di creare, consultare e aggiornare tutti i dati di un personaggio D&D 5e — statistiche, incantesimi, equipaggiamento, note — in modo più pratico e rapido rispetto alla carta.

---

## Implementato

### Schermata principale — lista personaggi

- Griglia a 2 colonne di card personaggio (larghezza fissa, centrate)
- Stato vuoto con messaggio esplicativo quando non ci sono personaggi
- FAB "+" per navigare alla schermata di creazione personaggio
- Tap su una card → navigazione alla schermata di modifica del personaggio
- Card con: avatar (icona), nome, razza e classe localizzate, livello

### Eliminazione personaggio

- Long press su una card → attiva la modalità drag
- Card originale diventa trasparente (ghost)
- Avatar circolare segue il dito durante il trascinamento
- Cestino animato appare dal basso (slide-up); cambia colore e dimensione quando l'avatar ci è sopra
- Rilascio sul cestino → dialog di conferma prima di eliminare
- Rilascio altrove → annulla il drag senza conseguenze

### Navigazione e impostazioni

- Barra superiore con titolo schermata e hamburger menu
- Drawer di navigazione che scivola da sinistra (scrim semi-trasparente)
- Schermata Impostazioni con selettore tema: Chiaro / Scuro / Sistema
- La scelta del tema persiste tra le sessioni tramite DataStore Preferences
- Il tema viene applicato globalmente prima del rendering di qualsiasi schermata
- Selettore lingua in Impostazioni: Sistema / Italiano / English
- La lingua persiste automaticamente tramite `LocaleManager` (Android 13+); nessun DataStore necessario
- "Sistema" usa la lingua del device con fallback su inglese

### Schermata dettaglio personaggio (crea / modifica)

- Stessa schermata usata sia per la creazione che per la modifica (modalità distinte)
- Campo nome con validazione (salvataggio disabilitato se vuoto)
- Selezione razza: dialog con lista scrollabile, scrollbar visiva, indicatore voce selezionata, scroll automatico all'elemento corrente; 15 voci (razze + sottorazze PHB 5e)
- Selezione classe: stesso componente picker, 12 classi PHB 5e
- Stepper livello (+/−, range 1–20)
- Salvataggio tramite `@Upsert`: crea il personaggio se nuovo, aggiorna se esistente

### Persistenza

- I personaggi vengono salvati su database SQLite locale tramite Room
- I dati sopravvivono alla chiusura e al riavvio dell'app
- La lista si aggiorna reattivamente tramite `Flow`: nessun polling manuale

---

## Da implementare

### Scheda personaggio completa
Quando si tocca una card dalla lista, si dovrebbe aprire la scheda completa del personaggio con (almeno):

- **Caratteristiche base:** Forza, Destrezza, Costituzione, Intelligenza, Saggezza, Carisma — con modificatori calcolati automaticamente
- **Informazioni generali:** razza, classe, sottoclasse, background, allineamento, livello, punti esperienza
- **Combattimento:** Punti Ferita (massimi, attuali, temporanei), Classe Armatura, Iniziativa, Velocità, Tiri Salvezza, dadi vita
- **Competenze e abilità:** lista con indicatore competenza/maestria
- **Attacchi e incantesimi:** armi equipaggiate, slot incantesimo per livello, lista incantesimi noti/preparati
- **Equipaggiamento:** inventario con peso e monete
- **Tratti e caratteristiche:** tratti di personalità, ideali, legami, difetti, caratteristiche razziali e di classe
- **Note libere**

### Funzionalità generali ancora mancanti
- Avanzamento di livello con conseguenze meccaniche (HP, competenze, ecc.)
- Eventuale import/export della scheda (JSON o PDF)
