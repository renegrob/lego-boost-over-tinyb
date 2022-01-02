package io.github.renegrob.movehub.peripheral.event;

import io.github.renegrob.movehub.message.Port;

public class PeripheralDetachedEvent implements PeripheralEvent {

    private final Port port;

    public PeripheralDetachedEvent(Port port) {
        this.port = port;
    }

    public Port port() {
        return port;
    }
}
