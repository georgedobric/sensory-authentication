public class PotValConversion {
    public static int convert (int potentiometerValue) {
        return (int) ((potentiometerValue / 1023.0) * 100);
    }
}
