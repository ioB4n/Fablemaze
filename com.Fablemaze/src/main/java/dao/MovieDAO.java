/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.Movie;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public boolean insertMovie(Movie movie) {
        String sql = """
            INSERT INTO Movie (title, release_year, duration, scene_count)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, movie.getTitle());

            if (movie.getReleaseYear() != null) {
                stmt.setInt(2, movie.getReleaseYear());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (movie.getDuration() != null) {
                stmt.setInt(3, movie.getDuration());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setInt(4, movie.getSceneCount());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    movie.setMovieId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }

        return false;
    }

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM Movie WHERE movie_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getObject("release_year", Integer.class),
                    rs.getObject("duration", Integer.class),
                    rs.getInt("scene_count")
                );
            }
        } catch (SQLException e) {
            System.err.println("Fetch failed: " + e.getMessage());
        }

        return null;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movie";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getObject("release_year", Integer.class),
                    rs.getObject("duration", Integer.class),
                    rs.getInt("scene_count")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Fetch all failed: " + e.getMessage());
        }

        return movies;
    }
}
