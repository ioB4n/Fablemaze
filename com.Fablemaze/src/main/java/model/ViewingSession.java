/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class ViewingSession {
    private int sessionId;
    private int userId;
    private int movieId;
    private String startTime; // Stored as ISO string or formatted timestamp
    private String endTime;
    private String deviceType;
    private boolean completed;

    // No-arg constructor
    public ViewingSession() {}

    // Full constructor
    public ViewingSession(int sessionId, int userId, int movieId, String startTime, String endTime, String deviceType, boolean completed) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.movieId = movieId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deviceType = deviceType;
        this.completed = completed;
    }

    // Constructor without sessionId (e.g., before DB insert)
    public ViewingSession(int userId, int movieId, String startTime, String endTime, String deviceType, boolean completed) {
        this(0, userId, movieId, startTime, endTime, deviceType, completed);
    }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
