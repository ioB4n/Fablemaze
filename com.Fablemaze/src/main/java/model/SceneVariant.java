/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class SceneVariant {
    private int variantId;
    private int sceneId;
    private String variantName;
    private String filePath;
    private Double pacingScore;
    private Double intensityScore;
    private Double dialogueDensity;
    private Double actionLevel;
    private Double characterFocus;
    private Double emotionalTone;
    private int duration;

    // Default constructor
    public SceneVariant() {}

    // Full constructor
    public SceneVariant(int variantId, int sceneId, String variantName, String filePath,
                        Double pacingScore, Double intensityScore, Double dialogueDensity,
                        Double actionLevel, Double characterFocus, Double emotionalTone, int duration) {
        this.variantId = variantId;
        this.sceneId = sceneId;
        this.variantName = variantName;
        this.filePath = filePath;
        this.pacingScore = pacingScore;
        this.intensityScore = intensityScore;
        this.dialogueDensity = dialogueDensity;
        this.actionLevel = actionLevel;
        this.characterFocus = characterFocus;
        this.emotionalTone = emotionalTone;
        this.duration = duration;
    }
    
    // Constructor without variantId (defaults to 0)
    public SceneVariant(int sceneId, String variantName, String filePath,
                        Double pacingScore, Double intensityScore, Double dialogueDensity,
                        Double actionLevel, Double characterFocus, Double emotionalTone, int duration) {
        this(0, sceneId, variantName, filePath, pacingScore, intensityScore,
             dialogueDensity, actionLevel, characterFocus, emotionalTone, duration);
    }


    // Getters and setters (one-liners)
    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getSceneId() { return sceneId; }
    public void setSceneId(int sceneId) { this.sceneId = sceneId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String videoPath) { this.filePath = videoPath; }

    public Double getPacingScore() { return pacingScore; }
    public void setPacingScore(Double pacingScore) { this.pacingScore = pacingScore; }

    public Double getIntensityScore() { return intensityScore; }
    public void setIntensityScore(Double intensityScore) { this.intensityScore = intensityScore; }

    public Double getDialogueDensity() { return dialogueDensity; }
    public void setDialogueDensity(Double dialogueDensity) { this.dialogueDensity = dialogueDensity; }

    public Double getActionLevel() { return actionLevel; }
    public void setActionLevel(Double actionLevel) { this.actionLevel = actionLevel; }

    public Double getCharacterFocus() { return characterFocus; }
    public void setCharacterFocus(Double characterFocus) { this.characterFocus = characterFocus; }

    public Double getEmotionalTone() { return emotionalTone; }
    public void setEmotionalTone(Double emotionalTone) { this.emotionalTone = emotionalTone; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}

