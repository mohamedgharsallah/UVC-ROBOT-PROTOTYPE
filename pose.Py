import cv2
import mediapipe as mp

mpPose = mp.solutions.pose
mpDraw = mp.solutions.drawing_utils

cap = cv2.VideoCapture(0)
cap.set(3, 640)
cap.set(4, 480)

with mpPose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
    while True:
        success, img = cap.read()
        imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        results = pose.process(imgRGB)

        if results.pose_landmarks:
            # Do something with the detected person pose landmarks
            for landmark in results.pose_landmarks.landmark:
                # Access individual landmark information
                x = int(landmark.x * img.shape[1])
                y = int(landmark.y * img.shape[0])
                cv2.circle(img, (x, y), 5, (0, 255, 0), -1)

        cv2.imshow("Image", img)
        if cv2.waitKey(1) == ord('q'):
            break

cap.release()
cv2.destroyAllWindows()
