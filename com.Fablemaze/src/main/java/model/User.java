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
    private String preferredPacing;

    public User(int userId, String username, String passwordHash, String dob, String sex, Double openness, 
                Double conscientiousness, Double extraversion, Double agreeableness, Double neuroticism, String preferredPacing) {
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
        this.preferredPacing = preferredPacing;
    }

    public User(String username, String passwordHash, String dob, String sex, Double openness, Double conscientiousness,
                Double extraversion, Double agreeableness, Double neuroticism, String preferredPacing) {
        this(0, username, passwordHash, dob, sex, openness, conscientiousness, extraversion, agreeableness, neuroticism, preferredPacing);
    }
    
    public User(String username, String passwordHash, String dob, String sex) {
        this(0, username, passwordHash, dob, sex, 0.0, 0.0, 0.0, 0.0, 0.0, "medium");
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
    
    public String getPreferredPacing() { return preferredPacing; }
    public void setPreferredPacing(String preferredPacing) { this.preferredPacing = preferredPacing; }
}

