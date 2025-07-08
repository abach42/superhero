package com.abach42.superhero.user;

import com.abach42.superhero.config.convertion.ConvertibleEnum;

public enum UserRole implements ConvertibleEnum<Byte> {
    ADMIN((byte) 0),
    USER((byte) 1);

    private final byte value;

    UserRole(int value) {
        this.value = (byte) value;
    }

    public static UserRole fromValue(byte value) {
        for (UserRole level : UserRole.values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid level value: " + value);
    }

    public Byte getValue() {
        return value;
    }
}
