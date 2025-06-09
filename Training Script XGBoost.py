import sqlite3
import pandas as pd
import numpy as np
import xgboost as xgb
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, roc_auc_score
from sklearn.preprocessing import LabelEncoder
import pickle
import json
from datetime import datetime
import warnings
import os
warnings.filterwarnings('ignore')


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, 'movie_app.db')
MODEL_PATH = os.path.join(BASE_DIR, 'xgboost_model.pkl')
ENCODERS_PATH = os.path.join(BASE_DIR, 'xgboost_encoders.pkl')
FEATURES_PATH = os.path.join(BASE_DIR, 'xgboost_features.json')

class XGBoostMovieRetentionTrainer:
    def __init__(self, db_path=DB_PATH):
        self.db_path = db_path
        self.model = None
        self.label_encoders = {}
        self.feature_columns = []
        
    def connect_db(self):
        """Connect to SQLite database"""
        return sqlite3.connect(self.db_path)
    
    def extract_training_data(self):
        """Extract and prepare training data from database"""
        conn = self.connect_db()
        
        # Complex query to join all relevant tables (updated for your schema)
        query = """
        SELECT 
            -- User features
            u.user_id,
            u.dob,
            u.sex,
            u.openness,
            u.conscientiousness,
            u.extraversion,
            u.agreeableness,
            u.neuroticism,
            u.preferred_pacing,
            u.total_watch_time,
            u.favourite_genres,
            u.avg_session_length,
            
            -- Movie features
            m.movie_id,
            m.release_year,
            m.duration as movie_duration,
            m.genres,
            m.rating as movie_rating,
            m.imdb_rating,
            m.scene_count,
            
            -- Scene features
            sv.variant_id,
            sv.variant_name,
            sv.pacing_score,
            sv.intensity_score,
            sv.dialogue_density,
            sv.action_level,
            sv.character_focus,
            sv.emotional_tone,
            sv.duration as segment_duration,
            
            -- Scene context (position in movie)
            s.scene_index,
            
            -- Session context
            vs.device_type,
            vs.start_time,
            
            -- Viewing behavior (TARGET)
            scv.watch_duration,
            scv.dropped_off,
            
            -- Derived features
            (CAST(scv.watch_duration AS FLOAT) / sv.duration) as completion_ratio,
            (s.scene_index * 1.0 / m.scene_count) as movie_progress
            
        FROM SceneViewing scv
        JOIN ViewingSession vs ON scv.session_id = vs.session_id
        JOIN User u ON vs.user_id = u.user_id
        JOIN SceneVariant sv ON scv.variant_id = sv.variant_id
        JOIN Scene s ON sv.scene_id = s.scene_id
        JOIN Movie m ON s.movie_id = m.movie_id
        WHERE scv.watch_duration > 0  -- Only include segments that were actually watched
        """
        
        df = pd.read_sql_query(query, conn)
        conn.close()
        
        print(f"Extracted {len(df)} training examples")
        return df
    
    def engineer_features(self, df):
        """Create additional features from raw data"""
        
        # Age from date of birth
        current_year = datetime.now().year
        df['age'] = current_year - pd.to_datetime(df['dob']).dt.year
        
        # Time of day when watching (hour)
        df['viewing_hour'] = pd.to_datetime(df['start_time']).dt.hour
        
        # Weekend vs weekday
        df['is_weekend'] = pd.to_datetime(df['start_time']).dt.dayofweek >= 5
        
        # User experience level (total watch time categories)
        df['user_experience'] = pd.cut(df['total_watch_time'], 
                                     bins=[0, 100, 500, 2000, float('inf')], 
                                     labels=['new', 'casual', 'regular', 'heavy'])
        
        # Segment position categories
        df['segment_position'] = pd.cut(df['movie_progress'],
                                      bins=[0, 0.25, 0.5, 0.75, 1.0],
                                      labels=['beginning', 'early', 'middle', 'end'])
        
        # User-content preference alignment
        df['pacing_preference_diff'] = abs(df['preferred_pacing'] - df['pacing_score'])
        
        # Scene intensity vs user personality
        df['intensity_extraversion_match'] = df['intensity_score'] * df['extraversion'] / 10
        
        # Completion ratio categories
        df['completion_category'] = pd.cut(df['completion_ratio'],
                                         bins=[0, 0.1, 0.5, 0.9, 1.0],
                                         labels=['barely_watched', 'partial', 'mostly', 'complete'])
        
        return df
    
    def prepare_features(self, df):
        """Prepare features for machine learning"""
        
        # Select features for training
        feature_columns = [
            # User demographic features
            'age', 'openness', 'conscientiousness', 'extraversion', 
            'agreeableness', 'neuroticism', 'preferred_pacing', 'total_watch_time', 'avg_session_length',
            
            # Content features
            'pacing_score', 'intensity_score', 'dialogue_density', 
            'action_level', 'character_focus', 'emotional_tone', 'segment_duration',
            'release_year', 'movie_duration', 'imdb_rating', 'scene_count',
            
            # Context features
            'scene_index', 'movie_progress', 'viewing_hour',
            
            # Derived features
            'completion_ratio', 'pacing_preference_diff', 'intensity_extraversion_match'
        ]
        
        categorical_columns = [
            'sex', 'variant_name', 'movie_rating',
            'device_type', 'user_experience', 'segment_position', 'genres', 
            'favourite_genres', 'completion_category'
        ]
        
        # Handle missing values
        for col in feature_columns:
            if col in df.columns:
                if df[col].dtype in ['int64', 'float64']:
                    df[col] = df[col].fillna(df[col].median())
                else:
                    mode_val = df[col].mode()
                    df[col] = df[col].fillna(mode_val.iloc[0] if len(mode_val) > 0 else 'unknown')
        
        # Encode categorical variables
        for col in categorical_columns:
            if col in df.columns:
                le = LabelEncoder()
                df[col + '_encoded'] = le.fit_transform(df[col].astype(str))
                self.label_encoders[col] = le
                feature_columns.append(col + '_encoded')
        
        # Boolean features
        df['is_weekend_int'] = df['is_weekend'].astype(int)
        feature_columns.append('is_weekend_int')
        
        self.feature_columns = [col for col in feature_columns if col in df.columns]
        
        return df[self.feature_columns], df['dropped_off']
    
    def train_model(self, X, y):
        """Train the XGBoost model with hyperparameter tuning"""
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )
        
        print(f"Training set: {len(X_train)} examples")
        print(f"Test set: {len(X_test)} examples")
        print(f"Drop-off rate: {y.mean():.2%}")
        
        # Calculate scale_pos_weight for class imbalance
        scale_pos_weight = (y == 0).sum() / (y == 1).sum()
        
        # XGBoost parameters optimized for binary classification
        self.model = xgb.XGBClassifier(
            objective='binary:logistic',
            n_estimators=200,
            max_depth=6,
            learning_rate=0.1,
            subsample=0.8,
            colsample_bytree=0.8,
            scale_pos_weight=scale_pos_weight,  # Handle class imbalance
            random_state=42,
            eval_metric='logloss',
            early_stopping_rounds=20,
            n_jobs=-1
        )
        
        # Train with early stopping
        self.model.fit(
            X_train, y_train,
            eval_set=[(X_test, y_test)],
            verbose=False
        )
        
        # Evaluate model
        y_pred = self.model.predict(X_test)
        y_pred_proba = self.model.predict_proba(X_test)[:, 1]
        
        accuracy = accuracy_score(y_test, y_pred)
        auc_score = roc_auc_score(y_test, y_pred_proba)
        
        print(f"\nModel Performance:")
        print(f"Accuracy: {accuracy:.3f}")
        print(f"AUC Score: {auc_score:.3f}")
        print("\nClassification Report:")
        print(classification_report(y_test, y_pred))

        # Cross-validation using a separate model with no early stopping
        model_for_cv = xgb.XGBClassifier(
            objective='binary:logistic',
            n_estimators=200,
            max_depth=6,
            learning_rate=0.1,
            subsample=0.8,
            colsample_bytree=0.8,
            scale_pos_weight=scale_pos_weight,
            random_state=42,
            eval_metric='logloss',
            n_jobs=-1
        )
        
        # Cross-validation
        cv_scores = cross_val_score(model_for_cv, X_train, y_train, cv=5, scoring='roc_auc')
        print(f"\nCross-validation AUC: {cv_scores.mean():.3f} (+/- {cv_scores.std() * 2:.3f})")
        
        # Feature importance
        feature_importance = pd.DataFrame({
            'feature': self.feature_columns,
            'importance': self.model.feature_importances_
        }).sort_values('importance', ascending=False)
        
        print("\nTop 15 Most Important Features:")
        print(feature_importance.head(15))
        
        return accuracy, auc_score
    
    def save_model(self, model_path=MODEL_PATH, encoders_path=ENCODERS_PATH, 
                   features_path=FEATURES_PATH):
        """Save trained model and preprocessing components"""
        
        # Save model
        with open(model_path, 'wb') as f:
            pickle.dump(self.model, f)
        
        # Save label encoders
        with open(encoders_path, 'wb') as f:
            pickle.dump(self.label_encoders, f)
        
        # Save feature columns
        with open(features_path, 'w') as f:
            json.dump(self.feature_columns, f)
        
        print(f"\nXGBoost Model saved to {model_path}")
        print(f"Encoders saved to {encoders_path}")
        print(f"Features saved to {features_path}")
    
    def run_training_pipeline(self):
        """Complete training pipeline"""
        print("Starting XGBoost Movie Retention Model Training...")
        print("=" * 55)
        
        try:
            # Extract data
            print("1. Extracting training data...")
            df = self.extract_training_data()
            
            if len(df) == 0:
                print("ERROR: No training data found. Make sure you have viewing data in your database.")
                return False
            
            # Engineer features
            print("2. Engineering features...")
            df = self.engineer_features(df)
            
            # Prepare features
            print("3. Preparing features for ML...")
            X, y = self.prepare_features(df)
            
            print(f"Final dataset shape: {X.shape}")
            print(f"Features: {len(self.feature_columns)}")
            
            # Train model
            print("4. Training XGBoost model...")
            accuracy, auc_score = self.train_model(X, y)
            
            # Save model
            print("5. Saving model...")
            self.save_model()
            
            print("\n" + "=" * 55)
            print("XGBOOST TRAINING COMPLETED SUCCESSFULLY!")
            print(f"Final Model Accuracy: {accuracy:.3f}")
            print(f"Final Model AUC: {auc_score:.3f}")
            
            return True
            
        except Exception as e:
            print(f"ERROR during training: {str(e)}")
            import traceback
            traceback.print_exc()
            return False

def main():
    """Main function to run the training"""
    trainer = XGBoostMovieRetentionTrainer(DB_PATH)  # Update path as needed
    success = trainer.run_training_pipeline()
    
    if success:
        print("\nYour XGBoost model is ready! You can now use it for predictions.")
        print("Note: Install XGBoost with: pip install xgboost")
    else:
        print("\nTraining failed. Check your database and data.")

if __name__ == "__main__":
    main()