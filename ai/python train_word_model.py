import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
from tensorflow.keras.callbacks import EarlyStopping
import matplotlib.pyplot as plt
import pickle
import os
import numpy as np
import pickle
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from tensorflow.keras.utils import to_categorical


# Nama model
model_name = "sign_word_classifier"

# Load dataset hasil preprocessing
X = np.load(os.path.join("preprocessing", "X_word.npy"), allow_pickle=True)
y = np.load(os.path.join("preprocessing", "y_word.npy"), allow_pickle=True)

# Encode label
label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)
y_categorical = to_categorical(y_encoded)

# Split train/test
X_train, X_test, y_train, y_test = train_test_split(
    X, y_categorical, test_size=0.2, random_state=42
)

# --- Model ---
model = Sequential([
    LSTM(128, return_sequences=True, input_shape=(30, 63)),
    LSTM(64),
    Dense(64, activation='relu'),
    Dropout(0.3),
    Dense(y_categorical.shape[1], activation='softmax')
])

model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
model.summary()

# --- Callback ---
early_stop = EarlyStopping(
    monitor='val_loss',
    patience=10,
    restore_best_weights=True,
    verbose=1
)

# --- Training ---
history = model.fit(
    X_train, y_train,
    validation_data=(X_test, y_test),
    epochs=50,
    batch_size=16,
    callbacks=[early_stop],
    verbose=1
)

# --- Plot Training ---
plt.figure(figsize=(10, 4))

# Accuracy plot
plt.subplot(1, 2, 1)
plt.plot(history.history['accuracy'], label='Train Accuracy', linewidth=2)
plt.plot(history.history['val_accuracy'], label='Val Accuracy', linestyle='--')
plt.title('Model Accuracy')
plt.xlabel('Epoch')
plt.ylabel('Accuracy')
plt.legend()

# Loss plot
plt.subplot(1, 2, 2)
plt.plot(history.history['loss'], label='Train Loss', linewidth=2)
plt.plot(history.history['val_loss'], label='Val Loss', linestyle='--')
plt.title('Model Loss')
plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.legend()

plt.tight_layout()
plt.show()

os.makedirs("models", exist_ok=True)

model_path = os.path.join("models", f"{model_name}.keras")
label_path = os.path.join("models", f"{model_name}_label.pkl")

model.save(model_path)

with open(label_path, "wb") as f:
    pickle.dump(label_encoder, f)

print(f"✅ Model disimpan sebagai {model_path}")
print(f"✅ Label encoder disimpan sebagai {label_path}")


os.makedirs("models", exist_ok=True)

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

tflite_path = os.path.join("models", f"{model_name}.tflite")

with open(tflite_path, "wb") as f:
    f.write(tflite_model)