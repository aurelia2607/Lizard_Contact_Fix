package com.lizardcontact.model;

public class ContactFactory {

    public static Contact create(String type) {
        if (type == null) throw new IllegalArgumentException("Tipe kontak tidak boleh null");
        return switch (type.trim().toLowerCase()) {
            case "personal" -> new PersonalContact();
            case "bisnis"   -> new BusinessContact();
            default -> throw new IllegalArgumentException("Tipe kontak tidak dikenal: " + type);
        };
    }
}
