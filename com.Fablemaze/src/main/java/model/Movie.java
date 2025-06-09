/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;

public class Movie {
    private int movieId;
    private String title;
    private Integer releaseYear; 
    private Integer duration;
    private String genres;
    private String rating;
    private Double imdbRating;
    private int sceneCount;

    public Movie() {
        this.movieId = 0;
        this.title = "Movie";
        this.releaseYear = 2025;
        this.duration = 120;
        this.genres = "Genre";
        this.rating = "PG";
        this.imdbRating = 5.0;
        this.sceneCount = 5;
    }

    public Movie(int movieId, String title, Integer releaseYear, Integer duration, String genres, 
                 String rating, Double imdbRating, int sceneCount) {
        this.movieId = movieId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.genres = genres;
        this.rating = rating;
        this.imdbRating = imdbRating;
        this.sceneCount = sceneCount;
    }

    public Movie(String title, Integer releaseYear, Integer duration, String genres, 
                 String rating, Double imdbRating, int sceneCount) {
        this(0, title, releaseYear, duration, genres, rating, imdbRating, sceneCount);
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
    
    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Double GetImdbRating() { return imdbRating; }
    public void setImdbRating(Double imdbRating) { this.imdbRating = imdbRating; }

    public int getSceneCount() { return sceneCount; }
    public void setSceneCount(int sceneCount) { this.sceneCount = sceneCount; }
}
