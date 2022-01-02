package io.github.renegrob.movehub.message.event;

import com.google.common.primitives.Bytes;
import io.github.renegrob.movehub.message.LWPUpstreamMessage;
import io.github.renegrob.movehub.message.MessageType;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import static io.github.renegrob.movehub.Util.toHex;
import static io.github.renegrob.movehub.Util.uint16;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#port-value-single
 */
public class PortValueSingle extends LWPUpstreamMessage {

    private final int port;
    private final int value;

    public PortValueSingle(MessageType messageType, ByteArrayInputStream input) {
        super(messageType.value());
        this.port = input.read();
        if (input.available() == 1) {
            value = input.read();
        } else if (input.available() == 2) {
            value = uint16((byte) input.read(), (byte) input.read());
        } else {
            byte[] valueBytes = input.readAllBytes();
            Bytes.reverse(valueBytes);
            ByteBuffer wrapped = ByteBuffer.wrap(valueBytes);
            value = wrapped.getInt();
        }
    }

    public int port() {
        return port;
    }

    public int value() {
        return value;
    }

    @Override
    public MessageType messageType() {
        return MessageType.of(rawMessageType());
    }

    @Override
    public String toString() {
        return "PortValueSingle{" +
                "port=" + toHex(new byte[]{(byte) port}) +
                ", value=" + value +
                '}';
    }
}
