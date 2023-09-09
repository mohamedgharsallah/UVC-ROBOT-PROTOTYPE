
#include <WiFi.h>

const char* ssid = "INFOCOM-AP";
const char* password = "infocom@2020";
const char* serverIP = "192.168.100.19";
int serverPort = 8888; // Match the port number on the Raspberry Pi

WiFiClient client;

void setup() {
  Serial.begin(115200);
pinMode(14,INPUT);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  Serial.println("Connected to WiFi");

  // Establish TCP connection with the server
  if (client.connect(serverIP, serverPort)) {
    Serial.println("Connected to server");
  }
}

void loop() {
  // Read sensor data or perform any necessary operations
  int sensorData = digitalRead(14);

  // Convert sensor data to a string
  String payload = String(sensorData);

  // Send the data to the server
  client.println(payload);
  Serial.println("Data sent");

  // Check if there is incoming data from the server
  if (client.available()) {
    String receivedData = client.readStringUntil('\n');
    Serial.println("Received data from server: " + receivedData);
  }

  delay(500); // Adjust the delay according to your requirements
}

