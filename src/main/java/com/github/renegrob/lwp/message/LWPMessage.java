package com.github.renegrob.lwp.message;

import com.google.common.primitives.Bytes;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#common-header-description
 */
public abstract class LWPMessage {

    private static final int HUB_ID = 0x00;

    private final byte messageType;

    protected LWPMessage(byte messageType) {
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
