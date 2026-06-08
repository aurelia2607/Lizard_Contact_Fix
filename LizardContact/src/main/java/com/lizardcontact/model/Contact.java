package com.lizardcontact.model;

import java.time.LocalDateTime;

public abstract class Contact implements Displayable {
    private int contactID;
    private String contactType;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private ContactCategory category;
    private boolean favorite;
    private int userID;
    private LocalDateTime createdAt;

    public Contact() {}

    public Contact(String name, String phoneNumber, String email, String address, ContactCategory category) {
        this.name        = name;
        this.phoneNumber = phoneNumber;
        this.email       = email;
        this.address     = address;
        this.category    = category;
        this.createdAt   = LocalDateTime.now();
    }

    @Override
    public abstract String getDisplayInfo();

    public int getContactID() {
        return contactID;
    }
    public void setContactID(int id) {
        this.contactID = id;
    }

    public String getContactType() {
        return contactType;
    }
    public void setContactType(String type) {
        this.contactType = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public ContactCategory getCategory() {
        return category;
    }
    public void setCategory(ContactCategory category){
        this.category = category;
    }

    public String getCategoryName() {
        return category != null ? category.getDisplayName() : ContactCategory.LAINNYA.getDisplayName();
    }

    public void setCategoryFromString(String value) {
        this.category = ContactCategory.fromString(value);
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
