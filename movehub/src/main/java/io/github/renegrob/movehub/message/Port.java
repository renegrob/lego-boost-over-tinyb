package io.github.renegrob.movehub.message;

import java.util.HashMap;
import java.util.Map;

public enum Port {
    A(0x00),
    B(0x01),
    AB(0x10),
    C(0x02),
    D(0x03);

    private static final Map<Integer, Port> VALUE_TO_TYPE = new HashMap<>();

    static {
        for (Port type : Port.values()) {
            VALUE_TO_TYPE.put(type.value, type);
        }
    }

    public static Port of(int value) {
        return VALUE_TO_TYPE.get(value);
    }

    private final int value;

    Port(int value) {
        this.value = value;
    }

    public byte value() {
        return (byte) value;
    }
}
