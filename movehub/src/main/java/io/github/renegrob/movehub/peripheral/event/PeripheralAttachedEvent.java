package io.github.renegrob.movehub.peripheral.event;

import io.github.renegrob.movehub.message.IOTypeID;
import io.github.renegrob.movehub.message.Port;
import io.github.renegrob.movehub.peripheral.Peripheral;

public class PeripheralAttachedEvent implements PeripheralEvent {

    private final Port port;
    private final IOTypeID type;

    public PeripheralAttachedEvent(Port port, IOTypeID type, Peripheral newPeripheral) {
        this.port = port;
        this.type = type;
    }

    public Port port() {
        return port;
    }

    public IOTypeID type() {
        return type;
    }
}
