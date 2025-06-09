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
        INSERT INTO SceneVariant (
            scene_id, variant_name, file_path,
            pacing_score, intensity_score, dialogue_density,
            action_level, character_focus, emotional_tone, duration
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, variant.getSceneId());
        stmt.setString(2, variant.getVariantName());
        stmt.setString(3, variant.getFilePath());
        stmt.setDouble(4, variant.getPacingScore());
        stmt.setDouble(5, variant.getIntensityScore());
        stmt.setDouble(6, variant.getDialogueDensity());
        stmt.setDouble(7, variant.getActionLevel());
        stmt.setDouble(8, variant.getCharacterFocus());
        stmt.setDouble(9, variant.getEmotionalTone());
        stmt.setInt(10, variant.getDuration());

        int rows = stmt.executeUpdate();
        if (rows > 0) {
            // Retrieve the last inserted ID
            try (Statement idStmt = conn.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    variant.setVariantId(rs.getInt(1));
                }
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
                    rs.getString("variant_name"),
                    rs.getString("file_path"),
                    rs.getDouble("pacing_score"),
                    rs.getDouble("intensity_score"),
                    rs.getDouble("dialogue_density"),
                    rs.getDouble("action_level"),
                    rs.getDouble("character_focus"),
                    rs.getDouble("emotional_tone"),
                    rs.getInt("duration")
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
                    rs.getString("variant_name"),
                    rs.getString("file_path"),
                    rs.getDouble("pacing_score"),
                    rs.getDouble("intensity_score"),
                    rs.getDouble("dialogue_density"),
                    rs.getDouble("action_level"),
                    rs.getDouble("character_focus"),
                    rs.getDouble("emotional_tone"),
                    rs.getInt("duration")
                );
            }

        } catch (SQLException e) {
            System.err.println("Fetch variant by ID failed: " + e.getMessage());
        }

        return null;
    }
}
