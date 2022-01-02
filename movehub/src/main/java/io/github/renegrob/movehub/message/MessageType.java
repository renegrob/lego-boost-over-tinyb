package io.github.renegrob.movehub.message;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

    UNKNOWN(-1),
    HUB_PROPERTIES(0x01),
    HUB_ACTIONS(0x02),
    HUB_ALERTS(0x03),
    HUB_ATTACHED_IO(0x04),
    GENERIC_ERROR_MESSAGE(0x05),
    HW_NETOWRK_COMMANDS(0x08),
    FW_UPDATE_LOCK_STATUS(0x13),
    PORT_INFORMATION(0x43),
    PORT_MODE_INFORMATION(0x44),
    PORT_VALUE_SINGLE(0x45),
    PORT_VALUE_COMBINED(0x46),
    PORT_INPUT_FORMAT_SINGLE(0x47),
    PORT_INPUT_FORMAT_COMBINED(0x48),
    PORT_OUTPUT_COMMAND_FEEDBACK(0x82);


    private static final Map<Integer, MessageType> VALUE_TO_EVENT = new HashMap<>();

    static {
        for (MessageType event : MessageType.values()) {
            VALUE_TO_EVENT.put(event.value, event);
        }
    }

    public static MessageType of(int value) {
        return VALUE_TO_EVENT.get(value);
    }

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public byte value() {
        return (byte) value;
    }
}
