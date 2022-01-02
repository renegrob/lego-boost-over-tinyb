package io.github.renegrob.movehub.message;

import com.google.common.primitives.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#common-header-description
 */
public abstract class LWPDownstreamMessage implements LWPMessage {

    private static final Logger LOG = LogManager.getLogger();

    private final byte messageType;

    protected LWPDownstreamMessage(byte messageType) {
        this.messageType = messageType;
    }

    abstract byte[] messagePayload();

    public byte[] toBytes() {
        byte[] length;
        int totalMessageLength = messagePayload().length + 3;
        if (totalMessageLength >= 0x80) {
            totalMessageLength++;
            length = new byte[]{ (byte) 0x80, (byte) (totalMessageLength - 0x80) };
        } else {
            length = new byte[]{ (byte) totalMessageLength };
        }
        byte[] commonMessageHeader = Bytes.concat(length, new byte[]{ HUB_ID, messageType });
        return Bytes.concat(commonMessageHeader, messagePayload());
    }
}
