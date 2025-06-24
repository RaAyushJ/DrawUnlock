import numpy as np
import tensorflow as tf
import os
import gzip
import matplotlib.pyplot as plt

# Categories you want to support (must match Kotlin side)
CLASSES = ['apple', 'cat', 'tree', 'car', 'book', 'sun', 'cloud', 'fish', 'star', 'house']

NUM_CLASSES = len(CLASSES)
IMG_SIZE = 28

def load_ndjson_class(name, max_items=3000):
    path = f"data/full_numpy_bitmap_{name}.npy"
    data = np.load(path)
    data = data[:max_items]
    labels = np.full((len(data),), CLASSES.index(name))
    return data, labels

# Load data
X = []
y = []

for label in CLASSES:
    x_i, y_i = load_ndjson_class(label)
    X.append(x_i)
    y.append(y_i)

X = np.concatenate(X, axis=0)
y = np.concatenate(y, axis=0)

# Normalize
X = X / 255.0
X = X.reshape(-1, 28, 28, 1)

# Shuffle and split
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Model
model = tf.keras.Sequential([
    tf.keras.layers.Conv2D(32, (3, 3), activation='relu', input_shape=(IMG_SIZE, IMG_SIZE, 1)),
    tf.keras.layers.MaxPooling2D(),
    tf.keras.layers.Conv2D(64, (3, 3), activation='relu'),
    tf.keras.layers.MaxPooling2D(),
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dense(NUM_CLASSES, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(X_train, y_train, epochs=5, validation_data=(X_test, y_test))

# Save the model
model.save("draw_model.h5")

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open("model.tflite", "wb") as f:
    f.write(tflite_model)

print("Model saved to model.tflite")
