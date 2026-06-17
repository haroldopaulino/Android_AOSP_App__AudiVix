# AudiVix Android & AOSP Streaming App

**Kotlin Android audio/video streaming app for phone and smart watch, built with Jetpack Compose, MVVM-style state management, Retrofit, Koin, Room, Media3 ExoPlayer, background media controls, caching, themes, and private backend API integration.**

AudiVix is a cross-device Android media app designed to stream audio and video from a private backend service to both a phone app and a smart watch app. The project demonstrates modern Android engineering across two form factors: a full-featured phone experience and a compact wearable experience.

The app supports audio/video playback, fullscreen and mini video player behavior, background media controls, remote artwork/thumbnail loading, backend media catalog integration, playback state handling, and modern Kotlin/Compose architecture.

<img width="1170" height="659" alt="audivix_1" src="https://github.com/user-attachments/assets/280be71d-d651-4fb5-bca3-5f10d9c2446e" />

---

<img width="1170" height="659" alt="audivix_2" src="https://github.com/user-attachments/assets/64272262-bc8c-42f7-99e3-212340abf841" />

---

## Why This Project Matters

AudiVix is more than a simple media player. It is a complete Android streaming app architecture that combines UI, networking, media playback, dependency injection, state management, caching, phone UX, wearable UX, and backend integration.

This repository demonstrates:

- Kotlin Android development
- Multi-module Android project structure
- Phone and smart watch app targets
- Jetpack Compose UI
- Wearable-friendly UI design
- MVVM-style architecture
- Media3 ExoPlayer playback
- Audio and video streaming
- Fullscreen and mini player behavior
- Background media controls
- Retrofit API integration
- Koin dependency injection
- Room local persistence support
- Coil image loading
- Coroutine-based async behavior
- RxJava interoperability
- Private backend API integration
- Modern Android SDK and Java 17 toolchain setup

---

## Project Overview

AudiVix is designed around a private media backend. The app loads a media catalog from the backend, displays audio and video items, and plays selected content using Media3.

The project includes two Android application modules:

| Module | Purpose |
|---|---|
| `phone` | Full Android phone streaming experience |
| `wearable` | AOSP Smart watch streaming/control experience |

The repository is configured as a multi-module Gradle project with `:phone` and `:wearable` modules.

---

## Main Features

### Audio and Video Streaming

AudiVix supports both audio and video media playback. It is designed for streaming from backend media endpoints rather than only playing local files.

### Media3 ExoPlayer Integration

The project uses AndroidX Media3, including ExoPlayer, Media3 UI, and Media3 Session dependencies. This gives the app a modern playback foundation for media rendering, playback state, and background control integration.

### Phone App Experience

The phone module provides the main full-screen Android app experience with Compose UI and Material 3.

The phone app is designed for:

- Browsing media
- Playing audio
- Playing video
- Showing artwork and thumbnails
- Managing playback controls
- Supporting fullscreen video behavior
- Supporting mini player behavior
- Supporting background media controls
- Applying app themes

### AOSP Smart Watch Experience

The wearable module provides a dedicated smart watch target with Compose and Wear Compose dependencies.

The wearable app is designed around a smaller screen and a compact interaction model, making the project relevant to Android wearable, AOSP, and connected-device roles.

### Private Backend API Integration

AudiVix integrates with a private backend API using Retrofit. The backend provides media metadata and stream URLs used by the app.

This demonstrates practical Android client/server engineering:

```text
Backend media catalog
      ↓
Retrofit API layer
      ↓
Repository/data layer
      ↓
ViewModel state
      ↓
Compose UI
      ↓
Media3 playback
```

### Dependency Injection with Koin

The project uses Koin for dependency injection. This helps separate app setup, API clients, repositories, ViewModels, and playback-related dependencies.

### Local Persistence and Caching

The README identifies Room and caching as part of the project. This supports a more production-like app flow where app data, preferences, media ordering, or cached metadata can survive app restarts.

### Remote Images with Coil

The project includes Coil 3 dependencies for Compose image loading and OkHttp-backed network image support. This is useful for displaying remote artwork, thumbnails, and media images.

### Modern Async Stack

The app uses Kotlin coroutines and includes RxJava support for interoperability with reactive APIs or async stream patterns.

---

## Technical Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Wearable UI | Wear Compose |
| Architecture | MVVM-style state management |
| Media playback | AndroidX Media3 / ExoPlayer |
| Media session | AndroidX Media3 Session |
| Networking | Retrofit |
| Serialization | Moshi / Gson depending on module |
| Dependency injection | Koin |
| Persistence | Room support identified in README |
| Image loading | Coil 3 |
| Async | Kotlin Coroutines |
| Reactive support | RxJava 3 |
| Build system | Gradle Kotlin DSL |
| Java toolchain | 17 |
| Compile SDK | 36 |
| Target SDK | 36 |
| Minimum SDK | 26 |
| License | GPL-3.0 |

---

## Repository Structure

