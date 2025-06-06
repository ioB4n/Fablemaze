/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.DropOff;
import util.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DropOffDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public boolean insertDropOff(DropOff dropOff) {
        String sql = """
            INSERT INTO DropOff (user_id, variant_id, drop_off_time)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, dropOff.getUserId());
            stmt.setInt(2, dropOff.getVariantId());
            stmt.setString(3, dropOff.getDropOffTime().format(FORMATTER));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    dropOff.setDropOffId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert drop-off failed: " + e.getMessage());
        }

        return false;
    }

    public List<DropOff> getDropOffsByUser(int userId) {
        List<DropOff> dropOffs = new ArrayList<>();
        String sql = "SELECT * FROM DropOff WHERE user_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dropOffs.add(new DropOff(
                    rs.getInt("drop_off_id"),
                    rs.getInt("user_id"),
                    rs.getInt("variant_id"),
                    LocalDateTime.parse(rs.getString("drop_off_time"), FORMATTER)
                ));
            }

        } catch (SQLException e) {
            System.err.println("Fetch drop-offs failed: " + e.getMessage());
        }

        return dropOffs;
    }

    public List<DropOff> getDropOffsByVariant(int variantId) {
        List<DropOff> dropOffs = new ArrayList<>();
        String sql = "SELECT * DropOff WHERE variant_id = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, variantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                dropOffs.add(new DropOff(
                    rs.getInt("drop_off_id"),
                    rs.getInt("user_id"),
                    rs.getInt("variant_id"),
                    LocalDateTime.parse(rs.getString("drop_off_time"), FORMATTER)
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Fetch drop-offs failed: " + e.getMessage());
        }
        
        return dropOffs;
    }
}