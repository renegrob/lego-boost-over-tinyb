package io.github.renegrob.movehub.message;

import static io.github.renegrob.movehub.Util.toHex;

public class GenericUpstreamMessage extends LWPUpstreamMessage {

    private final byte[] bytes;

    public GenericUpstreamMessage(byte rawMessageType, byte[] bytes) {
        super(rawMessageType);
        this.bytes = bytes;
    }

    public byte[] payload() {
        return bytes;
    }

    @Override
    public MessageType messageType() {
        return MessageType.UNKNOWN;
    }

    @Override
    public String toString() {
        return "GenericUpstreamMessage{" +
                "messageType=" + rawMessageType() +
                ", bytes=" + toHex(bytes) +
                '}';
    }
}
