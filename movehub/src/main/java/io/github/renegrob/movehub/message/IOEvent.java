package io.github.renegrob.movehub.message;

import java.util.HashMap;
import java.util.Map;

public enum IOEvent {

    DETACHED(0X00),
    ATTACHED(0X01),
    ATTACHED_VIRTUAL(0X02);

    private static final Map<Integer, IOEvent> VALUE_TO_EVENT = new HashMap<>();

    static {
        for (IOEvent event : IOEvent.values()) {
            VALUE_TO_EVENT.put(event.value, event);
        }
    }

    public static IOEvent ofValue(int value) {
        return VALUE_TO_EVENT.get(value);
    }

    private final int value;

    IOEvent(int value) {
        this.value = value;
    }
}
