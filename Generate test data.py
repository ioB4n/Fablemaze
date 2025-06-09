import sqlite3
import random
import json
from datetime import datetime, timedelta
import faker
import numpy as np
import sqlite3
import os

faker = faker.Faker()
random.seed(42)
np.random.seed(42)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, 'movie_app.db')
NUM_USERS = 100
NUM_MOVIES = 10
SCENES_PER_MOVIE = 10
VARIANTS_PER_SCENE = 3
SESSIONS_PER_USER = 8

GENRES = ['action', 'drama', 'comedy', 'thriller', 'sci-fi', 'romance']
SEXES = ['Male', 'Female', 'Non-binary', 'Prefer not to say']
RATINGS = ['G', 'PG', 'PG-13', 'R']
DEVICE_TYPES = ['mobile', 'desktop', 'tv']

def setup_database(conn):
    schema_sql = """
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
    """
    conn.executescript(schema_sql)
    conn.commit()


def generate_user(conn):
    users = []
    for _ in range(NUM_USERS):
        dob = faker.date_of_birth(minimum_age=18, maximum_age=60)
        username = faker.unique.user_name()
        user = (
            username,
            'hashed_pw',  # fake password
            dob.strftime('%Y-%m-%d'),
            random.choice(SEXES),
            round(random.uniform(0, 1), 2),
            round(random.uniform(0, 1), 2),
            round(random.uniform(0, 1), 2),
            round(random.uniform(0, 1), 2),
            round(random.uniform(0, 1), 2),
            random.randint(0, 5000),
            round(random.uniform(0, 10), 2),
            json.dumps(random.sample(GENRES, k=random.randint(1, 3))),
            round(random.uniform(5, 40), 2),
            datetime.now().isoformat()
        )
        users.append(user)
    conn.executemany("""
    INSERT INTO User (
        username, password_hash, dob, sex,
        openness, conscientiousness, extraversion, agreeableness, neuroticism,
        total_watch_time, preferred_pacing, favourite_genres, avg_session_length, registration_date
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, users)
    conn.commit()


def generate_movies_scenes_variants(conn):
    for m_id in range(NUM_MOVIES):
        title = f"Movie_{m_id}"
        year = random.randint(1990, 2022)
        duration = random.randint(60, 150)
        genres = json.dumps(random.sample(GENRES, k=2))
        rating = random.choice(RATINGS)
        imdb = round(random.uniform(4, 9), 1)
        scene_count = SCENES_PER_MOVIE

        cursor = conn.execute("""
        INSERT INTO Movie (title, release_year, duration, genres, rating, imdb_rating, scene_count)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """, (title, year, duration, genres, rating, imdb, scene_count))
        movie_id = cursor.lastrowid

        for i in range(SCENES_PER_MOVIE):
            cursor = conn.execute("""
            INSERT INTO Scene (movie_id, scene_index)
            VALUES (?, ?)
            """, (movie_id, i))
            scene_id = cursor.lastrowid

            for v in range(VARIANTS_PER_SCENE):
                name = f"Variant_{v}"
                variant = (
                    scene_id, name,
                    f"/path/to/{title}_scene{i}_v{v}.mp4",
                    round(random.uniform(0, 10), 2),
                    round(random.uniform(0, 10), 2),
                    round(random.uniform(0, 10), 2),
                    round(random.uniform(0, 10), 2),
                    round(random.uniform(0, 10), 2),
                    round(random.uniform(-5, 5), 2),
                    random.randint(20, 120)
                )
                conn.execute("""
                INSERT INTO SceneVariant (
                    scene_id, variant_name, file_path,
                    pacing_score, intensity_score, dialogue_density,
                    action_level, character_focus, emotional_tone, duration
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, variant)
    conn.commit()

def compute_affinity(user, variant):
    openness, conscientiousness, extraversion, agreeableness, neuroticism = user
    pacing = variant['pacing_score']
    intensity = variant['intensity_score']
    dialogue = variant['dialogue_density']
    action = variant['action_level']
    character = variant['character_focus']
    emotion = variant['emotional_tone']
    duration = variant['duration']

    affinity = (
        openness * (dialogue + emotion) +
        conscientiousness * (10 - abs(pacing - 5)) +  # prefers medium pacing
        extraversion * (intensity + action) +
        agreeableness * (character + dialogue) +
        (10 - neuroticism) * (10 - abs(emotion - 5))  # less likely to dislike emotional extremes
    )
    return affinity

def generate_viewings(conn):
    user_data = conn.execute("SELECT user_id, openness, conscientiousness, extraversion, agreeableness, neuroticism FROM User").fetchall()
    movie_ids = [row[0] for row in conn.execute("SELECT movie_id FROM Movie").fetchall()]

    for u_id, *personality in user_data:
        for _ in range(SESSIONS_PER_USER):
            movie_id = random.choice(movie_ids)
            start = faker.date_time_between(start_date='-30d', end_date='now')
            end = start + timedelta(minutes=random.randint(10, 120))
            device = random.choice(DEVICE_TYPES)
            completed = random.choice([True, False])

            cursor = conn.execute("""
            INSERT INTO ViewingSession (user_id, movie_id, start_time, end_time, device_type, completed)
            VALUES (?, ?, ?, ?, ?, ?)
            """, (u_id, movie_id, start, end, device, completed))
            session_id = cursor.lastrowid

            # Fetch scene-variant tuples for this movie
            scene_variants = conn.execute("""
            SELECT s.scene_id, sv.variant_id, sv.pacing_score, sv.intensity_score,
                   sv.dialogue_density, sv.action_level, sv.character_focus,
                   sv.emotional_tone, sv.duration
            FROM Scene s
            JOIN SceneVariant sv ON s.scene_id = sv.scene_id
            WHERE s.movie_id = ?
            """, (movie_id,)).fetchall()

            # Group by scene_id
            scenes = {}
            for sv in scene_variants:
                scene_id = sv[0]
                variant = dict(zip([
                    'scene_id', 'variant_id', 'pacing_score', 'intensity_score',
                    'dialogue_density', 'action_level', 'character_focus',
                    'emotional_tone', 'duration'
                ], sv))
                scenes.setdefault(scene_id, []).append(variant)

            for scene_id, variants in scenes.items():
                # Score each variant by personality alignment
                scored = [(compute_affinity(personality, v), v) for v in variants]
                scored.sort(reverse=True)
                best_variant = scored[0][1]

                # Compute drop-off risk based on neuroticism and intensity/emotion
                drop_risk = (
                    personality[4] * 0.1 +  # neuroticism
                    abs(best_variant['emotional_tone'] - 5) * 0.05 +
                    best_variant['intensity_score'] * 0.05
                )
                dropped = random.random() < drop_risk

                watch_time = int(best_variant['duration'] * (0.5 if dropped else random.uniform(0.8, 1.0)))
                timestamp = start + timedelta(seconds=random.randint(0, 3600))

                conn.execute("""
                INSERT INTO SceneViewing (
                    session_id, variant_id, watch_duration, dropped_off, timestamp
                ) VALUES (?, ?, ?, ?, ?)
                """, (
                    session_id, best_variant['variant_id'],
                    watch_time, dropped, timestamp
                ))
    conn.commit()


def populate_data(conn):
    print("Generating Users...")
    generate_user(conn)
    print("Generating Movies, Scenes, and Variants...")
    generate_movies_scenes_variants(conn)
    print("Generating Viewing Sessions and Scene Viewings...")
    generate_viewings(conn)
    conn.close()
    print("Done! Database populated with synthetic data.")

if __name__ == '__main__':
    conn = sqlite3.connect(DB_PATH)
    setup_database(conn)
    populate_data(conn)