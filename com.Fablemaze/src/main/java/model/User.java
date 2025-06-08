/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String dob;
    private String sex;
    
    private Double openness;
    private Double conscientiousness;
    private Double extraversion;
    private Double agreeableness;
    private Double neuroticism;
    
    private int totalWatchTime;
    private Double preferredPacing;
    private String favouriteGenres;
    private Double avgSessionLength;
    private String registrationDate;
    
    public User() {
    }
    
    public User(int userId, String username, String passwordHash, String dob, String sex,
                Double openness, Double conscientiousness, Double extraversion, Double agreeableness,
                Double neuroticism, int totalWatchTime, Double preferredPacing, String favoriteGenres,
                Double avgSessionLength, String registrationDate) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.dob = dob;
        this.sex = sex;
        this.openness = openness;
        this.conscientiousness = conscientiousness;
        this.extraversion = extraversion;
        this.agreeableness = agreeableness;
        this.neuroticism = neuroticism;
        this.totalWatchTime = totalWatchTime;
        this.preferredPacing = preferredPacing;
        this.favouriteGenres = favoriteGenres;
        this.avgSessionLength = avgSessionLength;
        this.registrationDate = registrationDate;
    }

    public User(int userId, String username, String passwordHash, String dob, String sex) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.dob = dob;
        this.sex = sex;
    }
    
    public User(String username, String passwordHash, String dob, String sex) {
        this(0, username, passwordHash, dob, sex);
    }

    // Getters and setters (or use Lombok if allowed)
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    
    public Double getOpenness() { return openness; }
    public void setOpenness(Double openness) { this.openness = openness; }
    
    public Double getConscientiousness() { return conscientiousness; }
    public void setConscientiousness(Double conscientiousness) { this.conscientiousness = conscientiousness; }
    
    public Double getExtraversion() { return extraversion; }
    public void setExtraversion(Double extraversion) { this.extraversion = extraversion; }
    
    public Double getAgreeableness() { return agreeableness; }
    public void setAgreeableness(Double agreeableness) { this.agreeableness = agreeableness; }
    
    public Double getNeuroticism() { return neuroticism; }
    public void setNeuroticism(Double neuroticism) { this.neuroticism = neuroticism; }
    
    public int getTotalWatchTime() { return totalWatchTime; }
    public void setTotalWatchTime(int totalWatchTime) { this.totalWatchTime = totalWatchTime;}

    public Double getPreferredPacing() { return preferredPacing; }
    public void setPreferredPacing(Double preferredPacing) { this.preferredPacing = preferredPacing; }

    public String getFavouriteGenres() { return favouriteGenres; }
    public void setFavouriteGenres(String favouriteGenres) { this.favouriteGenres = favouriteGenres; }

    public Double getAvgSessionLength() { return avgSessionLength; }
    public void setAvgSessionLength(Double avgSessionLength) { this.avgSessionLength = avgSessionLength; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
}

