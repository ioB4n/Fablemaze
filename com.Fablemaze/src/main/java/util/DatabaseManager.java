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
        
        String loadDataSql = """
            -- Insert Movies
            INSERT INTO Movie (title, release_year, duration, genres, rating, imdb_rating, scene_count)
            VALUES 
            ('The Edge of Tomorrow', 2014, 113, '["action", "sci-fi"]', 'PG-13', 7.9, 5),
            ('Whispers in the Dark', 2021, 98, '["drama", "thriller"]', 'R', 7.2, 5),
            ('Adventures of Pixel', 2023, 88, '["animation", "comedy"]', 'PG', 6.8, 5);
            
            -- Insert Scenes (movie_id assumed sequentially from 1 to 3)
            -- Movie 1
            INSERT INTO Scene (movie_id, scene_index) VALUES 
            (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);
            
            -- Movie 2
            INSERT INTO Scene (movie_id, scene_index) VALUES 
            (2, 1), (2, 2), (2, 3), (2, 4), (2, 5);
            
            -- Movie 3
            INSERT INTO Scene (movie_id, scene_index) VALUES 
            (3, 1), (3, 2), (3, 3), (3, 4), (3, 5);
            
            -- Insert SceneVariants
            -- Scene IDs: Movie 1 → 1-5, Movie 2 → 6-10, Movie 3 → 11-15
            
            -- Movie 1 Variants
            INSERT INTO SceneVariant (scene_id, variant_name, file_path, pacing_score, intensity_score, dialogue_density, action_level, character_focus, emotional_tone, duration) VALUES
            (1, 'Alpha', 'edge1_scene1_A.jpg', 7.5, 8.2, 3.1, 9.0, 4.3, 1.2, 22),
            (1, 'Beta',  'edge1_scene1_B.jpg', 6.9, 7.8, 4.2, 8.5, 4.0, 0.8, 23),
            (1, 'Gamma', 'edge1_scene1_C.jpg', 8.1, 8.7, 2.5, 9.3, 3.8, 1.6, 21),
            
            (2, 'Alpha', 'edge1_scene2_A.jpg', 6.4, 6.5, 5.8, 7.2, 5.1, 0.2, 20),
            (2, 'Beta',  'edge1_scene2_B.jpg', 6.7, 6.9, 5.0, 7.0, 5.5, -0.3, 19),
            (2, 'Gamma', 'edge1_scene2_C.jpg', 7.2, 7.1, 4.9, 6.8, 5.2, 0.1, 20),
            
            (3, 'Alpha', 'edge1_scene3_A.jpg', 8.5, 9.0, 3.0, 9.5, 4.0, 2.0, 24),
            (3, 'Beta',  'edge1_scene3_B.jpg', 8.0, 8.7, 3.5, 9.0, 4.1, 1.7, 23),
            (3, 'Gamma', 'edge1_scene3_C.jpg', 8.3, 9.2, 2.8, 9.6, 3.9, 2.2, 25),
            
            (4, 'Alpha', 'edge1_scene4_A.jpg', 5.5, 6.0, 6.5, 6.0, 6.2, -0.5, 19),
            (4, 'Beta',  'edge1_scene4_B.jpg', 5.8, 6.3, 6.0, 6.2, 6.0, -0.2, 18),
            (4, 'Gamma', 'edge1_scene4_C.jpg', 6.1, 6.7, 5.5, 6.4, 5.8, -0.3, 19),
            
            (5, 'Alpha', 'edge1_scene5_A.jpg', 7.0, 7.5, 4.0, 8.0, 4.5, 1.0, 21),
            (5, 'Beta',  'edge1_scene5_B.jpg', 7.3, 7.8, 3.8, 8.2, 4.6, 1.1, 22),
            (5, 'Gamma', 'edge1_scene5_C.jpg', 7.1, 7.6, 4.1, 8.1, 4.4, 0.9, 21);
            
            -- Movie 2 Variants
            INSERT INTO SceneVariant (scene_id, variant_name, file_path, pacing_score, intensity_score, dialogue_density, action_level, character_focus, emotional_tone, duration) VALUES
            (6, 'Alpha', 'whispers_scene1_A.jpg', 4.0, 4.5, 7.5, 2.5, 8.0, -2.0, 20),
            (6, 'Beta',  'whispers_scene1_B.jpg', 3.8, 4.1, 7.2, 2.8, 8.2, -1.5, 21),
            (6, 'Gamma', 'whispers_scene1_C.jpg', 4.2, 4.6, 7.8, 2.6, 8.1, -2.1, 20),
            
            (7, 'Alpha', 'whispers_scene2_A.jpg', 5.0, 5.5, 6.5, 3.5, 7.0, -1.0, 22),
            (7, 'Beta',  'whispers_scene2_B.jpg', 5.2, 5.7, 6.2, 3.7, 6.8, -0.8, 21),
            (7, 'Gamma', 'whispers_scene2_C.jpg', 5.1, 5.6, 6.3, 3.6, 7.1, -0.9, 22),
            
            (8, 'Alpha', 'whispers_scene3_A.jpg', 6.5, 6.0, 5.0, 4.0, 6.0, 0.0, 23),
            (8, 'Beta',  'whispers_scene3_B.jpg', 6.7, 6.2, 5.2, 4.2, 6.2, 0.1, 22),
            (8, 'Gamma', 'whispers_scene3_C.jpg', 6.6, 6.1, 5.1, 4.1, 6.1, -0.1, 23),
            
            (9, 'Alpha', 'whispers_scene4_A.jpg', 4.5, 5.0, 6.8, 3.0, 7.0, -1.2, 21),
            (9, 'Beta',  'whispers_scene4_B.jpg', 4.8, 5.2, 6.5, 3.2, 7.1, -1.3, 22),
            (9, 'Gamma', 'whispers_scene4_C.jpg', 4.6, 5.1, 6.6, 3.1, 7.2, -1.1, 21),
            
            (10, 'Alpha', 'whispers_scene5_A.jpg', 6.0, 6.5, 5.5, 4.5, 6.5, 0.5, 24),
            (10, 'Beta',  'whispers_scene5_B.jpg', 6.2, 6.7, 5.7, 4.7, 6.7, 0.4, 25),
            (10, 'Gamma', 'whispers_scene5_C.jpg', 6.1, 6.6, 5.6, 4.6, 6.6, 0.6, 24);
            
            -- Movie 3 Variants
            INSERT INTO SceneVariant (scene_id, variant_name, file_path, pacing_score, intensity_score, dialogue_density, action_level, character_focus, emotional_tone, duration) VALUES
            (11, 'Alpha', 'pixel_scene1_A.jpg', 7.0, 5.0, 6.5, 5.0, 5.5, 2.0, 20),
            (11, 'Beta',  'pixel_scene1_B.jpg', 7.2, 5.2, 6.3, 5.2, 5.6, 1.8, 21),
            (11, 'Gamma', 'pixel_scene1_C.jpg', 7.1, 5.1, 6.4, 5.1, 5.7, 2.1, 20),
            
            (12, 'Alpha', 'pixel_scene2_A.jpg', 6.0, 4.5, 7.0, 4.5, 6.0, 1.5, 22),
            (12, 'Beta',  'pixel_scene2_B.jpg', 6.3, 4.7, 6.8, 4.7, 6.1, 1.6, 21),
            (12, 'Gamma', 'pixel_scene2_C.jpg', 6.1, 4.6, 6.9, 4.6, 6.2, 1.4, 22),
            
            (13, 'Alpha', 'pixel_scene3_A.jpg', 8.0, 6.0, 5.5, 6.5, 4.5, 2.5, 23),
            (13, 'Beta',  'pixel_scene3_B.jpg', 7.8, 5.8, 5.6, 6.3, 4.6, 2.3, 24),
            (13, 'Gamma', 'pixel_scene3_C.jpg', 7.9, 5.9, 5.7, 6.4, 4.7, 2.4, 23),
            
            (14, 'Alpha', 'pixel_scene4_A.jpg', 5.5, 3.5, 7.5, 3.5, 7.0, 1.0, 20),
            (14, 'Beta',  'pixel_scene4_B.jpg', 5.8, 3.8, 7.3, 3.8, 7.1, 1.1, 19),
            (14, 'Gamma', 'pixel_scene4_C.jpg', 5.6, 3.6, 7.4, 3.6, 7.2, 1.2, 20),
            
            (15, 'Alpha', 'pixel_scene5_A.jpg', 6.5, 4.5, 6.5, 4.5, 6.5, 1.3, 22),
            (15, 'Beta',  'pixel_scene5_B.jpg', 6.8, 4.8, 6.3, 4.8, 6.6, 1.4, 21),
            (15, 'Gamma', 'pixel_scene5_C.jpg', 6.6, 4.6, 6.4, 4.6, 6.7, 1.2, 22);
        """;
        

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            
            for (String sql : createUserTable.split(";")) {
                if (!sql.strip().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            
            // Run this code to populate the database on creation.
            /*
            for (String sql : loadDataSql.split(";")) {
                if (!sql.strip().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            */
            
            System.out.println("Schema initialized.");
        } catch (SQLException e) {
            System.err.println("Schema init error: " + e.getMessage());
        }
    }
}
