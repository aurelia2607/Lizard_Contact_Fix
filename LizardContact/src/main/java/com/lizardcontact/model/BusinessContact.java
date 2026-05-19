package com.lizardcontact.model;

public class BusinessContact extends Contact {
    private String company;
    private String jobTitle;
    private String website;

    public BusinessContact() {
        this.contactType = "Bisnis";
    }

    public BusinessContact(String name, String phoneNumber, String email, String address, String category,
                           String company, String jobTitle, String website) {
        super(name, phoneNumber, email, address, category);
        this.contactType = "Bisnis";
        this.company = company;
        this.jobTitle = jobTitle;
        this.website = website;
    }

    @Override
    public String getDisplayInfo() {
        return "[Bisnis] " + name + (company != null && !company.isEmpty() ? " - " + company : "");
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
