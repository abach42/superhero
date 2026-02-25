package com.abach42.superhero.syslog;

import com.abach42.superhero.shared.convertion.ConvertibleEnum;

public enum Level implements ConvertibleEnum<Byte> {
    ESSENTIAL((byte) 0),
    STANDARD((byte) 1),
    DETAILED((byte) 2);

    private final byte value;

    Level(int value) {
        this.value = (byte) value;
    }

    public Byte getValue() {
        return value;
    }

    public static Level fromValue(byte value) {
        for (Level level : Level.values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid level value: " + value);
    }
}
