import org.firmata4j.*;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;

import java.math.*;

public class Main {
    public static void main(String[] args) throws Exception {
        FirmataDevice device = new FirmataDevice("/dev/cu.usbserial-0001"); // Adjust the port as needed
        device.start();
        device.ensureInitializationIsDone();

        Pin lightBlockPin = device.getPin(11);
        Pin buttonPin = device.getPin(6);
        Pin laserPin = device.getPin(7);
        Pin greenLedPin = device.getPin(3);
        Pin yellowLedPin = device.getPin(8);
        Pin redLedPin = device.getPin(2);
        Pin potentiometerPin = device.getPin(14);
        Pin photoResistorPin = device.getPin(15);

        FirmataController controller = new FirmataController(greenLedPin, yellowLedPin, redLedPin, potentiometerPin, buttonPin, laserPin, lightBlockPin, photoResistorPin);

        JFrame frame = new JFrame("Live Moisture Wetness Percentage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Graph graph = new Graph();
        JFreeChart chart = graph.createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.add(chartPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        boolean accessGranted = false;
        boolean OLEDacc = false; // Has the 'Access Granted' message been printed?
        boolean OLEDplease = false; // Has the 'Please Validate 3 Inputs...' message been printed?
        I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
        SSD1306 theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515
        // Initialize the OLED (SSD1306) object
        theOledObject.init();
        int lastVal = 0;
        while (true) {
            // Read Input Values
            int potentiometerValue = controller.readPotentiometer();
            int buttonValue = controller.readButton();
            int lightBlockValue = controller.readLightBlock();
            int photoResistorValue = controller.readphotoResistor();

            if (Math.abs(photoResistorValue - lastVal) >= 100)
                System.out.println("PHOTORESISTOR VAL: " + photoResistorValue);
            lastVal = photoResistorValue;

            //Update Graph
            int potValPercentage = PotValConversion.convert(potentiometerValue);

            //Control Actuators
            controller.controlLaser(buttonValue);
            controller.controlLeds(potValPercentage, lightBlockValue, photoResistorValue);
            accessGranted = controller.controlLeds(potValPercentage, lightBlockValue, photoResistorValue);

            if (accessGranted == true && !OLEDacc){
                theOledObject.getCanvas().clear();
                theOledObject.getCanvas().setCursor(0,0);
                System.out.println("access granted");
                theOledObject.getCanvas().write("ACCESS GRANTED");
                theOledObject.display();
                OLEDacc = true;
                OLEDplease = false;
            }
            else if (accessGranted == false && !OLEDplease){
                theOledObject.getCanvas().clear();
                theOledObject.getCanvas().setCursor(0,0);
                System.out.println("Please validate three inputs...");
                theOledObject.getCanvas().write("Please validate three inputs...");
                theOledObject.display();
                OLEDplease = true;
                OLEDacc = false;

            }


            Thread.sleep(20);

            if (graph != null) {
                graph.updateChart(potValPercentage);
            }

        }
    }
}
