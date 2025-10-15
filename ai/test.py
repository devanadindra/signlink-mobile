import tensorflow as tf
from IPython.display import display, clear_output
import cv2
import numpy as np
from PIL import Image
import time
import string

model_name = 'sign_classifier'

# Load the TFLite model
interpreter = tf.lite.Interpreter(model_path=f"{model_name}.tflite")
interpreter.allocate_tensors()

# Get input & output details
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
input_shape = input_details[0]['shape'][1:3]  # (224, 224)

# List of labels A-Z
labels = list(string.ascii_uppercase)


cap = cv2.VideoCapture(0)

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        img = cv2.resize(frame, (224, 224))
        input_data = np.expand_dims(img.astype(np.float32) / 255.0, axis=0)
        interpreter.set_tensor(input_details[0]['index'], input_data)
        interpreter.invoke()
        output = interpreter.get_tensor(output_details[0]['index'])
        pred = np.argmax(output)
        letter = labels[pred] if pred < len(labels) else "?"

        cv2.putText(frame, f"Pred: {letter}", (10, 30),
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)

        cv2.imshow("Sign Detection", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break


finally:
    cap.release()
    time.sleep(0.5)
    del cap
    cv2.VideoCapture(0).release()
    cv2.destroyAllWindows()
    print("Camera released.")
    
