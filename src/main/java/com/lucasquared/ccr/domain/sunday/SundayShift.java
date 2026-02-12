package com.lucasquared.ccr.domain.sunday;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SundayShift {
    MORNING("morning"),
    NIGHT("night");

    private final String value;

    SundayShift(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SundayShift fromString(String input) {
        if (input == null) {
            return null;
        }

        for (SundayShift shift : SundayShift.values()) {
            if (shift.name().equalsIgnoreCase(input) || shift.value.equalsIgnoreCase(input)) {
                return shift;
            }
        }

        throw new IllegalArgumentException("Invalid SundayShift value: " + input);
    }
}
