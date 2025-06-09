from flask import Flask, request, jsonify
from segment_predictor import SegmentSequencePredictor
import traceback

app = Flask(__name__)
predictor = SegmentSequencePredictor()  # initialize the predictor once

@app.route('/predict_sequence', methods=['POST'])
def predict_sequence():
    try:
        data = request.get_json()
        user_id = int(data.get('user_id'))
        movie_id = int(data.get('movie_id'))
        device_type = data.get('device_type', 'desktop')

        result = predictor.predict_segment_sequence(user_id, movie_id, device_type)
        if result:

            variant_sequence = [segment['variant_id'] for segment in result['optimal_sequence']]
            return jsonify({'variant_sequence': variant_sequence}), 200
        else:
            return jsonify({'error': 'Failed to generate optimal sequence'}), 500
    except Exception as e:
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

@app.route('/alternatives', methods=['POST'])
def get_alternatives():
    try:
        data = request.get_json()
        user_id = int(data.get('user_id'))
        movie_id = int(data.get('movie_id'))
        scene_index = int(data.get('scene_index'))
        top_n = int(data.get('top_n', 3))

        alternatives = predictor.get_alternative_variants(user_id, movie_id, scene_index, top_n)
        return jsonify(alternatives), 200
    except Exception as e:
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000, debug=True)
