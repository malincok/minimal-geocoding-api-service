package com.malincok.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidCoordinatesException extends RuntimeException {
    private final String message;
}
