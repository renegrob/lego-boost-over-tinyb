package io.github.renegrob.movehub.message;

import io.github.renegrob.movehub.message.event.HubAttachedIOEvent;
import io.github.renegrob.movehub.message.event.PortValueSingle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

import static io.github.renegrob.movehub.Util.toHex;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#common-header-description
 */
public abstract class LWPUpstreamMessage implements LWPMessage {

    private static final Logger LOG = LogManager.getLogger();

    private final byte rawMessageType;

    protected LWPUpstreamMessage(byte rawMessageType) {
        this.rawMessageType = rawMessageType;
    }

    public byte rawMessageType() {
        return rawMessageType;
    }

    public abstract MessageType messageType();

    public static LWPUpstreamMessage parseMessage(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        int length = input.read();
        if (length == 0x80) {
           length += input.read();
        }
        if (length != bytes.length) {
            LOG.warn("length value {} is not equal to received bytes: {}.", length, bytes.length);
        }
        int hubID = input.read();
        if (hubID != HUB_ID) {
            LOG.warn("Hub ID: {}", toHex(new byte[]{(byte) hubID}));
        }
        byte rawMessageType = (byte) input.read();
        MessageType messageType = MessageType.of(rawMessageType);
        if (messageType == null) {
            LOG.warn("Unknown message type: " + toHex(new byte[]{rawMessageType}));
            return new GenericUpstreamMessage(rawMessageType, bytes);
        }

        switch (messageType) {
            case HUB_ATTACHED_IO:
                return new HubAttachedIOEvent(messageType, input);
            case PORT_VALUE_SINGLE:
                return new PortValueSingle(messageType, input);
            default:
                LOG.warn("Unhandled message type: " + messageType);
                return new GenericUpstreamMessage(rawMessageType, bytes);
        }
    }

}
