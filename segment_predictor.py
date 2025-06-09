import sqlite3
import pandas as pd
import numpy as np
import pickle
import json
import sys
from datetime import datetime
import warnings
import os
warnings.filterwarnings('ignore')

class SegmentSequencePredictor:

    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    DB_PATH = os.path.join(BASE_DIR, 'movie_app.db')
    MODEL_PATH = os.path.join(BASE_DIR, 'xgboost_model.pkl')
    ENCODERS_PATH = os.path.join(BASE_DIR, 'xgboost_encoders.pkl')
    FEATURES_PATH = os.path.join(BASE_DIR, 'xgboost_features.json')

    def __init__(self, db_path=DB_PATH, 
                 model_path=MODEL_PATH,
                 encoders_path=ENCODERS_PATH,
                 features_path=FEATURES_PATH):
        self.db_path = db_path
        self.model = None
        self.label_encoders = {}
        self.feature_columns = []
        
        # Load trained model and preprocessing components
        self.load_model(model_path, encoders_path, features_path)
    
    def load_model(self, model_path, encoders_path, features_path):
        """Load trained model and preprocessing components"""
        try:
            # Load model
            with open(model_path, 'rb') as f:
                self.model = pickle.load(f)
            
            # Load encoders
            with open(encoders_path, 'rb') as f:
                self.label_encoders = pickle.load(f)
            
            # Load feature columns
            with open(features_path, 'r') as f:
                self.feature_columns = json.load(f)
                
            print(f"Model loaded successfully. Features: {len(self.feature_columns)}")
            
        except Exception as e:
            print(f"Error loading model: {e}")
            raise
    
    def connect_db(self):
        """Connect to SQLite database"""
        return sqlite3.connect(self.db_path)
    
    def get_user_profile(self, user_id):
        """Get user profile and historical behavior with defaults for missing data"""
        conn = self.connect_db()
        
        # Get user basic info with COALESCE for nullable fields
        user_query = """
        SELECT 
            user_id, 
            dob, 
            COALESCE(sex, 'Other') as sex,
            COALESCE(openness, 5.0) as openness,
            COALESCE(conscientiousness, 5.0) as conscientiousness,
            COALESCE(extraversion, 5.0) as extraversion,
            COALESCE(agreeableness, 5.0) as agreeableness,
            COALESCE(neuroticism, 5.0) as neuroticism,
            COALESCE(preferred_pacing, 5.0) as preferred_pacing,
            COALESCE(total_watch_time, 0) as total_watch_time,
            COALESCE(favourite_genres, 'drama') as favourite_genres,
            COALESCE(avg_session_length, 60) as avg_session_length,
            registration_date
        FROM User WHERE user_id = ?
        """
        
        user_df = pd.read_sql_query(user_query, conn, params=[user_id])
        
        if len(user_df) == 0:
            conn.close()
            raise ValueError(f"User {user_id} not found")
        
        # Get user's historical viewing patterns (empty DataFrame is fine for new users)
        history_query = """
        SELECT sv.pacing_score, sv.intensity_score, sv.dialogue_density,
            sv.action_level, sv.character_focus, sv.emotional_tone,
            scv.dropped_off, 
            (CAST(scv.watch_duration AS FLOAT) / sv.duration) as completion_ratio
        FROM SceneViewing scv
        JOIN ViewingSession vs ON scv.session_id = vs.session_id
        JOIN SceneVariant sv ON scv.variant_id = sv.variant_id
        WHERE vs.user_id = ?
        """
        
        history_df = pd.read_sql_query(history_query, conn, params=[user_id])
        conn.close()
        
        return user_df.iloc[0], history_df
    
    def get_movie_segments(self, movie_id):
        """Get all available segments for a movie"""
        conn = self.connect_db()
        
        query = """
        SELECT m.movie_id, m.title, m.release_year, m.duration as movie_duration,
               m.genres, m.rating as movie_rating, m.imdb_rating, m.scene_count,
               s.scene_id, s.scene_index,
               sv.variant_id, sv.variant_name, sv.pacing_score, sv.intensity_score,
               sv.dialogue_density, sv.action_level, sv.character_focus,
               sv.emotional_tone, sv.duration as segment_duration, sv.file_path
        FROM Movie m
        JOIN Scene s ON m.movie_id = s.movie_id
        JOIN SceneVariant sv ON s.scene_id = sv.scene_id
        WHERE m.movie_id = ?
        ORDER BY s.scene_index, sv.variant_id
        """
        
        segments_df = pd.read_sql_query(query, conn, params=[movie_id])
        conn.close()
        
        if len(segments_df) == 0:
            raise ValueError(f"Movie {movie_id} not found or has no segments")
        
        return segments_df
    
    def create_prediction_features(self, user_profile, user_history, segments_df):
        """Create features for each segment prediction with robust defaults"""
        predictions_data = []
        
        # Calculate user preferences from history or use sensible defaults
        if len(user_history) > 0:
            # User's preferred content characteristics (from non-dropped segments)
            good_segments = user_history[user_history['dropped_off'] == False]
            if len(good_segments) > 0:
                avg_preferred_pacing = good_segments['pacing_score'].mean()
                avg_preferred_intensity = good_segments['intensity_score'].mean()
                avg_preferred_dialogue = good_segments['dialogue_density'].mean()
                avg_preferred_action = good_segments['action_level'].mean()
            else:
                # Fallback to user's stated preference or neutral
                avg_preferred_pacing = user_profile.get('preferred_pacing', 5.0)
                avg_preferred_intensity = 5.0
                avg_preferred_dialogue = 5.0
                avg_preferred_action = 5.0
        else:
            # New user - use neutral preferences
            avg_preferred_pacing = user_profile.get('preferred_pacing', 5.0)
            avg_preferred_intensity = 5.0
            avg_preferred_dialogue = 5.0
            avg_preferred_action = 5.0
        
        # Current time for context
        current_time = datetime.now()
        current_hour = current_time.hour
        is_weekend = current_time.weekday() >= 5
        
        # User age with default (25 if dob missing)
        try:
            age = current_time.year - pd.to_datetime(user_profile['dob']).year
        except:
            age = 25  # default age if dob is missing or invalid
        
        # User experience level (simplified for new users)
        total_watch_time = user_profile.get('total_watch_time', 0)
        user_experience = 'new'  # All new users start as 'new'
        
        # Create features for each segment
        for _, segment in segments_df.iterrows():
            # Movie progress
            movie_progress = segment['scene_index'] / segment['scene_count']
            
            # Segment position category
            if movie_progress <= 0.25:
                segment_position = 'beginning'
            elif movie_progress <= 0.5:
                segment_position = 'early'
            elif movie_progress <= 0.75:
                segment_position = 'middle'
            else:
                segment_position = 'end'
            
            # Feature vector for this segment with safe defaults
            features = {
                # User features
                'age': age,
                'openness': user_profile.get('openness', 5.0),
                'conscientiousness': user_profile.get('conscientiousness', 5.0),
                'extraversion': user_profile.get('extraversion', 5.0),
                'agreeableness': user_profile.get('agreeableness', 5.0),
                'neuroticism': user_profile.get('neuroticism', 5.0),
                'preferred_pacing': user_profile.get('preferred_pacing', 5.0),
                'total_watch_time': total_watch_time,
                'avg_session_length': user_profile.get('avg_session_length', 60),
                
                # Content features (from segment)
                'pacing_score': segment['pacing_score'],
                'intensity_score': segment['intensity_score'],
                'dialogue_density': segment['dialogue_density'],
                'action_level': segment['action_level'],
                'character_focus': segment['character_focus'],
                'emotional_tone': segment['emotional_tone'],
                'segment_duration': segment['segment_duration'],
                'release_year': segment['release_year'],
                'movie_duration': segment['movie_duration'],
                'imdb_rating': segment.get('imdb_rating', 7.0),
                'scene_count': segment['scene_count'],
                
                # Context features
                'scene_index': segment['scene_index'],
                'movie_progress': movie_progress,
                'viewing_hour': current_hour,
                
                # Derived features
                'completion_ratio': 1.0,
                'pacing_preference_diff': abs(user_profile.get('preferred_pacing', 5.0)) - segment['pacing_score'],
                'intensity_extraversion_match': segment['intensity_score'] * user_profile.get('extraversion', 5.0) / 10,
                
                # Categorical features with defaults
                'sex': user_profile.get('sex', 'Other'),
                'variant_name': segment.get('variant_name', 'standard'),
                'movie_rating': segment.get('movie_rating', 'PG-13'),
                'device_type': 'desktop',
                'user_experience': user_experience,
                'segment_position': segment_position,
                'genres': segment.get('genres', 'drama'),
                'favourite_genres': user_profile.get('favourite_genres', 'drama'),
                'completion_category': 'complete',
                'is_weekend_int': int(is_weekend),
                
                # Metadata for selection
                'variant_id': segment['variant_id'],
                'scene_id': segment['scene_id'],
                'scene_index': segment['scene_index'],
                'file_path': segment['file_path'],
                'title': segment['title']
            }
            
            predictions_data.append(features)
        
        return pd.DataFrame(predictions_data)
    
    def preprocess_features(self, df):
        """Apply same preprocessing as training"""
        # Encode categorical variables
        categorical_columns = [
            'sex', 'variant_name', 'movie_rating', 'device_type', 
            'user_experience', 'segment_position', 'genres', 
            'favourite_genres', 'completion_category'
        ]
        
        for col in categorical_columns:
            if col in df.columns and col in self.label_encoders:
                # Handle unknown categories
                def safe_transform(x):
                    try:
                        return self.label_encoders[col].transform([str(x)])[0]
                    except ValueError:
                        # Unknown category - use most frequent class
                        return 0
                
                df[col + '_encoded'] = df[col].apply(safe_transform)
        
        # Select only the features used in training
        feature_data = pd.DataFrame()
        for col in self.feature_columns:
            if col in df.columns:
                feature_data[col] = df[col]
            else:
                # Handle missing features with default values
                if col.endswith('_encoded'):
                    feature_data[col] = 0
                else:
                    feature_data[col] = 0
        
        # Fill any remaining NaN values
        feature_data = feature_data.fillna(0)
        
        return feature_data
    
    def predict_segment_sequence(self, user_id, movie_id, device_type='desktop'):
        """Predict optimal segment sequence for a user and movie"""
        
        print(f"Generating optimal segment sequence for User {user_id}, Movie {movie_id}")
        print("=" * 60)
        
        try:
            # Get user profile and history
            print("1. Loading user profile...")
            user_profile, user_history = self.get_user_profile(user_id)
            print(f"   User: {user_profile['sex']}, Age: {datetime.now().year - pd.to_datetime(user_profile['dob']).year}")
            print(f"   Viewing History: {len(user_history)} segments")
            
            # Get movie segments
            print("2. Loading movie segments...")
            segments_df = self.get_movie_segments(movie_id)
            movie_title = segments_df.iloc[0]['title']
            unique_scenes = segments_df['scene_index'].nunique()
            total_variants = len(segments_df)
            print(f"   Movie: {movie_title}")
            print(f"   Scenes: {unique_scenes}, Total variants: {total_variants}")
            
            # Create prediction features
            print("3. Creating prediction features...")
            prediction_df = self.create_prediction_features(user_profile, user_history, segments_df)
            prediction_df['device_type'] = device_type  # Override device type
            
            # Preprocess features
            print("4. Preprocessing features...")
            feature_data = self.preprocess_features(prediction_df)
            
            # Make predictions
            print("5. Making dropout predictions...")
            dropout_probabilities = self.model.predict_proba(feature_data)[:, 1]
            engagement_scores = 1 - dropout_probabilities  # Higher score = less likely to drop off
            
            # Add predictions to dataframe
            prediction_df['dropout_probability'] = dropout_probabilities
            prediction_df['engagement_score'] = engagement_scores
            
            # Select best variant for each scene
            print("6. Selecting optimal variants...")
            optimal_sequence = []
            
            # Group by scene and select variant with highest engagement score
            for scene_idx in sorted(prediction_df['scene_index'].unique()):
                scene_variants = prediction_df[prediction_df['scene_index'] == scene_idx]
                best_variant = scene_variants.loc[scene_variants['engagement_score'].idxmax()]
                
                optimal_sequence.append({
                    'scene_index': int(best_variant['scene_index']),
                    'scene_id': int(best_variant['scene_id']),
                    'variant_id': int(best_variant['variant_id']),
                    'variant_name': best_variant['variant_name'],
                    'file_path': best_variant['file_path'],
                    'engagement_score': float(best_variant['engagement_score']),
                    'dropout_probability': float(best_variant['dropout_probability']),
                    'segment_duration': float(best_variant['segment_duration'])
                })
            
            # Calculate sequence statistics
            avg_engagement = np.mean([s['engagement_score'] for s in optimal_sequence])
            total_duration = sum([s['segment_duration'] for s in optimal_sequence])
            
            print("\n" + "=" * 60)
            print("OPTIMAL SEGMENT SEQUENCE GENERATED")
            print(f"Average Engagement Score: {avg_engagement:.3f}")
            print(f"Total Duration: {total_duration/60:.1f} minutes")
            print(f"Sequences: {len(optimal_sequence)} segments")
            
            # Show top and bottom segments
            print("\nTop 3 Most Engaging Segments:")
            top_segments = sorted(optimal_sequence, key=lambda x: x['engagement_score'], reverse=True)[:3]
            for i, seg in enumerate(top_segments, 1):
                print(f"  {i}. Scene {seg['scene_index']}: {seg['variant_name']} "
                      f"(Engagement: {seg['engagement_score']:.3f})")
            
            print("\nTop 3 Riskiest Segments:")
            risky_segments = sorted(optimal_sequence, key=lambda x: x['engagement_score'])[:3]
            for i, seg in enumerate(risky_segments, 1):
                print(f"  {i}. Scene {seg['scene_index']}: {seg['variant_name']} "
                      f"(Engagement: {seg['engagement_score']:.3f})")
            
            return {
                'user_id': user_id,
                'movie_id': movie_id,
                'movie_title': movie_title,
                'optimal_sequence': optimal_sequence,
                'avg_engagement_score': avg_engagement,
                'total_duration': total_duration,
                'generated_at': datetime.now().isoformat()
            }
            
        except Exception as e:
            print(f"ERROR: {str(e)}")
            import traceback
            traceback.print_exc()
            return None
    
    def get_alternative_variants(self, user_id, movie_id, scene_index, top_n=3):
        """Get alternative variants for a specific scene with their engagement scores"""
        
        try:
            # Get user profile and segments
            user_profile, user_history = self.get_user_profile(user_id)
            segments_df = self.get_movie_segments(movie_id)
            
            # Filter to specific scene
            scene_segments = segments_df[segments_df['scene_index'] == scene_index]
            
            if len(scene_segments) == 0:
                return None
            
            # Create prediction features
            prediction_df = self.create_prediction_features(user_profile, user_history, scene_segments)
            
            # Preprocess and predict
            feature_data = self.preprocess_features(prediction_df)
            dropout_probabilities = self.model.predict_proba(feature_data)[:, 1]
            engagement_scores = 1 - dropout_probabilities
            
            # Create results
            alternatives = []
            for i, (_, segment) in enumerate(scene_segments.iterrows()):
                alternatives.append({
                    'variant_id': int(segment['variant_id']),
                    'variant_name': segment['variant_name'],
                    'engagement_score': float(engagement_scores[i]),
                    'dropout_probability': float(dropout_probabilities[i]),
                    'file_path': segment['file_path'],
                    'pacing_score': float(segment['pacing_score']),
                    'intensity_score': float(segment['intensity_score']),
                    'action_level': float(segment['action_level'])
                })
            
            # Sort by engagement score
            alternatives.sort(key=lambda x: x['engagement_score'], reverse=True)
            
            return alternatives[:top_n]
            
        except Exception as e:
            print(f"Error getting alternatives: {e}")
            return None

