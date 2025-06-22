# DrawUnlock

DrawUnlock is an experimental Android screen lock application that allows users to unlock their device by drawing a specific object. It uses a lightweight image classification model (TFLite) to identify the drawing and verify whether it matches a pre-set keyword.

## Features

- Drawing-based screen unlock mechanism
- AI-powered sketch recognition using TensorFlow Lite
- Fully offline and privacy-respecting
- Retry counter with fallback PIN option for added security

## How It Works

1. A random keyword (e.g., "apple") is selected and briefly displayed.
2. The user must draw the object that matches the keyword.
3. The drawing is passed to a locally embedded model for classification.
4. If the prediction matches the keyword with sufficient confidence, the screen unlocks.
5. Otherwise, the user is prompted to retry. After multiple failures, a fallback PIN is requested.

## Project Structure

DrawUnlock/
├── app/
│ ├── src/
│ │ └── main/
│ │ ├── java/com/example/drawunlock/
│ │ │ ├── MainActivity.kt
│ │ │ ├── DrawCanvas.kt
│ │ │ ├── UnlockViewModel.kt
│ │ │ └── Utils.kt
│ │ ├── res/
│ │ ├── assets/
│ │ │ └── model.tflite
│ │ └── AndroidManifest.xml
├── .gitignore
├── build.gradle.kts
├── README.md
└── LICENSE


## Getting Started

### Prerequisites

- Android Studio Electric Eel or later
- Kotlin 1.8+
- Minimum SDK: 24
- TensorFlow Lite model (`.tflite`)

### Running the App

1. Clone the repository:

```bash
git clone https://github.com/yourusername/DrawUnlock.git
cd DrawUnlock
```

    Open the project in Android Studio.

    Place your model.tflite file in app/src/main/assets/.

    Build and run the app on a physical device or emulator.

Model Details

    Model Type: CNN trained on sketches

    Dataset: Subset of Google Quick, Draw! dataset (e.g., 40–50 categories)

    Format: TensorFlow Lite (float or quantized)

    Size: ~100–200 KB
