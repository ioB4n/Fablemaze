/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package dao;

import model.User;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean insertUser(User user) {
        String sql = """
            INSERT INTO User (username, password_hash, dob, sex, openness, conscientiousness, extraversion, agreeableness, neuroticism, preferred_pacing)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getDob());
            stmt.setString(4, user.getSex());
            stmt.setDouble(5, user.getOpenness());
            stmt.setDouble(6, user.getConscientiousness());
            stmt.setDouble(7, user.getExtraversion());
            stmt.setDouble(8, user.getAgreeableness());
            stmt.setDouble(9, user.getNeuroticism());
            stmt.setString(10, user.getPreferredPacing());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }

        return false;
    }
    
    public boolean setTraits(int userId, Double openness, Double agreeableness, Double extraversion, Double neuroticism, Double conscientiousness) {
        String sql = "UPDATE User SET openness = ?, agreeableness = ?, extraversion = ?, neuroticism = ?, conscientiousness = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, openness);
            stmt.setDouble(2, conscientiousness);
            stmt.setDouble(3, extraversion);
            stmt.setDouble(4, agreeableness);
            stmt.setDouble(5, neuroticism);
            stmt.setInt(6, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM User WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("dob"),
                    rs.getString("sex"),
                    rs.getDouble("openness"),
                    rs.getDouble("conscientiousness"),
                    rs.getDouble("extraversion"),
                    rs.getDouble("agreeableness"),
                    rs.getDouble("neuroticism"),
                    rs.getString("preferred_pacing")
                );
            }
        } catch (SQLException e) {
            System.err.println("Fetch failed: " + e.getMessage());
        }

        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("dob"),
                    rs.getString("sex"),
                    rs.getDouble("openness"),
                    rs.getDouble("conscientiousness"),
                    rs.getDouble("extraversion"),
                    rs.getDouble("agreeableness"),
                    rs.getDouble("neuroticism"),
                    rs.getString("preferred_pacing")
                );
            }
        } catch (SQLException e) {
            System.err.println("Fetch failed: " + e.getMessage());
        }
        
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("dob"),
                    rs.getString("sex"),
                    rs.getDouble("openness"),
                    rs.getDouble("conscientiousness"),
                    rs.getDouble("extraversion"),
                    rs.getDouble("agreeableness"),
                    rs.getDouble("neuroticism"),
                    rs.getString("preferred_pacing")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Fetch all failed: " + e.getMessage());
        }

        return users;
    }
}

