import cv2
import mediapipe as mp
import paho.mqtt.client as mqtt

# Define MQTT broker address and port
broker_address = "test.mosquitto.org"
broker_port = 1883

# Create MQTT client instance
client = mqtt.Client()

# Connect to MQTT broker
client.connect(broker_address, broker_port)

mpPose = mp.solutions.pose
mpDraw = mp.solutions.drawing_utils

cap = cv2.VideoCapture(0)
cap.set(3, 640)
cap.set(4, 480)

is_person_detected = False  # Flag to keep track of person detection

with mpPose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
    while True:
        success, img = cap.read()
        imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        results = pose.process(imgRGB)

        if results.pose_landmarks:
            is_person_detected = True  # Set the flag if person is detected
            mpDraw.draw_landmarks(img, results.pose_landmarks, mpPose.POSE_CONNECTIONS)
        else:
            is_person_detected = False  # Reset the flag if person is not detected

        # Publish the person detection status to the MQTT broker
        topic = "person/detection"
        payload = str(int(is_person_detected))

        client.publish(topic, payload)
        print(payload)

        cv2.imshow("Image", img)
        if cv2.waitKey(1) == ord('q'):
            break

cap.release()
cv2.destroyAllWindows()
