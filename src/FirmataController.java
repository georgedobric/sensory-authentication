import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;

public class FirmataController {
    private final Pin greenLedPin;
    private final Pin yellowLedPin;
    private final Pin redLedPin;
    private final Pin potentiometerPin;
    private final Pin buttonPin;
    private final Pin laserPin;
    private final Pin lightBlockPin;
    private final Pin photoResistorPin;

    public FirmataController(Pin greenLedPin, Pin yellowLedPin, Pin redLedPin, Pin potentiometerPin, Pin buttonPin, Pin laserPin, Pin lightBlockPin, Pin photoResistorPin) throws IOException {

        this.greenLedPin = greenLedPin;
        this.yellowLedPin = yellowLedPin;
        this.redLedPin = redLedPin;
        this.potentiometerPin = potentiometerPin;
        this.buttonPin = buttonPin;
        this.laserPin = laserPin;
        this.lightBlockPin = lightBlockPin;
        this.photoResistorPin = photoResistorPin;

        greenLedPin.setMode(Pin.Mode.OUTPUT);
        yellowLedPin.setMode(Pin.Mode.OUTPUT);
        redLedPin.setMode(Pin.Mode.OUTPUT);
        potentiometerPin.setMode(Pin.Mode.ANALOG);
        buttonPin.setMode(Pin.Mode.INPUT);
        laserPin.setMode(Pin.Mode.OUTPUT);
        lightBlockPin.setMode(Pin.Mode.INPUT);
        photoResistorPin.setMode(Pin.Mode.ANALOG);
    }
    boolean lineOfSight = false;
    boolean accessGranted = false;

    // State Machine
    public boolean controlLeds(int potVal, int lightBlockVal, int photoResistorVal) throws IOException {
        if (photoResistorVal > 600)
            lineOfSight = false;
        else
            lineOfSight = true;
        // Control LEDs based on the Potentiometer & Light Block Sensor
        if (lightBlockVal == 0 && !lineOfSight) {
            greenLedPin.setValue(0);
            yellowLedPin.setValue(0);
            redLedPin.setValue(1);
            accessGranted = false;
        } else if ((potVal >= 50 && lightBlockVal == 1 && !lineOfSight) || (potVal >= 50 && lineOfSight && lightBlockVal == 0) ) {
            greenLedPin.setValue(0);
            yellowLedPin.setValue(1);
            redLedPin.setValue(0);
            accessGranted = false;
        } else if (potVal >= 50 && potVal <= 100 && lightBlockVal == 1 && lineOfSight) {
            greenLedPin.setValue(1);
            yellowLedPin.setValue(0);
            redLedPin.setValue(0);
            accessGranted = true;
        }

        return (accessGranted);
    }

    public void controlLaser(int buttonVal) throws IOException {
        if (buttonVal == 1) {
            laserPin.setValue(1);
//            System.out.println("Laser is On");
        }
        else
            laserPin.setValue(0);
    }



    public int readPotentiometer() {
        return (int) potentiometerPin.getValue();
    }

    public int readButton() {
        return (int) buttonPin.getValue();
    }

    public int readLightBlock() {
        return (int) lightBlockPin.getValue();
    }

    public int readphotoResistor() {
        return (int) photoResistorPin.getValue();
    }
}
