package io.github.renegrob.movehub.message;

import com.google.common.primitives.Bytes;

/**
 * https://lego.github.io/lego-ble-wireless-protocol-docs/index.html#port-output-command
 */
public class PortOutputCommand extends LWPDownstreamMessage {

    private static final byte PORT_OUTPUT_COMMAND_MESSAGE_TYPE = (byte) 0x81;

    private final byte portId;
    private boolean bufferEnabled;
    private boolean feedbackEnabled;
    private final byte subCommand;
    private final byte[] subCommandPayload;

    public PortOutputCommand(int portId, byte subCommand, byte[] subCommandPayload) {
        super(PORT_OUTPUT_COMMAND_MESSAGE_TYPE);
        this.portId = (byte) portId;
        this.subCommand = subCommand;
        this.subCommandPayload = subCommandPayload;
    }

    public PortOutputCommand(int portId, boolean bufferEnabled, boolean feedbackEnabled, byte subCommand, byte... subCommandPayload) {
        super(PORT_OUTPUT_COMMAND_MESSAGE_TYPE);
        this.portId = (byte) portId;
        this.bufferEnabled = bufferEnabled;
        this.feedbackEnabled = feedbackEnabled;
        this.subCommand = subCommand;
        this.subCommandPayload = subCommandPayload;
    }

    @Override
    byte[] messagePayload() {
        int ssss = bufferEnabled ? 0 : 0b00010000;
        int cccc = feedbackEnabled ? 1 : 0;
        return Bytes.concat(new byte[]{portId, (byte) (ssss | cccc), subCommand}, subCommandPayload);
    }

    public boolean isBufferEnabled() {
        return bufferEnabled;
    }

    public void setBufferEnabled(boolean bufferEnabled) {
        this.bufferEnabled = bufferEnabled;
    }

    public boolean isFeedbackEnabled() {
        return feedbackEnabled;
    }

    public void setFeedbackEnabled(boolean feedbackEnabled) {
        this.feedbackEnabled = feedbackEnabled;
    }
}
