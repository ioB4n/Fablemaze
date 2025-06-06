/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class SceneVariant {
    private int variantId;
    private int sceneId;
    private String pacing;
    private String tone;
    private String emphasis;
    private int duration;
    private String videoPath;

    public SceneVariant(int variantId, int sceneId, String pacing, String tone, String emphasis, int duration, String videoPath) {
        this.variantId = variantId;
        this.sceneId = sceneId;
        this.pacing = pacing;
        this.tone = tone;
        this.emphasis = emphasis;
        this.duration = duration;
        this.videoPath = videoPath;
    }

    public SceneVariant(int sceneId, String pacing, String tone, String emphasis, int duration, String videoPath) {
        this(0, sceneId, pacing, tone, emphasis, duration, videoPath);
    }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getSceneId() { return sceneId; }
    public void setSceneId(int sceneId) { this.sceneId = sceneId; }

    public String getPacing() { return pacing; }
    public void setPacing(String pacing) { this.pacing = pacing; }

    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }

    public String getEmphasis() { return emphasis; }
    public void setEmphasis(String emphasis) { this.emphasis = emphasis; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
}
