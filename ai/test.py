import cv2
import mediapipe as mp
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
import string
import os
from collections import deque

# === Load TFLite model untuk Letter ===
model_letter_path = os.path.join("models", "sign_classifier.tflite")
interpreter_letter = tf.lite.Interpreter(model_path=model_letter_path)
interpreter_letter.allocate_tensors()
input_details_letter = interpreter_letter.get_input_details()
output_details_letter = interpreter_letter.get_output_details()

# === Load Keras model untuk Word ===
model_word_path = os.path.join("models", "sign_classifier_word.keras")
model_word = load_model(model_word_path)

# === Label ===
labels_letter = list(string.ascii_uppercase)
labels_word = [line.strip() for line in open("models/labels_word.txt")]

# === MediaPipe Hands ===
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils

hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=2,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

# === Kamera ===
cap = cv2.VideoCapture(0)

# --- Buffer untuk kata (sequence 30 frame) ---
seq_buffer = deque(maxlen=30)

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        frame = cv2.flip(frame, 1)
        img_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = hands.process(img_rgb)

        keypoints = []

        if results.multi_hand_landmarks:
            hand_keypoints = []
            for hand_landmarks in results.multi_hand_landmarks[:2]:
                hand = [lm_coord for lm in hand_landmarks.landmark for lm_coord in (lm.x, lm.y, lm.z)]
                hand_keypoints.append(hand)
            while len(hand_keypoints) < 2:
                hand_keypoints.append([0.0]*63)
            keypoints = np.concatenate(hand_keypoints)
            for hand_landmarks in results.multi_hand_landmarks:
                mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
            hand_detected = True
        else:
            keypoints = np.zeros(126)
            hand_detected = False  # <-- Tidak ada tangan

        seq_buffer.append(keypoints)

        # === Prediksi Letter (per frame) ===
        if hand_detected:
            input_letter = np.expand_dims(keypoints.astype(np.float32), axis=0)
            interpreter_letter.set_tensor(input_details_letter[0]['index'], input_letter)
            interpreter_letter.invoke()
            output_letter = interpreter_letter.get_tensor(output_details_letter[0]['index'])
            pred_letter = np.argmax(output_letter)
            conf_letter = np.max(output_letter)
            letter_text = f"Letter: {labels_letter[pred_letter]} ({conf_letter:.2f})"
        else:
            letter_text = "Letter: No Hand"

        # === Prediksi Word (sequence 30 frame) ===
        if hand_detected and len(seq_buffer) == 30:
            input_word = np.expand_dims(np.array(seq_buffer, dtype=np.float32), axis=0)
            output_word = model_word.predict(input_word)
            pred_word = np.argmax(output_word)
            conf_word = np.max(output_word)
            word_text = f"Word: {labels_word[pred_word]} ({conf_word:.2f})"
        elif not hand_detected:
            word_text = "Word: No Hand"
        else:
            word_text = "Word: waiting..."

        # === Tampilkan ===
        cv2.putText(frame, letter_text, (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0,255,0), 2)
        cv2.putText(frame, word_text, (10, 70), cv2.FONT_HERSHEY_SIMPLEX, 1, (0,0,255), 2)

        cv2.imshow("Sign Detection (Letter + Word)", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

finally:
    cap.release()
    cv2.destroyAllWindows()
    hands.close()
    print("Camera released.")
