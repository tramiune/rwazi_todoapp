# Rwazi Notes - Premium Note-Taking App

A robust, offline-first Android note-taking application designed with Clean Architecture, emphasizing performance, responsive UI, and seamless cloud synchronization.

## 🚀 Features
- **Responsive Staggered Grid Layout**: Dynamically adapts from single-column on phones to 2-3 columns on tablets and landscape modes.
- **Offline-First Architecture**: Powered by Room Database. Fully functional without an internet connection.
- **Real-Time Cloud Sync (Bonus)**: Background synchronization with Firebase Firestore using Kotlin Flows & WorkManager.
- **Infinite Scrolling**: Extremely smooth list pagination utilizing Google's `Paging 3` library.
- **Advanced Search**: Debounced Full-Text Search (FTS) to ensure fluid typing with zero UI blocking.
- **Dynamic Theming**: 15 custom-curated color palettes natively injected via Jetpack DataStore preferences.
- **Premium UX/UI**: Immersive edge-to-edge text justification (`inter_word`) with custom TopBars, clean typography, and micro-interactions.

## 🛠 Tech Stack
- **Language**: Kotlin 
- **Architecture**: MVVM + Clean Architecture (Data, Domain, Presentation Layers). Uni-directional Data Flow (UDF).
- **Concurrency**: Kotlin Coroutines & Flow (StateFlow, SharedFlow).
- **Dependency Injection**: Dagger Hilt.
- **Local Storage**: Room Database (with FTS4).
- **Preferences**: Jetpack DataStore.
- **Cloud Backend**: Firebase Authentication & Cloud Firestore.
- **Background Processing**: WorkManager constraint-based workers.

## 🏗 Setup & Instructions
1. **Prerequisites**: Android Studio Iguana (or newer).
2. **Setup**: The project uses standard Gradle wrapper dependencies. Simply open the root folder in Android Studio and wait for purely automated sync.
3. **Execution**: Press `Run (Shift + F10)` targeting any Emulator or physical device (API 24+).
4. **Testing Sync**:
   - Sign in using an active Google Account.
   - Disconnect the internet (Airplane mode), add a few notes.
   - Reconnect the internet. The WorkManager will silently push local data to Firestore!

## ✨ Highlights
- Eliminating Fat-Fragments: Logic strictly isolated in ViewModels.
- Avoided memory leaks via Lifecycle-aware Flow collectors (`collectInStarted`).
- Custom `IErrorManager` for centralized error piping instead of scattered Try/Catch.
