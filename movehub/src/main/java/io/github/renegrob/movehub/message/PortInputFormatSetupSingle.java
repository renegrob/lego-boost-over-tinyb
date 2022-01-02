package io.github.renegrob.movehub.message;


import static io.github.renegrob.movehub.Util.b;

public class PortInputFormatSetupSingle extends LWPDownstreamMessage {

    private static final byte PORT_INPUT_FORMAT_SETUP_SINGLE = 0x41;

    private static final byte MODE_SENSOR_SPEED = 0x01;
    private static final byte MODE_SENSOR_ANGLE = 0x02;

    /*
    COLOR_INDEX = 0x00
    DISTANCE_INCHES = 0x01
    COUNT_2INCH = 0x02
    DISTANCE_REFLECTED = 0x03
    AMBIENT_LIGHT = 0x04
    SET_COLOR = 0x05
    COLOR_RGB = 0x06
    SET_IR_TX = 0x07
    COLOR_DISTANCE_FLOAT = 0x08  # it's not declared by dev's mode info

    DEBUG = 0x09  # first val is by fact ambient light, second is zero
    0x09 - lumonosity measurement mode (gives 1024 values from 0 to 1023)
    CALIBRATE = 0x0a  # gives constant values
     */

    private static final byte UnlockAndStartWithMultiUpdateEnabled = 0x03;

    private final Port port;
    private byte mode = MODE_SENSOR_ANGLE;
    private int deltaInterval = 1;
    private boolean notificationEnabled  = true;

    public PortInputFormatSetupSingle(Port port) {
        super(PORT_INPUT_FORMAT_SETUP_SINGLE);
        this.port = port;
    }

    private byte mapPort(Port port) {
        // strange port mapping - is this correct?
        switch (port) {
            case A:
                return 0x37;
            case B:
                return 0x38;
            case AB:
                return 0x39;
            case C:
                return 0x01;
            case D:
                return 0x02;
            default:
                throw new RuntimeException("Cannot map port: " + port.name());
        }
    }

    @Override
    byte[] messagePayload() {
        return new byte[]{ port.value(), mode, (byte) deltaInterval, (byte) (deltaInterval >> 8), (byte) (deltaInterval >> 16), (byte) (deltaInterval >> 24), b(notificationEnabled ? 1 : 0) };
    }
}
