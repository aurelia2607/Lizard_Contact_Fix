package com.lizardcontact.model;

import java.time.LocalDate;

public class PersonalContact extends Contact {
    private String nickname;
    private LocalDate birthdate;
    private String relationship;

    public PersonalContact() {
        this.contactType = "Personal";
    }

    public PersonalContact(String name, String phoneNumber, String email, String address, String category,
                           String nickname, LocalDate birthdate, String relationship) {
        super(name, phoneNumber, email, address, category);
        this.contactType = "Personal";
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.relationship = relationship;
    }

    @Override
    public String getDisplayInfo() {
        return "[Personal] " + name + (nickname != null && !nickname.isEmpty() ? " (" + nickname + ")" : "");
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

}
