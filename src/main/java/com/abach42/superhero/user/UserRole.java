package com.abach42.superhero.user;

import com.abach42.superhero.shared.convertion.ConvertibleEnum;

public enum UserRole implements ConvertibleEnum<Byte> {
    ADMIN((byte) 0),
    USER((byte) 1);

    private final byte value;

    UserRole(int value) {
        this.value = (byte) value;
    }

    public Byte getValue() {
        return value;
    }
}
