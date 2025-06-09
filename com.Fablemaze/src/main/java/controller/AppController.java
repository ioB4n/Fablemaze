/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package controller;

import dao.*;
import java.util.ArrayList;
import model.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

public class AppController {
    private final UserDAO userDAO = new UserDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private final SceneDAO sceneDAO = new SceneDAO();
    private final SceneVariantDAO variantDAO = new SceneVariantDAO();
    private final SceneViewingDAO sceneViewingDAO = new SceneViewingDAO();
    private final ViewingSessionDAO viewingSessionDAO = new ViewingSessionDAO();
    
    private User currentUser;
    private Map<String, String> questionToTrait = new HashMap();
    private Map<Integer, String> valueToOption = new HashMap();
    private Map<String, List<Integer>> traitToAnswers = new HashMap();
    
    public AppController() {
        loadQuestions();
    }
    
    private void loadQuestions() {
        questionToTrait.put("I enjoy being the center of attention.", "Extraversion");
        questionToTrait.put("I often feel anxious or worried.", "Neuroticism");
        questionToTrait.put("I enjoy trying out new and different activities.", "Openness");
        questionToTrait.put("I sympathize with others' feelings.", "Agreeableness");
        questionToTrait.put("I pay attention to details and stay organized.", "Conscientiousness");
        
        questionToTrait.put("I feel energized after socializing with others.", "Extraversion");
        questionToTrait.put("I get stressed out easily.", "Neuroticism");
        questionToTrait.put("I am curious about many different things.", "Openness");
        questionToTrait.put("I try to get along with everyone.", "Agreeableness");
        questionToTrait.put("I complete tasks thoroughly and on time.", "Conscientiousness");
        
        valueToOption.put(1, "Strongly disagree");
        valueToOption.put(2, "Disagree");
        valueToOption.put(3, "Neutral");
        valueToOption.put(4, "Agree");
        valueToOption.put(5, "Strongly agree");
    }
    
    public Set<String> getQuestions() {
        return questionToTrait.keySet();
    }
    
    public Map<Integer, String> getOptions() {
        return valueToOption;
    }
    
    public void setAnswers(Map<String, Integer> questionToAnswer) {
        for (Map.Entry<String, Integer> entry : questionToAnswer.entrySet()) {
            String question = entry.getKey();
            Integer answer = entry.getValue();
            
            String trait = questionToTrait.get(question);
            
            if (trait != null) {
                traitToAnswers.putIfAbsent(trait, new ArrayList<>());
                traitToAnswers.get(trait).add(answer);
            }
        }
    }
    
    public void completeProfile() {
        Map<String, Double> traitToScore = new HashMap<>();
        
        for (String trait : traitToAnswers.keySet()) {
            OptionalDouble avg = traitToAnswers.get(trait)
                                               .stream()
                                               .mapToInt(Integer::intValue)
                                               .average();
            traitToScore.put(trait, avg.orElse(0.0));
        }
        
        userDAO.setTraits(
                currentUser.getUserId(),
                traitToScore.getOrDefault("Openness", 0.0) / 5.0,
                traitToScore.getOrDefault("Agreeableness", 0.0) / 5.0,
                traitToScore.getOrDefault("Extraversion", 0.0) / 5.0,
                traitToScore.getOrDefault("Neuroticism", 0.0) / 5.0,
                traitToScore.getOrDefault("Conscientiousness", 0.0) / 5.0
        );
        
        traitToAnswers.clear();
    }
    
    public String login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        
        if (user == null) {
            return "Account not found!";
        }
        
        if (!hashPassword(password).equals(user.getPasswordHash())) {
            return "Invalid password!";
        }
        
        currentUser = user;
        return "Login successful!";
    }

    public String signUp(String username, String password, LocalDate dob, String sex) {
        if (userDAO.getUserByUsername(username) != null) {
            return "Username already in use!";
        }
        
        User user = new User(username, hashPassword(password), dob.toString(), sex);
        
        if (userDAO.insertUser(user)) {
            currentUser = user;
            return "Sign-Up successful!";
        }
        
        return "Something went wrong!";
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = md.digest(password.getBytes());

            // Convert bytes to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }
    
    public List<Movie> getMovies() {
        return movieDAO.getAllMovies();
    }
    
    public List<SceneVariant> getSceneVariantSequence(Movie movie) {
        List<Scene> scenes = sceneDAO.getScenesByMovieId(movie.getMovieId());
        Map<Scene, List<SceneVariant>> scenesToVariants = new HashMap<>();
        List<SceneVariant> sceneVariants = new ArrayList<>();
        
        for (Scene scene : scenes) {
            scenesToVariants.put(scene, variantDAO.getVariantsBySceneId(scene.getSceneId()));
            sceneVariants.add(scenesToVariants.get(scene).get(0));
        }
        
        return sceneVariants;
    }
}
