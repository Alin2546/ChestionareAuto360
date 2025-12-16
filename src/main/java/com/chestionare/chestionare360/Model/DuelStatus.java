package com.chestionare.chestionare360.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DuelStatus {

    WAITING("În așteptare"),
    IN_PROGRESS("În desfășurare"),
    FINISHED("Finalizat"),
    CANCELLED("Anulat"),
    TIMEOUT("Expirat");

    private final String label;

}
