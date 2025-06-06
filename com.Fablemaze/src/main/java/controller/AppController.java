/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package controller;

import dao.*;
import java.util.ArrayList;
import model.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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
    private final DropOffDAO dropOffDAO = new DropOffDAO();
    
    private User currentUser;
    private Map<String, String> questionToTrait = new HashMap();
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
        
        questionToTrait.put("I like to start conversations with people.", "Extraversion");
        questionToTrait.put("I have frequent mood swings.", "Neuroticism");
        questionToTrait.put("I enjoy thinking about abstract or philosophical ideas.", "Openness");
        questionToTrait.put("I am considerate and kind to almost everyone.", "Agreeableness");
        questionToTrait.put("I follow through with my plans and goals.", "Conscientiousness");
        
        questionToTrait.put("I prefer spending time with others over being alone.", "Extraversion");
        questionToTrait.put("I often feel sad or down.", "Neuroticism");
        questionToTrait.put("I like to explore unfamiliar places and ideas.", "Openness");
        questionToTrait.put("I go out of my way to help others.", "Agreeableness");
        questionToTrait.put("I avoid careless mistakes.", "Conscientiousness");
    }
    
    public Set<String> getQuestions() {
        return questionToTrait.keySet();
    }
    
    public void setAnswers(HashMap<String, Integer> questionToAnswer) {
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
        return "Login was successful.";
    }

    public String signUp(String username, String password, String dob, String sex) {
        if (userDAO.getUserByUsername(username) != null) {
            return "Username already in use!";
        }
        
        User user = new User(username, hashPassword(password), dob, sex);
        
        if (userDAO.insertUser(user)) {
            currentUser = user;
            return "SignUp was successful.";
        }
        
        return "Something went wrong!";
    }

    public Movie getMovie(int movieId) {
        return movieDAO.getMovieById(movieId);
    }

    public List<Scene> getScenesForMovie(int movieId) {
        return sceneDAO.getScenesByMovieId(movieId);
    }

    public List<SceneVariant> getVariantsForScene(int sceneId) {
        return variantDAO.getVariantsBySceneId(sceneId);
    }

    public boolean recordDropOff(int userId, int variantId, LocalDateTime timestamp) {
        DropOff dropOff = new DropOff(userId, variantId, timestamp);
        return dropOffDAO.insertDropOff(dropOff);
    }

    // Example method to build a personalized film
    public List<SceneVariant> buildMovieForUser(int movieId, User user) {
        List<Scene> scenes = getScenesForMovie(movieId);
        List<SceneVariant> personalizedVariants = new ArrayList<>();

        for (Scene scene : scenes) {
            List<SceneVariant> variants = getVariantsForScene(scene.getSceneId());

            // Very basic selection logic for now
            SceneVariant chosen = chooseBestVariant(variants, user.getPreferredPacing());
            personalizedVariants.add(chosen);
        }

        return personalizedVariants;
    }

    private SceneVariant chooseBestVariant(List<SceneVariant> variants, String preferredPacing) {
        return variants.stream()
                .filter(v -> v.getPacing().equalsIgnoreCase(preferredPacing))
                .findFirst()
                .orElse(variants.get(0)); // fallback
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
}
