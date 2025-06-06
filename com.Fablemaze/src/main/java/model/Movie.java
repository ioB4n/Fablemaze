/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class Movie {
    private int movieId;
    private String title;
    private Integer releaseYear; // Can be null
    private Integer duration;    // Can be null
    private int sceneCount;

    public Movie(int movieId, String title, Integer releaseYear, Integer duration, int sceneCount) {
        this.movieId = movieId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.sceneCount = sceneCount;
    }

    public Movie(String title, Integer releaseYear, Integer duration, int sceneCount) {
        this(0, title, releaseYear, duration, sceneCount);
    }

    // Getters and setters
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public int getSceneCount() { return sceneCount; }
    public void setSceneCount(int sceneCount) { this.sceneCount = sceneCount; }
}
