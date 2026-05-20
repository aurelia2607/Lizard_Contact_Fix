package com.lizardcontact.model;

import java.util.Map;

public class ContactStatistics {
    private int totalContacts;
    private int favoriteCount;
    private int personalCount;
    private int businessCount;
    private int newThisMonth;
    private Map<String, Integer> categoryDistribution;
    private String topCategory;

    public ContactStatistics() {}

    public double getAveragePerCategory() {
        if (categoryDistribution == null || categoryDistribution.isEmpty()) return 0;
        return (double) totalContacts / categoryDistribution.size();
    }

    public String getPersonalVsBusinessRatio() {
        if (totalContacts == 0) return "0:0";
        int pPct = (int) Math.round((double) personalCount / totalContacts * 100);
        int bPct = 100 - pPct;
        return pPct + ":" + bPct;
    }

    public int getTotalContacts() { return totalContacts; }
    public void setTotalContacts(int totalContacts) { this.totalContacts = totalContacts; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public int getPersonalCount() { return personalCount; }
    public void setPersonalCount(int personalCount) { this.personalCount = personalCount; }
    public int getBusinessCount() { return businessCount; }
    public void setBusinessCount(int businessCount) { this.businessCount = businessCount; }
    public int getNewThisMonth() { return newThisMonth; }
    public void setNewThisMonth(int newThisMonth) { this.newThisMonth = newThisMonth; }
    public Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Integer> map) { this.categoryDistribution = map; }
    public String getTopCategory() { return topCategory; }
    public void setTopCategory(String topCategory) { this.topCategory = topCategory; }
}
