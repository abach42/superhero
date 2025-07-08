package com.abach42.superhero.superhero;

import com.abach42.superhero.config.convertion.ConvertibleEnum;

public enum Gender implements ConvertibleEnum<Byte> {
    MALE((byte) 0),
    FEMALE((byte) 1),
    HIDDEN((byte) 2);

    private final byte value;

    Gender(int value) {
        this.value = (byte) value;
    }

    public Byte getValue() {
        return value;
    }
}
