package com.uxui.carwash.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobType {
    INTERIOR("Interior"),
    EXTERIOR("Exterior"),
    INTERIOR_AND_EXTERIOR("Interior and exterior");

    private String description;
}