```text
Android_AOSP_App__AudiVix/
├── phone/
│   ├── src/
│   └── build.gradle.kts
├── wearable/
│   ├── src/main/
│   └── build.gradle.kts
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── LICENSE
```

---

## Module Configuration

### Phone Module

The phone app uses:

- Namespace: `com.harold.audivix`
- Application ID: `com.harold.audivix`
- Minimum SDK: 26
- Target SDK: 36
- Compile SDK: 36
- Version name: `1.0.31`
- Java/Kotlin toolchain: 17

Important phone dependencies include:

- AndroidX Core
- Activity Compose
- Compose BOM
- Material 3
- Navigation Compose
- Lifecycle runtime/viewmodel Compose
- Media3 ExoPlayer
- Media3 UI
- Media3 Session
- Retrofit 3
- Moshi converter
- OkHttp logging interceptor
- Coil Compose
- Koin
- Kotlin Coroutines
- RxJava 3
- MockK and MockWebServer for testing support

### Wearable Module AOSP

The wearable app uses:

- Namespace: `com.harold.audivix.wear`
- Application ID: `com.harold.audivix.wear`
- Minimum SDK: 26
- Target SDK: 36
- Compile SDK: 36
- Version name: `1.0.31`
- Java/Kotlin toolchain: 17

Important wearable dependencies include:

- Compose UI
- Compose Foundation
- Material 3
- Wear Compose Material 3
- Wear Compose Foundation
- Navigation Compose
- Lifecycle runtime/viewmodel Compose
- Media3 ExoPlayer
- Media3 UI
- Media3 Session
- Media3 Common
- Retrofit 3
- Gson converter
- Coil Compose
- Koin
- Kotlin Coroutines
- RxJava 3

---

## Architecture

AudiVix follows a modern Android architecture direction:

```text
Compose UI
   ↓
ViewModel state
   ↓
Repository / use-case layer
   ↓
Retrofit backend API
   ↓
Media metadata and stream URLs
   ↓
Media3 player/session
   ↓
Phone and smart watch playback UI
```

The project is structured so the phone and AOSP wearable modules can have separate UI experiences while sharing the same product concept and backend media model.

---

## Media Playback Flow

A typical playback flow looks like this:

```text
User opens AudiVix
      ↓
App loads media list from backend
      ↓
User selects audio or video
      ↓
App prepares Media3 ExoPlayer
      ↓
Playback starts
      ↓
UI updates playback controls and progress
      ↓
Background controls remain available
      ↓
Video can move between mini and fullscreen experiences
```

This is a realistic production media-app workflow.

---

## Backend Integration Flow

AudiVix is designed to work with a private backend API that provides media metadata and streaming endpoints.

A typical backend data flow is:

```text
Private backend API
      ↓
Media catalog response
      ↓
Retrofit network layer
      ↓
Repository parsing and state update
      ↓
Compose media list
      ↓
Media3 stream playback
```

This demonstrates full client-side media integration rather than only local playback.

---

## Phone and AOSP Smart Watch Product Value

AudiVix is valuable as a portfolio project because it targets both phone and wearable experiences.

### Phone Value

The phone app shows full media browsing and playback behavior, including larger screen layouts, video presentation, background controls, and theme support.

### AOSP Smart Watch Value

The smart watch app demonstrates compact UI thinking, wearable navigation, and media interaction on small screens. This is especially relevant to AOSP, Wear OS, embedded-adjacent Android, and connected-device teams.

---

## Skills Demonstrated

This repository demonstrates several Android and mobile engineering skills:

- Kotlin Android development
- Jetpack Compose UI development
- Wear Compose development
- Multi-module Android project setup
- Media3 ExoPlayer integration
- Media session/background playback architecture
- Audio/video playback handling
- Video UI state and fullscreen behavior
- Retrofit API integration
- Dependency injection with Koin
- Local persistence/caching design
- Remote image loading with Coil
- Coroutine-based asynchronous programming
- RxJava interoperability
- Modern Gradle Kotlin DSL configuration
- Java 17 Android toolchain setup
- Phone and smart watch product architecture
- Backend-driven media app design

---

## AOSP and Embedded-Adjacent Relevance

Although AudiVix is an Android application, it is especially relevant to AOSP and connected-device work.

Media apps on AOSP-based devices often require knowledge of:

- Android application lifecycle
- Media playback stack
- Media sessions and notification controls
- Audio/video rendering
- Background playback behavior
- Network streaming constraints
- Small-screen or wearable UX
- Device-specific UI/performance considerations
- Kotlin/Compose modernization
- Integration with private backend services

AudiVix demonstrates many of these skills in a practical app.

---

## How to Build

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync using the included wrapper.
4. Select the `phone` module to run the phone app.
5. Select the `wearable` module to run the smart watch app.
6. Configure the private backend API endpoint if needed.
7. Build and run on an Android phone, Android emulator, Wear OS device, or wearable emulator.
8. Test media list loading, audio playback, video playback, and background controls.

---

## Owner

by Harold Paulino

---

## License

This project is licensed under the GPL-3.0 license.
