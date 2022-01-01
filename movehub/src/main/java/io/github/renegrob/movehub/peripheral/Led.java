package io.github.renegrob.movehub.peripheral;

import io.github.renegrob.movehub.MoveHub;
import io.github.renegrob.movehub.message.PortOutputCommand;

public class Led extends Peripheral {

    public Led(MoveHub hub) {
        super(hub);
    }

    public enum Color {
        BLACK(0x00),
        PINK(0x01),
        PURPLE(0x02),
        BLUE(0x03),
        LIGHTBLUE(0x04),
        CYAN(0x05),
        GREEN(0x06),
        YELLOW(0x07),
        ORANGE(0x08),
        RED(0x09),
        WHITE(0x0a),
        NONE(0xFF);

        private final int value;

        Color(int value) {
            this.value = value;
        }

        public byte value() {
            return (byte) value;
        }
    }


    public void setColor(Color color) {
        hub.writeValue(new LedColorCommand(PORT_LED, color));
        //hub.writeValue(Bytes.concat(new byte[] {0x08, 0x00, (byte) 0x81, 0x32, 0x11, WRITE_DIRECT_MODE_DATA, 0x00}, new byte[]{ (byte) color.value}));
    }

    /**
     * Port ID, Startup and Completion Information, 0x51, 0x00, ColorNo
     */
    static class LedColorCommand extends PortOutputCommand {
        public LedColorCommand(int portId, Color color) {
            super(portId, false, true, Peripheral.WRITE_DIRECT_MODE_DATA, (byte) 0x00, color.value());
        }
    }

    /**
     * Port ID, Startup and Completion Information, 0x51, 0x00, 0x51, 0x01, R, G, B
     * Only works in mode 1
     */
    static class LedRGBColorCommand extends PortOutputCommand {
        public LedRGBColorCommand(int portId, int red, int green, int blue) {
            super(portId, false, true, Peripheral.WRITE_DIRECT_MODE_DATA, (byte) 0x00,
                    (byte) 0x51, (byte) 0x01, (byte) red, (byte) green, (byte) blue);
        }
    }
}
