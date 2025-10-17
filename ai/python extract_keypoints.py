import os
import cv2
import numpy as np
from tqdm import tqdm
import mediapipe as mp
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from tensorflow.keras.utils import to_categorical
import joblib

dataset_dir = './dataset_word'

mp_hands = mp.solutions.hands
hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=2,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

data = []
labels = []

print("🎬 Mulai ekstraksi keypoint dari video...\n")

for label_folder in tqdm(sorted(os.listdir(dataset_dir))):
    folder_path = os.path.join(dataset_dir, label_folder)
    if not os.path.isdir(folder_path):
        continue

    for file in os.listdir(folder_path):
        if not file.lower().endswith(".mp4"):
            continue

        video_path = os.path.join(folder_path, file)
        cap = cv2.VideoCapture(video_path)

        keypoints_seq = []
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            results = hands.process(frame)

            if results.multi_hand_landmarks:
                hand_keypoints = []
                for hand_landmarks in results.multi_hand_landmarks:
                    hand = []
                    for lm in hand_landmarks.landmark:
                        hand.extend([lm.x, lm.y, lm.z])
                    hand_keypoints.append(hand)

                if len(hand_keypoints) == 1:
                    hand_keypoints.append([0.0] * 63)

                keypoints = np.concatenate(hand_keypoints)
            else:
                keypoints = np.zeros(126)

            keypoints_seq.append(keypoints)

        cap.release()

        max_len = 30
        keypoints_seq = np.array(keypoints_seq)
        if len(keypoints_seq) < max_len:
            pad = np.zeros((max_len - len(keypoints_seq), keypoints_seq.shape[1]))
            keypoints_seq = np.vstack((keypoints_seq, pad))
        elif len(keypoints_seq) > max_len:
            keypoints_seq = keypoints_seq[:max_len]

        data.append(keypoints_seq)
        labels.append(label_folder)

os.makedirs("preprocessing", exist_ok=True)

output_X_path = os.path.join("preprocessing", "X_word.npy")
output_y_path = os.path.join("preprocessing", "y_word.npy")

np.save(output_X_path, np.array(data))
np.save(output_y_path, np.array(labels))

print(f"\n✅ Ekstraksi selesai:")
print(f"📁 Data disimpan ke: {output_X_path}")
print(f"🏷️ Label disimpan ke: {output_y_path}")
print(f"🧩 Jumlah sampel: {len(data)}")

X = np.load(os.path.join("preprocessing", "X_word.npy"), allow_pickle=True)
y = np.load(os.path.join("preprocessing", "y_word.npy"), allow_pickle=True)

label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)
y_categorical = to_categorical(y_encoded)

joblib.dump(label_encoder, os.path.join("preprocessing", "label_encoder_word.pkl"))

X_train, X_test, y_train, y_test = train_test_split(X, y_categorical, test_size=0.2, random_state=42)

print("📊 Data shape:", X_train.shape, y_train.shape)