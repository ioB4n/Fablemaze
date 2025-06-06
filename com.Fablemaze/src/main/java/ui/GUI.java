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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {
    
    private Stage primaryStage;
    private Scene loginScene;
    private Scene profileScene;
    
    private AppController controller;
    
    @Override
    public void start(Stage stage) {
        DatabaseManager.initSchema();
        controller = new AppController();
        
        primaryStage = stage;
        primaryStage.setTitle("Account Portal");
        
        // Create login/signup scene
        createLoginScene();
        
        // Create profile creation scene
        createProfileScene();
        
        // Start with login scene
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void createLoginScene() {
        // Main container with gradient background
        StackPane root = new StackPane();
        root.setStyle("-fx-background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        
        // Main content card
        VBox mainCard = new VBox();
        mainCard.setMaxWidth(450);
        mainCard.setMaxHeight(600);
        mainCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 40;" +
            "-fx-spacing: 25;"
        );
        
        // Add subtle shadow
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        shadow.setColor(Color.color(0, 0, 0, 0.3));
        mainCard.setEffect(shadow);
        
        // Title
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: 300;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-family: 'Segoe UI Light';"
        );
        
        Label subtitleLabel = new Label("Sign in to your account or create a new one");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-family: 'Segoe UI';"
        );
        
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Create tab pane for login/signup
        TabPane tabPane = new TabPane();
        tabPane.setStyle(
            "-fx-tab-min-width: 200px;" +
            "-fx-tab-max-width: 200px;"
        );
        
        // Login Tab
        Tab loginTab = new Tab("Sign In");
        loginTab.setClosable(false);
        loginTab.setStyle("-fx-font-family: 'Segoe UI';");
        VBox loginContent = createLoginTab();
        loginTab.setContent(loginContent);
        
        // Signup Tab
        Tab signupTab = new Tab("Create Account");
        signupTab.setClosable(false);
        signupTab.setStyle("-fx-font-family: 'Segoe UI';");
        VBox signupContent = createSignupTab();
        signupTab.setContent(signupContent);
        
        tabPane.getTabs().addAll(loginTab, signupTab);
        
        mainCard.getChildren().addAll(titleBox, tabPane);
        root.getChildren().add(mainCard);
        
        loginScene = new Scene(root, 600, 700);
    }
    
    private VBox createLoginTab() {
        VBox loginBox = new VBox(20);
        loginBox.setPadding(new Insets(30, 0, 0, 0));
        loginBox.setAlignment(Pos.CENTER);
        
        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle(getModernTextFieldStyle());
        usernameField.setPrefHeight(45);
        
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(getModernTextFieldStyle());
        passwordField.setPrefHeight(45);
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setStyle(getPrimaryButtonStyle());
        loginButton.setPrefHeight(50);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        
        // Hover effects
        addButtonHoverEffect(loginButton);
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
                return;
            }
            
            // Here you would call your AppController to handle login
            // For demo purposes, just show success
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px;");
            
            usernameField.clear();
            passwordField.clear();
        });
        
        loginBox.getChildren().addAll(usernameBox, passwordBox, loginButton, messageLabel);
        return loginBox;
    }
    
    private VBox createSignupTab() {
        VBox signupBox = new VBox(20);
        signupBox.setPadding(new Insets(20, 0, 0, 0));
        signupBox.setAlignment(Pos.CENTER);
        
        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setStyle(getModernTextFieldStyle());
        usernameField.setPrefHeight(45);
        
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setStyle(getModernTextFieldStyle());
        passwordField.setPrefHeight(45);
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Date of birth field
        VBox dobBox = new VBox(8);
        Label dobLabel = new Label("Date of Birth");
        dobLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select your birth date");
        dobPicker.setStyle(getModernTextFieldStyle());
        dobPicker.setPrefHeight(45);
        
        dobBox.getChildren().addAll(dobLabel, dobPicker);
        
        // Sex field
        VBox sexBox = new VBox(8);
        Label sexLabel = new Label("Gender");
        sexLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 500;");
        
        ComboBox<String> sexComboBox = new ComboBox<>();
        sexComboBox.getItems().addAll("Male", "Female", "Non-binary", "Prefer not to say");
        sexComboBox.setPromptText("Select gender");
        sexComboBox.setStyle(getModernTextFieldStyle());
        sexComboBox.setPrefHeight(45);
        sexComboBox.setMaxWidth(Double.MAX_VALUE);
        
        sexBox.getChildren().addAll(sexLabel, sexComboBox);
        
        // Signup button
        Button signupButton = new Button("Create Account");
        signupButton.setStyle(getSecondaryButtonStyle());
        signupButton.setPrefHeight(50);
        signupButton.setMaxWidth(Double.MAX_VALUE);
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        
        addButtonHoverEffect(signupButton);
        
        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            LocalDate dob = dobPicker.getValue();
            String sex = sexComboBox.getValue();
            
            if (username.isEmpty() || password.isEmpty() || dob == null || sex == null) {
                messageLabel.setText("Please fill in all fields");
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
                return;
            }
            
            // Here you would call your AppController to handle signup
            // For demo purposes, move to profile creation
            primaryStage.setScene(profileScene);
        });
        
        signupBox.getChildren().addAll(usernameBox, passwordBox, dobBox, sexBox, signupButton, messageLabel);
        return signupBox;
    }
    
    private void createProfileScene() {
        // Main container with gradient background
        StackPane root = new StackPane();
        root.setStyle("-fx-background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        // Header card
        VBox headerCard = new VBox(15);
        headerCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 40;" +
            "-fx-alignment: center;"
        );
        headerCard.setMaxWidth(800);
        headerCard.setEffect(createShadowEffect());
        
        Label titleLabel = new Label("Complete Your Profile");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: 300;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-family: 'Segoe UI Light';"
        );
        
        Label instructionLabel = new Label("Help us get to know you better by answering these questions");
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(600);
        instructionLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-text-alignment: center;"
        );
        
        headerCard.getChildren().addAll(titleLabel, instructionLabel);
        
        // Questions container
        VBox questionsContainer = new VBox(25);
        questionsContainer.setAlignment(Pos.CENTER);
        questionsContainer.setMaxWidth(800);
        
        // Profile questions
        String[] questions = {
            "I enjoy meeting new people and socializing",
            "I prefer to plan things in advance rather than be spontaneous",
            "I am comfortable being the center of attention",
            "I enjoy creative activities like art, music, or writing",
            "I like to help others with their problems",
            "I prefer working alone rather than in a team",
            "I enjoy taking risks and trying new experiences",
            "I am very organized and like to keep things tidy",
            "I prefer facts and logic over emotions when making decisions",
            "I enjoy competitive activities and sports"
        };
        
        List<ToggleGroup> answerGroups = new ArrayList<>();
        
        for (int i = 0; i < questions.length; i++) {
            VBox questionCard = createQuestionCard(questions[i], i + 1);
            ToggleGroup group = createQuestionOptions(questionCard);
            answerGroups.add(group);
            questionsContainer.getChildren().add(questionCard);
        }
        
        // Complete button card
        VBox buttonCard = new VBox(20);
        buttonCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 40;" +
            "-fx-alignment: center;"
        );
        buttonCard.setMaxWidth(800);
        buttonCard.setEffect(createShadowEffect());
        
        Button completeButton = new Button("Complete Profile");
        completeButton.setStyle(
            "-fx-background-color: linear-gradient(45deg, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 15 40;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px;");
        
        addButtonHoverEffect(completeButton);
        
        completeButton.setOnAction(e -> {
            // Check if all questions are answered
            boolean allAnswered = answerGroups.stream()
                .allMatch(group -> group.getSelectedToggle() != null);
            
            if (!allAnswered) {
                showModernAlert("Incomplete Profile", "Please answer all questions to complete your profile.");
                return;
            }
            
            // Collect answers for AppController
            List<String> answers = new ArrayList<>();
            for (ToggleGroup group : answerGroups) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                answers.add(selected.getText());
            }
            
            // Here you would pass answers to your AppController
            resultLabel.setText("✓ Profile completed successfully!");
            resultLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: 600;");
            
            // Add back button
            Button backButton = new Button("Back to Login");
            backButton.setStyle(getSecondaryButtonStyle());
            backButton.setOnAction(event -> primaryStage.setScene(loginScene));
            addButtonHoverEffect(backButton);
            
            if (!buttonCard.getChildren().contains(backButton)) {
                buttonCard.getChildren().add(backButton);
            }
        });
        
        buttonCard.getChildren().addAll(completeButton, resultLabel);
        
        contentBox.getChildren().addAll(headerCard, questionsContainer, buttonCard);
        scrollPane.setContent(contentBox);
        root.getChildren().add(scrollPane);
        
        profileScene = new Scene(root, 900, 700);
    }
    
    private VBox createQuestionCard(String question, int number) {
        VBox questionCard = new VBox(20);
        questionCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 30;"
        );
        questionCard.setEffect(createShadowEffect());
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label numberLabel = new Label(String.valueOf(number));
        numberLabel.setStyle(
            "-fx-background-color: linear-gradient(45deg, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 20;" +
            "-fx-min-width: 35;" +
            "-fx-alignment: center;"
        );
        
        Label questionLabel = new Label(question);
        questionLabel.setWrapText(true);
        questionLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: 500;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        headerBox.getChildren().addAll(numberLabel, questionLabel);
        questionCard.getChildren().add(headerBox);
        
        return questionCard;
    }
    
    private ToggleGroup createQuestionOptions(VBox questionCard) {
        ToggleGroup group = new ToggleGroup();
        
        HBox optionsBox = new HBox(15);
        optionsBox.setAlignment(Pos.CENTER);
        
        String[] options = {"Not like me", "Somewhat like me", "Moderately like me", "Very like me", "Extremely like me"};
        String[] colors = {"#e74c3c", "#f39c12", "#f1c40f", "#2ecc71", "#27ae60"};
        
        for (int i = 0; i < options.length; i++) {
            VBox optionBox = new VBox(8);
            optionBox.setAlignment(Pos.CENTER);
            
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill: transparent;");
            
            Label optionLabel = new Label(options[i]);
            optionLabel.setWrapText(true);
            optionLabel.setMaxWidth(120);
            optionLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #7f8c8d;" +
                "-fx-text-alignment: center;" +
                "-fx-font-weight: 500;"
            );
            
            final String color = colors[i];
            final Label label = optionLabel;
            
            // Custom styling for selected state
            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    label.setStyle(
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-text-alignment: center;" +
                        "-fx-font-weight: bold;"
                    );
                } else {
                    label.setStyle(
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #7f8c8d;" +
                        "-fx-text-alignment: center;" +
                        "-fx-font-weight: 500;"
                    );
                }
            });
            
            optionBox.getChildren().addAll(rb, optionLabel);
            optionsBox.getChildren().add(optionBox);
        }
        
        questionCard.getChildren().add(optionsBox);
        return group;
    }
    
    private DropShadow createShadowEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        return shadow;
    }
    
    private String getModernTextFieldStyle() {
        return "-fx-background-color: #f8f9fa;" +
               "-fx-border-color: #e9ecef;" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 12;" +
               "-fx-font-size: 14px;" +
               "-fx-text-fill: #2c3e50;" +
               "-fx-prompt-text-fill: #95a5a6;";
    }
    
    private String getPrimaryButtonStyle() {
        return "-fx-background-color: linear-gradient(45deg, #667eea, #764ba2);" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 16px;" +
               "-fx-font-weight: 600;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;" +
               "-fx-border-color: transparent;";
    }
    
    private String getSecondaryButtonStyle() {
        return "-fx-background-color: linear-gradient(45deg, #667eea, #764ba2);" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 16px;" +
               "-fx-font-weight: 600;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;" +
               "-fx-border-color: transparent;";
    }
    
    private void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-scale-x: 1.02; -fx-scale-y: 1.02;", "")));
    }
    
    private void showModernAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-font-family: 'Segoe UI';"
        );
        
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}