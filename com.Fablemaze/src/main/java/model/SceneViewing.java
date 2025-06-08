/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class SceneViewing {
    private int viewingId;
    private int sessionId;
    private int variantId;
    private int watchDuration;
    private boolean droppedOff;
    private String timestamp; // stored as a string in ISO format

    // Full constructor
    public SceneViewing(int viewingId, int sessionId, int variantId, int watchDuration, boolean droppedOff, String timestamp) {
        this.viewingId = viewingId;
        this.sessionId = sessionId;
        this.variantId = variantId;
        this.watchDuration = watchDuration;
        this.droppedOff = droppedOff;
        this.timestamp = timestamp;
    }

    // Constructor without ID (used when inserting a new row)
    public SceneViewing(int sessionId, int variantId, int watchDuration, boolean droppedOff, String timestamp) {
        this(0, sessionId, variantId, watchDuration, droppedOff, timestamp);
    }

    // Getters and setters (1-line)
    public int getViewingId() { return viewingId; }
    public void setViewingId(int viewingId) { this.viewingId = viewingId; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getWatchDuration() { return watchDuration; }
    public void setWatchDuration(int watchDuration) { this.watchDuration = watchDuration; }

    public boolean isDroppedOff() { return droppedOff; }
    public void setDroppedOff(boolean droppedOff) { this.droppedOff = droppedOff; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

