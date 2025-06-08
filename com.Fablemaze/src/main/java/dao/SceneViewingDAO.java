/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.SceneViewing;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SceneViewingDAO {

    // Insert a new SceneViewing row
    public boolean insertSceneViewing(SceneViewing viewing) {
        String sql = """
            INSERT INTO SceneViewing (session_id, variant_id, watch_duration, dropped_off, timestamp)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, viewing.getSessionId());
            stmt.setInt(2, viewing.getVariantId());
            stmt.setInt(3, viewing.getWatchDuration());
            stmt.setBoolean(4, viewing.isDroppedOff());
            stmt.setString(5, viewing.getTimestamp());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (Statement idStmt = conn.createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        viewing.setViewingId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert scene viewing failed: " + e.getMessage());
        }

        return false;
    }

    // Get SceneViewing by ID
    public SceneViewing getSceneViewingById(int id) {
        String sql = "SELECT * FROM SceneViewing WHERE viewing_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new SceneViewing(
                    rs.getInt("viewing_id"),
                    rs.getInt("session_id"),
                    rs.getInt("variant_id"),
                    rs.getInt("watch_duration"),
                    rs.getBoolean("dropped_off"),
                    rs.getString("timestamp")
                );
            }

        } catch (SQLException e) {
            System.err.println("Fetch scene viewing by ID failed: " + e.getMessage());
        }

        return null;
    }

    // Get all SceneViewing records
    public List<SceneViewing> getAllSceneViewings() {
        List<SceneViewing> viewings = new ArrayList<>();
        String sql = "SELECT * FROM SceneViewing";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SceneViewing viewing = new SceneViewing(
                    rs.getInt("viewing_id"),
                    rs.getInt("session_id"),
                    rs.getInt("variant_id"),
                    rs.getInt("watch_duration"),
                    rs.getBoolean("dropped_off"),
                    rs.getString("timestamp")
                );
                viewings.add(viewing);
            }

        } catch (SQLException e) {
            System.err.println("Fetch all scene viewings failed: " + e.getMessage());
        }

        return viewings;
    }
}
