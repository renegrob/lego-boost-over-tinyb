package io.github.renegrob.movehub.message;

import java.util.HashMap;
import java.util.Map;

public enum IOTypeID {

    MOTOR(0X0001),
    SYSTEM_TRAIN_MOTOR(0X0002),
    BUTTON(0X0005),
    LED_LIGHT(0X0008),
    VOLTAGE(0X0014),
    CURRENT(0X0015),
    PIEZO_TONE_SOUND(0X0016),
    RGB_LIGHT(0X0017),
    EXTERNAL_TILT_SENSOR(0X0022),
    MOTION_SENSOR(0X0023),
    VISION_SENSOR(0X0025),
    EXTERNAL_MOTOR_WITH_TACHO(0X0026),
    INTERNAL_MOTOR_WITH_TACHO(0X0027),
    INTERNAL_TILT(0X0028);

    private static final Map<Integer, IOTypeID> VALUE_TO_TYPE = new HashMap<>();

    static {
        for (IOTypeID type : IOTypeID.values()) {
            VALUE_TO_TYPE.put(type.value, type);
        }
    }

    public static IOTypeID ofValue(int value) {
        return VALUE_TO_TYPE.get(value);
    }

    private final int value;

    IOTypeID(int value) {
        this.value = value;
    }
}