def main():
    """Example usage of the predictor"""
    
    if len(sys.argv) < 3:
        print("Usage: python segment_predictor.py <user_id> <movie_id> [device_type]")
        print("Example: python segment_predictor.py 1 1 desktop")
        return
    
    user_id = int(sys.argv[1])
    movie_id = int(sys.argv[2])
    device_type = sys.argv[3] if len(sys.argv) > 3 else 'desktop'
    
    # Initialize predictor
    try:
        predictor = SegmentSequencePredictor()
    except Exception as e:
        print(f"Failed to load model: {e}")
        print("Make sure you have trained the model first using the training script.")
        return
    
    # Generate optimal sequence
    result = predictor.predict_segment_sequence(user_id, movie_id, device_type)
    
    if result:
        # Save to file
        filename = f"optimal_sequence_user{user_id}_movie{movie_id}.json"
        
        print(f"\nOptimal sequence generated and saved to {filename}")
        
        # Show some alternative variants for the first scene
        print("\nAlternative variants for Scene 1:")
        alternatives = predictor.get_alternative_variants(user_id, movie_id, 1)
        if alternatives:
            for i, alt in enumerate(alternatives, 1):
                print(f"  {i}. {alt['variant_name']} - Engagement: {alt['engagement_score']:.3f}")
    else:
        print("Failed to generate optimal sequence.")