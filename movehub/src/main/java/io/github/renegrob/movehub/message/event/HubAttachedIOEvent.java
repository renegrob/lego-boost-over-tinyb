package io.github.renegrob.movehub.message.event;

import io.github.renegrob.movehub.Util;
import io.github.renegrob.movehub.message.*;

import java.io.ByteArrayInputStream;

import static io.github.renegrob.movehub.Util.toHex;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#hub-attached-i-o-message-format
 */
public class HubAttachedIOEvent extends LWPUpstreamMessage {

    private final IOEvent event;
    private final int port;
    private final IOTypeID type;

    public HubAttachedIOEvent(MessageType messageType, ByteArrayInputStream input) {
        super(messageType.value());
        this.port = input.read();
        this.event = IOEvent.ofValue(input.read());
        this.type = IOTypeID.ofValue(Util.uint16((byte) input.read(), (byte) input.read()));
        // TODO: physical ports A + B
    }

    public IOEvent event() {
        return event;
    }

    public Port port() {
        return Port.of(port);
    }

    public int rawPort() {
        return port;
    }

    public IOTypeID type() {
        return type;
    }


    @Override
    public MessageType messageType() {
        return MessageType.of(rawMessageType());
    }

    @Override
    public String toString() {
        return "HubAttachedIOEvent{" +
                "event=" + event +
                ", port=" + toHex(new byte[]{(byte) port}) +
                ", type=" + type +
                '}';
    }
}
