/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ui;

import util.DatabaseManager;
import controller.AppController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUI1 extends Application {
    
    private Stage primaryStage;
    private Scene loginScene;
    private Scene profileScene;
    
    private AppController controller;
    
    // User data storage (in real app, this would be a database)
    private List<User> users = new ArrayList<>();
    private User currentUser;
    
    @Override
    public void start(Stage stage) {
        DatabaseManager.initSchema();
        controller = new AppController();
        
        primaryStage = stage;
        primaryStage.setTitle("User Registration & Profile System");
        
        // Create login/signup scene
        createLoginScene();
        
        // Create profile creation scene
        createProfileScene();
        
        // Start with login scene
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    
    private void createLoginScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Welcome");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Create tab pane for login/signup
        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(400);
        
        // Login Tab
        Tab loginTab = new Tab("Login");
        loginTab.setClosable(false);
        VBox loginContent = createLoginTab();
        loginTab.setContent(loginContent);
        
        // Signup Tab
        Tab signupTab = new Tab("Sign Up");
        signupTab.setClosable(false);
        VBox signupContent = createSignupTab();
        signupTab.setContent(signupContent);
        
        tabPane.getTabs().addAll(loginTab, signupTab);
        
        root.getChildren().addAll(titleLabel, tabPane);
        loginScene = new Scene(root, 500, 600);
    }
    
    private VBox createLoginTab() {
        VBox loginBox = new VBox(15);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        loginButton.setPrefWidth(300);
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                return;
            }
            
            // Check credentials
            User user = findUser(username, password);
            if (user != null) {
                currentUser = user;
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");
                // In a real app, you'd navigate to the main application
                showAlert("Success", "Welcome back, " + user.getUsername() + "!");
            } else {
                messageLabel.setText("Invalid username or password");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
            
            usernameField.clear();
            passwordField.clear();
        });
        
        loginBox.getChildren().addAll(
            new Label("Login to your account"),
            usernameField, passwordField, loginButton, messageLabel
        );
        
        return loginBox;
    }
    
    private VBox createSignupTab() {
        VBox signupBox = new VBox(15);
        signupBox.setPadding(new Insets(20));
        signupBox.setAlignment(Pos.CENTER);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        dobPicker.setMaxWidth(300);
        
        ComboBox<String> sexComboBox = new ComboBox<>();
        sexComboBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        sexComboBox.setPromptText("Select Sex");
        sexComboBox.setMaxWidth(300);
        
        Button signupButton = new Button("Sign Up");
        signupButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        signupButton.setPrefWidth(300);
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        
        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            LocalDate dob = dobPicker.getValue();
            String sex = sexComboBox.getValue();
            
            if (username.isEmpty() || password.isEmpty() || dob == null || sex == null) {
                messageLabel.setText("Please fill in all fields");
                return;
            }
            
            // Check if username already exists
            if (usernameExists(username)) {
                messageLabel.setText("Username already exists");
                return;
            }
            
            // Create new user
            User newUser = new User(username, password, dob, sex);
            users.add(newUser);
            currentUser = newUser;
            
            // Move to profile creation
            primaryStage.setScene(profileScene);
        });
        
        signupBox.getChildren().addAll(
            new Label("Create a new account"),
            usernameField, passwordField, dobPicker, sexComboBox, signupButton, messageLabel
        );
        
        return signupBox;
    }
    
    private void createProfileScene() {
        ScrollPane scrollPane = new ScrollPane();
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        Label titleLabel = new Label("Complete Your Profile");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label instructionLabel = new Label("Please answer the following questions by selecting the option that best describes you:");
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(600);
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        VBox questionsBox = new VBox(25);
        questionsBox.setAlignment(Pos.CENTER);
        questionsBox.setMaxWidth(700);
        
        List<ToggleGroup> answerGroups = new ArrayList<>();
        
        for (String question : controller.getQuestions()) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
            
            Label questionLabel = new Label(question);
            questionLabel.setWrapText(true);
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            HBox optionsBox = new HBox(15);
            optionsBox.setAlignment(Pos.CENTER);
            
            ToggleGroup group = new ToggleGroup();
            answerGroups.add(group);
            
            String[] options = {"Very untrue for me", "Somewhat untrue for me", "Neutral", "Somewhat true of me", "Very tru of me"};
            
            for (String option : options) {
                RadioButton rb = new RadioButton(option);
                rb.setToggleGroup(group);
                rb.setStyle("-fx-font-size: 12px;");
                optionsBox.getChildren().add(rb);
            }
            
            questionBox.getChildren().addAll(questionLabel, optionsBox);
            questionsBox.getChildren().add(questionBox);
        }
        
        Button completeButton = new Button("Complete Profile");
        completeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 30;");
        
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        
        completeButton.setOnAction(e -> {
            // Check if all questions are answered
            boolean allAnswered = true;
            for (ToggleGroup group : answerGroups) {
                if (group.getSelectedToggle() == null) {
                    allAnswered = false;
                    break;
                }
            }
            
            if (!allAnswered) {
                showAlert("Incomplete", "Please answer all questions to complete your profile.");
                return;
            }
            
            // Save profile answers (in real app, save to database)
            List<String> answers = new ArrayList<>();
            for (ToggleGroup group : answerGroups) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                answers.add(selected.getText());
            }
            
            if (currentUser != null) {
                currentUser.setProfileAnswers(answers);
            }
            
            resultLabel.setText("Profile completed successfully! Welcome, " + 
                (currentUser != null ? currentUser.getUsername() : "User") + "!");
            
            // Option to go back to login or continue to main app
            Button backButton = new Button("Back to Login");
            backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px;");
            backButton.setOnAction(event -> primaryStage.setScene(loginScene));
            
            if (!root.getChildren().contains(backButton)) {
                root.getChildren().add(backButton);
            }
        });
        
        root.getChildren().addAll(titleLabel, instructionLabel, questionsBox, completeButton, resultLabel);
        
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f5;");
        
        profileScene = new Scene(scrollPane, 800, 700);
    }
    
    private User findUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    private boolean usernameExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// User class to store user data
class User {
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private String sex;
    private List<String> profileAnswers;
    
    public User(String username, String password, LocalDate dateOfBirth, String sex) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.profileAnswers = new ArrayList<>();
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getSex() { return sex; }
    public List<String> getProfileAnswers() { return profileAnswers; }
    public void setProfileAnswers(List<String> answers) { this.profileAnswers = answers; }
}
