import java.io.*;
import java.net.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Runtime;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Server {

    private static final Pin gg = RaspiPin.GPIO_27;
    private static final Pin cc = RaspiPin.GPIO_24;
    private static final Pin dd = RaspiPin.GPIO_25;
    private static final Pin in1 = RaspiPin.GPIO_09;
    private static final Pin in2 = RaspiPin.GPIO_07;
    private static final Pin in3 = RaspiPin.GPIO_00;
    private static final Pin in4 = RaspiPin.GPIO_02;
    private static final Pin led = RaspiPin.GPIO_04;
    private static final Pin buzz = RaspiPin.GPIO_05;
    private static String mqttdata ; 

    public static void main(String[] args) throws Exception {

        // Create GPIO controller
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalInput lcc = gpio.provisionDigitalInputPin(cc, "Sensorc");
        final GpioPinDigitalInput lgg = gpio.provisionDigitalInputPin(gg, "Sensorg");
        final GpioPinDigitalInput ldd = gpio.provisionDigitalInputPin(dd, "Sensord");
        final GpioPinDigitalOutput sin1 = gpio.provisionDigitalOutputPin(in1, "in1", PinState.LOW);
        final GpioPinDigitalOutput sin2 = gpio.provisionDigitalOutputPin(in2, "in2", PinState.LOW);
        final GpioPinDigitalOutput sin3 = gpio.provisionDigitalOutputPin(in3, "in3", PinState.LOW);
        final GpioPinDigitalOutput sin4 = gpio.provisionDigitalOutputPin(in4, "in4", PinState.LOW);
        final GpioPinDigitalOutput sled = gpio.provisionDigitalOutputPin(led, "led", PinState.LOW);
        final GpioPinDigitalOutput sbuzz = gpio.provisionDigitalOutputPin(buzz, "buzz", PinState.LOW);

        // Create PWM pins for ena and enb
        final GpioPinPwmOutput pwmEna = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);
        final GpioPinPwmOutput pwmEnb = gpio.provisionPwmOutputPin(RaspiPin.GPIO_23);
        pwmEna.setPwmRange(100);
        pwmEna.setPwm(60);
        pwmEnb.setPwmRange(100);
        pwmEnb.setPwm(60);

        // Create a server socket on a specific port
        int port = 8810;
        int portw = 8888;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        // Listen for client connections
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

        // Create input/output streams for communication with the client
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter writer = new PrintWriter(outputStream, true);

        //wifi
        ServerSocket serverSocketw = new ServerSocket(portw);
        System.out.println("Waiting for connection...");

        Socket socketw = serverSocketw.accept();
        System.out.println("Connected to ESP32");

        BufferedReader readerw = new BufferedReader(new InputStreamReader(socketw.getInputStream()));
        PrintWriter writerw = new PrintWriter(socketw.getOutputStream(), true);
        PrintWriter writerDashboard = new PrintWriter(outputStream, true);

        // mqtt 
        String broker = "tcp://test.mosquitto.org:1883"; // MQTT broker address
        String clientId = "JavaSubscriber"; // Client ID for this subscriber
        String topic = "person/detection"; // Topic to subscribe to

        //mqtt 
        try {
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Reconnect or handle the loss of connection
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Handle the received message
                    String receivedMessage = new String(message.getPayload());
                    System.out.println("Received message: " + receivedMessage);

                    // Assign the received message to the variable
                    mqttdata = receivedMessage;
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });

            client.connect();
            client.subscribe(topic);

            int p = 0;
            // Process commands from the mobile app
            
            String command = "";
            boolean continueProcessing = true;
            boolean continueProcessingoff = true;
            while (continueProcessing) {
                if (reader.ready()) {
                    command = reader.readLine();
                    System.out.println("Received command from mobile app: " + command);

                    switch (command) {
                        case "ON":
                            writer.println("robot ON");
                            continueProcessingoff = true;

                            // Continuously read sensor values and follow the line
                            while (continueProcessingoff) {
                                String receivedData = readerw.readLine();
                                System.out.println("Received data from ESP32: " + receivedData);

                                if (receivedData.equals("0") && mqttdata.equals("0")) {
                                    boolean iscc = lcc.isHigh();
                                    boolean isgg = lgg.isHigh();
                                    boolean isdd = ldd.isHigh();

                                    if (!isgg && iscc && !isdd) {
                                        pwmEna.setPwm(50);
                                        sin1.low();
                                        sin2.high();
                                        sin3.low();
                                        sin4.high();
                                        pwmEnb.setPwm(50);
                                        sled.high();
                                        sbuzz.low();
                                    } else if (isgg && !isdd) {
                                        pwmEna.setPwm(50);
                                        sin1.low();
                                        sin2.high();
                                        sin3.low();
                                        sin4.high();
                                        pwmEnb.setPwm(20);
                                        sled.high();
                                        sbuzz.low();
                                    } else if (!isgg && isdd ) {
                                        pwmEna.setPwm(15);
                                        sin1.low();
                                        sin2.high();
                                        sin3.low();
                                        sin4.high();
                                        pwmEnb.setPwm(50);
                                        sled.high();
                                        sbuzz.low();
                                    }
                                     
                                    //procesus
                                    else if (isgg && iscc && isdd) {
                                        p=p+25;
                                        pwmEna.setPwm(50);
                                        sin1.low();
                                        sin2.high();
                                        sin3.low();
                                        sin4.high();
                                        pwmEnb.setPwm(50);
                                        sled.high();
                                        sbuzz.low();
                                        Thread.sleep(500);
                                        writerDashboard.println(p);
                                    }
                                } else if (receivedData.equals("1") || mqttdata.equals("1")) {
                                    stopRobot(sin1, sin2, sin3, sin4, pwmEna, pwmEnb, writer);
                                    sled.low();
                                    sbuzz.high();
                                    //continueProcessing = false;
                                }

                                // Check if the command is changed to "OFF"
                                if (reader.ready() && (command = reader.readLine()) != null && command.equals("OFF")) {
                                    stopRobot(sin1, sin2, sin3, sin4, pwmEna, pwmEnb, writer);
                                    sled.low();
                                    sbuzz.low();

                                    continueProcessingoff = false;
                                }
                            }

                            // Stop the robot before exiting the loop
                            stopRobot(sin1, sin2, sin3, sin4, pwmEna, pwmEnb, writer);
                            sled.low();
                            break;
                        case "OFF":
                            stopRobot(sin1, sin2, sin3, sin4, pwmEna, pwmEnb, writer);
                            //continueProcessing = false;

                            break;

                        

                        default:
                            writer.println("Invalid command");
                            break;
                    }
                }
            }

            // Clean up resources
            reader.close();
            writer.close();
            clientSocket.close();
            serverSocket.close();
            gpio.shutdown();
            client.unsubscribe(topic);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private static void stopRobot(GpioPinDigitalOutput sin1, GpioPinDigitalOutput sin2,
                                  GpioPinDigitalOutput sin3, GpioPinDigitalOutput sin4,
                                  GpioPinPwmOutput pwmEna, GpioPinPwmOutput pwmEnb,
                                  PrintWriter writer) {
        sin1.low();
        sin2.low();
        sin3.low();
        sin4.low();

        pwmEna.setPwm(0); // Set PWM value for ena pin to 0
        pwmEnb.setPwm(0); // Set PWM value for enb pin to 0

        if (writer != null) {
            writer.println("robot OFF");
        }
    }
}

