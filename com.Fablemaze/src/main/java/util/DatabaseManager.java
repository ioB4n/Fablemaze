/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package util;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:movie_app.db";
    
    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void initSchema() {
        String createUserTable = """
            DROP TABLE IF EXISTS User;
            Drop TABLE IF EXISTS Movie;
            DROP TABLE IF EXISTS Scene;
            Drop TABLE IF EXISTS SceneVariant;  
            DROP TABLE IF EXISTS DropOff;                   
                                 
            CREATE TABLE IF NOT EXISTS User (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                dob TEXT NOT NULL,
                sex TEXT CHECK(sex IN ('Male', 'Female', 'Other', 'Prefer not to say')),
                openness REAL,
                conscientiousness REAL,
                extraversion REAL,
                agreeableness REAL,
                neuroticism REAL,
                preferred_pacing TEXT CHECK(preferred_pacing IN ('slow', 'medium', 'fast'))
            );
                                 
            CREATE TABLE IF NOT EXISTS Movie (
                movie_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                release_year INTEGER,
                duration INTEGER,
                scene_count INTEGER NOT NULL
            );

            CREATE TABLE IF NOT EXISTS Scene (
                scene_id INTEGER PRIMARY KEY AUTOINCREMENT,
                movie_id INTEGER NOT NULL,
                scene_index INTEGER NOT NULL,
                FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
            );

            CREATE TABLE IF NOT EXISTS SceneVariant (
                variant_id INTEGER PRIMARY KEY AUTOINCREMENT,
                scene_id INTEGER NOT NULL,
                pacing TEXT CHECK(pacing IN ('slow', 'medium', 'fast')),
                tone TEXT CHECK(tone IN ('calm', 'intense', 'neutral')),
                emphasis TEXT, -- e.g. 'dialogue', 'action', etc.
                duration INTEGER NOT NULL,
                video_path TEXT NOT NULL,
                FOREIGN KEY (scene_id) REFERENCES Scene(scene_id)
            );
                                 
            CREATE TABLE IF NOT EXISTS DropOff (
                drop_off_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                variant_id INTEGER NOT NULL,
                drop_off_time TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES User(user_id),
                FOREIGN KEY (variant_id) REFERENCES SceneVariant(variant_id) 
            );
        """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTable);
            System.out.println("Schema initialized.");
        } catch (SQLException e) {
            System.err.println("Schema init error: " + e.getMessage());
        }
    }
}
