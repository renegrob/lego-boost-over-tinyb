package io.github.renegrob.movehub.peripheral;

import io.github.renegrob.movehub.MoveHub;
import io.github.renegrob.movehub.peripheral.event.PeripheralDetachedEvent;
import io.github.renegrob.movehub.peripheral.event.PeripheralEvent;

public abstract class Peripheral {

    protected final MoveHub hub;

    public static final byte PORT_A = 0x00;
    public static final byte PORT_B = 0x01;
    public static final byte PORT_C = 0x02;
    public static final byte PORT_D = 0x03;
    public static final byte PORT_AB = 0x10;
    public static final byte PORT_LED = 0x32;
    public static final byte PORT_TILT_SENSOR = 0x3A;
    public static final byte PORT_CURRENT = 0x3B;
    public static final byte PORT_VOLTAGE = 0x3C;

    public static final byte WRITE_DIRECT = 0x50;
    public static final byte WRITE_DIRECT_MODE_DATA = 0x51;

    protected Peripheral(MoveHub hub) {
        this.hub = hub;
    }

    public void event(PeripheralEvent instance) {
    }
}
