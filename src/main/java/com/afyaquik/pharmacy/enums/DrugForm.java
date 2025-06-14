package com.afyaquik.pharmacy.enums;

public enum DrugForm {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    SYRUP("Syrup"),
    INJECTION("Injection"),
    OINTMENT("Ointment"),
    CREAM("Cream"),
    POWDER("Powder"),
    SOLUTION("Solution"),
    SUSPENSION("Suspension"),
    DROPS("Drops");

    private final String displayName;

    DrugForm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
