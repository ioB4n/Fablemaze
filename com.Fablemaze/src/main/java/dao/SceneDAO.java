/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.Scene;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SceneDAO {

    public boolean insertScene(Scene scene) {
        String sql = """
            INSERT INTO Scene (movie_id, scene_index)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, scene.getMovieId());
            stmt.setInt(2, scene.getSceneIndex());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    scene.setSceneId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert scene failed: " + e.getMessage());
        }

        return false;
    }

    public Scene getSceneById(int id) {
        String sql = "SELECT * FROM Scene WHERE scene_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Scene(
                    rs.getInt("scene_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("scene_index")
                );
            }

        } catch (SQLException e) {
            System.err.println("Fetch scene failed: " + e.getMessage());
        }

        return null;
    }

    public List<Scene> getScenesByMovieId(int movieId) {
        List<Scene> scenes = new ArrayList<>();
        String sql = "SELECT * FROM Scene WHERE movie_id = ? ORDER BY scene_index";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                scenes.add(new Scene(
                    rs.getInt("scene_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("scene_index")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Fetch scenes by movie failed: " + e.getMessage());
        }

        return scenes;
    }
}
