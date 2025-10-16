import cv2
import mediapipe as mp
import numpy as np
import tensorflow as tf
import string
import time

# === Load TFLite model ===
model_name = 'sign_classifier'
interpreter = tf.lite.Interpreter(model_path=f"{model_name}.tflite")
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# === Label huruf A-Z ===
labels = list(string.ascii_uppercase)

# === Inisialisasi MediaPipe Hands ===
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

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        # Flip supaya tampil natural seperti cermin
        frame = cv2.flip(frame, 1)
        img_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = hands.process(img_rgb)

        keypoints = []

        if results.multi_hand_landmarks:
            for hand_landmarks in results.multi_hand_landmarks:
                for lm in hand_landmarks.landmark:
                    keypoints.extend([lm.x, lm.y, lm.z])  # ambil koordinat xyz
                mp_drawing.draw_landmarks(
                    frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

        # Jika ada tangan terdeteksi
        if keypoints:
            # Normalisasi panjang input sesuai model
            max_len = input_details[0]['shape'][1]  # misalnya 126 (2 tangan * 21 * 3)
            keypoints = np.array(keypoints, dtype=np.float32)
            if len(keypoints) < max_len:
                # isi sisa dengan nol kalau cuma 1 tangan
                keypoints = np.pad(keypoints, (0, max_len - len(keypoints)))
            elif len(keypoints) > max_len:
                # potong kalau lebih dari yang dibutuhkan
                keypoints = keypoints[:max_len]

            input_data = np.expand_dims(keypoints, axis=0)

            # Inference ke model
            interpreter.set_tensor(input_details[0]['index'], input_data)
            interpreter.invoke()
            output = interpreter.get_tensor(output_details[0]['index'])
            pred = np.argmax(output)
            confidence = np.max(output)

            letter = labels[pred] if pred < len(labels) else "?"
            cv2.putText(frame, f"{letter} ({confidence:.2f})",
                        (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 255, 0), 2)
        else:
            cv2.putText(frame, "No Hand", (10, 40),
                        cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 0, 255), 2)

        cv2.imshow("Sign Detection (TFLite + MediaPipe)", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

finally:
    cap.release()
    cv2.destroyAllWindows()
    hands.close()
    print("Camera released.")
