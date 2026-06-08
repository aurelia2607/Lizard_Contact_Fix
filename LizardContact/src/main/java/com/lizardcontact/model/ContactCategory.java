package com.lizardcontact.model;

public enum ContactCategory {
    TEMAN("Teman"),
    KELUARGA("Keluarga"),
    KOLEGA("Kolega"),
    LAINNYA("Lainnya");

    private final String displayName;

    ContactCategory(String displayName) {

        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ContactCategory fromString(String value) {
        if (value == null) return LAINNYA;
        for (ContactCategory cat : values()) {
            if (cat.displayName.equalsIgnoreCase(value)) return cat;
        }
        return LAINNYA;
    }

    public static String[] allDisplayNames() {
        ContactCategory[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    @Override
    public String toString() {
        return displayName;
    }
}