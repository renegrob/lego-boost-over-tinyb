package com.github.renegrob.lwp.peripheral;

import com.google.common.primitives.Bytes;
import org.junit.jupiter.api.Test;

import static com.github.renegrob.lwp.Util.toHex;
import static com.github.renegrob.lwp.peripheral.Peripheral.PORT_LED;
import static com.github.renegrob.lwp.peripheral.Peripheral.WRITE_DIRECT_MODE_DATA;
import static org.assertj.core.api.Assertions.assertThat;

class LedTest {
    
    @Test
    void ledColorCommand() {
        byte[] actualBytes = new Led.LedColorCommand(PORT_LED, Led.Color.GREEN).toBytes();
        byte[] expectedByes = Bytes.concat(new byte[] {0x08, 0x00, (byte) 0x81, PORT_LED, 0x11, WRITE_DIRECT_MODE_DATA, 0x00}, new byte[]{ Led.Color.GREEN.value() });
        assertThat(toHex(actualBytes)).isEqualTo(toHex(expectedByes));
    }
}