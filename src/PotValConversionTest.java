import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PotValConversionTest {

    @Test
    public void convert() throws IOException {
        assertEquals(50, PotValConversion.convert(512));
        assertEquals(100, PotValConversion.convert(1024));
        assertEquals(0, PotValConversion.convert(0));
    }

}
