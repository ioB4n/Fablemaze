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
            CREATE TABLE IF NOT EXISTS User (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                dob TEXT NOT NULL,
                sex TEXT NOT NULL CHECK(sex IN ('Male', 'Female', 'Non-binary', 'Prefer not to say')),
                openness REAL DEFAULT 0,
                conscientiousness REAL DEFAULT 0,
                extraversion REAL DEFAULT 0,
                agreeableness REAL DEFAULT 0,
                neuroticism REAL DEFAULT 0,
                total_watch_time INTEGER DEFAULT 0,
                preferred_pacing REAL CHECK(preferred_pacing >= 0 AND preferred_pacing <= 10),
                favourite_genres TEXT,
                avg_session_length REAL DEFAULT 0,
                registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP		 
            );

            CREATE TABLE IF NOT EXISTS Movie (
                movie_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                release_year INTEGER,
                duration INTEGER,
                genres TEXT, -- JSON array like ['action', 'drama'],
                rating TEXT CHECK(rating IN ('G', 'PG', 'PG-13', 'R')),
                imdb_rating REAL,
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
                variant_name TEXT,
                file_path TEXT NOT NULL,
                pacing_score REAL CHECK(pacing_score >= 0 AND pacing_score <= 10),
                intensity_score REAL CHECK(intensity_score >= 0 AND intensity_score <= 10), 
                dialogue_density REAL CHECK(dialogue_density >= 0 AND dialogue_density <= 10),
                action_level REAL CHECK(action_level >= 0 AND action_level <= 10),
                character_focus REAL CHECK(character_focus >= 0 AND character_focus <= 10),
                emotional_tone REAL CHECK(emotional_tone >= -5 AND emotional_tone <= 5),
                duration INTEGER NOT NULL,
                FOREIGN KEY (scene_id) REFERENCES Scene(scene_id)
            );

            CREATE TABLE IF NOT EXISTS ViewingSession (
                session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                movie_id INTEGER NOT NULL,
                start_time TIMESTAMP NOT NULL,
                end_time TIMESTAMP,
                device_type TEXT, -- mobile, desktop, tv
                completed BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (user_id) REFERENCES User(user_id),
                FOREIGN KEY (movie_id) REFERENCES Movie(movie_id)
            );

            CREATE TABLE IF NOT EXISTS SceneViewing (
                viewing_id INTEGER PRIMARY KEY AUTOINCREMENT,
                session_id INTEGER NOT NULL,
                variant_id INTEGER NOT NULL,
                watch_duration INTEGER NOT NULL, -- seconds actually watched
                dropped_off BOOLEAN DEFAULT FALSE,
                timestamp TIMESTAMP NOT NULL,
                FOREIGN KEY (session_id) REFERENCES ViewingSession(session_id),
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
