package io.github.renegrob.movehub;

import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

public class Util {

    private static final BaseEncoding HEX_ENCODING = BaseEncoding.base16().upperCase().withSeparator(" ", 2);

    public static String toHex(byte[] bytes) {
        return HEX_ENCODING.encode(bytes);
    }

    public static void checkRange(String name, int value, int min, int max) {
        Preconditions.checkArgument(value >= min && value <= max, name + " must be in range [" + min + ", " + max + "].");
    }

    public static byte b(int uint8) {
        checkRange("uint8", uint8, 0, 255);
        return (byte) uint8;
    }


}
