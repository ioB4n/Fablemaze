/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class Scene {
    private int sceneId;
    private int movieId;
    private int sceneIndex;

    public Scene(int sceneId, int movieId, int sceneIndex) {
        this.sceneId = sceneId;
        this.movieId = movieId;
        this.sceneIndex = sceneIndex;
    }

    public Scene(int movieId, int sceneIndex) {
        this(0, movieId, sceneIndex);
    }

    public int getSceneId() { return sceneId; }
    public void setSceneId(int sceneId) { this.sceneId = sceneId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getSceneIndex() { return sceneIndex; }
    public void setSceneIndex(int sceneIndex) { this.sceneIndex = sceneIndex; }
}
