/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.ViewingSession;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewingSessionDAO {

    public boolean insertViewingSession(ViewingSession session) {
        String sql = """
            INSERT INTO ViewingSession (user_id, movie_id, start_time, end_time, device_type, completed)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, session.getUserId());
            stmt.setInt(2, session.getMovieId());
            stmt.setString(3, session.getStartTime());
            stmt.setString(4, session.getEndTime());
            stmt.setString(5, session.getDeviceType());
            stmt.setBoolean(6, session.isCompleted());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Retrieve the last inserted ID
                try (Statement idStmt = conn.createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        session.setSessionId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert viewing session failed: " + e.getMessage());
        }

        return false;
    }

    // Retrieve a session by ID
    public ViewingSession getViewingSessionById(int id) {
        String sql = "SELECT * FROM ViewingSession WHERE session_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ViewingSession(
                    rs.getInt("session_id"),
                    rs.getInt("user_id"),
                    rs.getInt("movie_id"),
                    rs.getString("start_time"),
                    rs.getString("end_time"),
                    rs.getString("device_type"),
                    rs.getBoolean("completed")
                );
            }

        } catch (SQLException e) {
            System.err.println("Fetch session by ID failed: " + e.getMessage());
        }

        return null;
    }

    // Retrieve all sessions
    public List<ViewingSession> getAllViewingSessions() {
        List<ViewingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM ViewingSession";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ViewingSession session = new ViewingSession(
                    rs.getInt("session_id"),
                    rs.getInt("user_id"),
                    rs.getInt("movie_id"),
                    rs.getString("start_time"),
                    rs.getString("end_time"),
                    rs.getString("device_type"),
                    rs.getBoolean("completed")
                );
                sessions.add(session);
            }

        } catch (SQLException e) {
            System.err.println("Fetch all viewing sessions failed: " + e.getMessage());
        }

        return sessions;
    }
}