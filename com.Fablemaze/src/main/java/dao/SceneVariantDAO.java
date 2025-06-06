/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.SceneVariant;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SceneVariantDAO {

    public boolean insertSceneVariant(SceneVariant variant) {
        String sql = """
            INSERT INTO SceneVariant (scene_id, pacing, tone, emphasis, duration, video_path)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, variant.getSceneId());
            stmt.setString(2, variant.getPacing());
            stmt.setString(3, variant.getTone());
            stmt.setString(4, variant.getEmphasis());
            stmt.setInt(5, variant.getDuration());
            stmt.setString(6, variant.getVideoPath());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    variant.setVariantId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert scene variant failed: " + e.getMessage());
        }

        return false;
    }

    public List<SceneVariant> getVariantsBySceneId(int sceneId) {
        List<SceneVariant> variants = new ArrayList<>();
        String sql = "SELECT * FROM SceneVariant WHERE scene_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sceneId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                variants.add(new SceneVariant(
                    rs.getInt("variant_id"),
                    rs.getInt("scene_id"),
                    rs.getString("pacing"),
                    rs.getString("tone"),
                    rs.getString("emphasis"),
                    rs.getInt("duration"),
                    rs.getString("video_path")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Fetch scene variants failed: " + e.getMessage());
        }

        return variants;
    }

    public SceneVariant getVariantById(int id) {
        String sql = "SELECT * FROM SceneVariant WHERE variant_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new SceneVariant(
                    rs.getInt("variant_id"),
                    rs.getInt("scene_id"),
                    rs.getString("pacing"),
                    rs.getString("tone"),
                    rs.getString("emphasis"),
                    rs.getInt("duration"),
                    rs.getString("video_path")
                );
            }

        } catch (SQLException e) {
            System.err.println("Fetch variant by ID failed: " + e.getMessage());
        }

        return null;
    }
}
