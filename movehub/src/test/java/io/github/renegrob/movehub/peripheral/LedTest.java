package io.github.renegrob.movehub.peripheral;

import com.google.common.primitives.Bytes;
import org.junit.jupiter.api.Test;

import static io.github.renegrob.movehub.Util.toHex;
import static io.github.renegrob.movehub.peripheral.Peripheral.PORT_LED;
import static io.github.renegrob.movehub.peripheral.Peripheral.WRITE_DIRECT_MODE_DATA;
import static org.assertj.core.api.Assertions.assertThat;

class LedTest {
    
    @Test
    void ledColorCommand() {
        byte[] actualBytes = new Led.LedColorCommand(PORT_LED, Led.Color.GREEN).toBytes();
        byte[] expectedByes = Bytes.concat(new byte[] {0x08, 0x00, (byte) 0x81, PORT_LED, 0x11, WRITE_DIRECT_MODE_DATA, 0x00}, new byte[]{ Led.Color.GREEN.value() });
        assertThat(toHex(actualBytes)).isEqualTo(toHex(expectedByes));
    }
}