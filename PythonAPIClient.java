/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

    
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class PythonAPIClient {
    
    private final HttpClient client;
    
    public PythonAPIClient() {
        this.client = HttpClient.newHttpClient();
    }
    
    public HttpResponse<String> predictSequence(int userId, int movieId, String deviceType) throws Exception {

        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("movie_id", movieId);
        json.put("device_type", deviceType);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5000/predict_sequence"))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8))
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
        
    }
    
    public void PredictAndPrintSequence(int userId, int movieId, String deviceType) throws Exception {
        
        HttpResponse<String> response = predictSequence(userId, movieId, deviceType);
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        
    }

}

    