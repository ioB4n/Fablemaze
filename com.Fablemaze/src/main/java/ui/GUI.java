/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ui;

import javafx.concurrent.Task;
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
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import model.Movie;
import model.SceneVariant;

public class GUI extends Application {
    
    private Stage primaryStage;
    private StackPane mainContainer;
    
    // Login/Signup page components
    private VBox loginForm;
    private VBox signupForm;
    private Button loginToggle;
    private Button signupToggle;
    private boolean isLoginMode = true;
    
    // Pages
    private VBox authPage;
    private VBox homePage;
    private VBox profileSpecPage;
    private VBox movieWatchPage;
    
    private AppController controller;
    private String currentUsername;
    
    @Override
public void start(Stage stage) {
    primaryStage = stage;
    primaryStage.setTitle("Fablemaze");

    // Show a temporary loading scene
    Label loadingLabel = new Label("Loading application, please wait...");
    loadingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    StackPane loadingPane = new StackPane(loadingLabel);
    loadingPane.setStyle("-fx-background-color: linear-gradient(#141e30, #243b55);");
    Scene loadingScene = new Scene(loadingPane, 1050, 825);
    primaryStage.setScene(loadingScene);
    primaryStage.show();

    // Initialize DB in background thread
    Task<Void> initTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
            DatabaseManager.initSchema();
            return null;
        }
    };

    initTask.setOnSucceeded(e -> {
        // Now safe to continue with GUI init
        controller = new AppController();
        
        mainContainer = new StackPane();
        mainContainer.setStyle(
            "-fx-background-color: linear-gradient(#667eea 0%, #764ba2 100%);"
        );

        createAuthPage();
        createHomePage();
        createProfileSpecPage();
        createMovieWatchPage(new Movie());

        mainContainer.getChildren().addAll(authPage, homePage, profileSpecPage, movieWatchPage);
        showAuthPage();

        Scene mainScene = new Scene(mainContainer, 1050, 825);
        primaryStage.setScene(mainScene);
    });

    initTask.setOnFailed(e -> {
        Throwable ex = initTask.getException();
        loadingLabel.setText("Failed to initialize database:\n" + ex.getMessage());
        ex.printStackTrace();
    });

    new Thread(initTask).start();
}

    
    private void createAuthPage() {
        // Left side - Decorative panel
        VBox leftPanel = createLeftPanel();
        
        // Right side - Form panel
        VBox rightPanel = createRightPanel();
        
        // Main card container
        HBox mainCard = new HBox();
        mainCard.setMaxWidth(900);
        mainCard.setMaxHeight(600);
        mainCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);"
        );
        
        mainCard.getChildren().addAll(leftPanel, rightPanel);
        
        authPage = new VBox();
        authPage.setAlignment(Pos.CENTER);
        authPage.getChildren().add(mainCard);
    }
    
    private void createHomePage() {
        homePage = new VBox(30);
        homePage.setAlignment(Pos.CENTER);
        homePage.setPadding(new Insets(40));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label welcomeLabel = new Label("Welcome to Fablemaze");
        welcomeLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 300;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 20;"
        );

        logoutButton.setOnAction(e -> {
            currentUsername = null;
            showAuthPage();
        });

        header.getChildren().addAll(welcomeLabel, spacer, logoutButton);

        // Main content card
        VBox contentCard = new VBox(30);
        contentCard.setMaxWidth(1000);
        contentCard.setPadding(new Insets(40));
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );

        Label titleLabel = new Label("Available Movies");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-weight: 600;"
        );

        // Get movies from controller
        List<Movie> movies = controller.getMovies();

        // Create scrollable movie grid
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: white;" +
            "-fx-background-color: white;" +
            "-fx-border-color: transparent;"
        );

        GridPane moviesGrid = new GridPane();
        moviesGrid.setHgap(20);
        moviesGrid.setVgap(20);
        moviesGrid.setAlignment(Pos.CENTER);
        moviesGrid.setPadding(new Insets(20));

        // Display movies in a grid (3 columns)
        int col = 0;
        int row = 0;

        for (Movie movie : movies) {
            VBox movieCard = createMovieCard(movie);
            moviesGrid.add(movieCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        scrollPane.setContent(moviesGrid);
        scrollPane.setPrefHeight(400);

        contentCard.getChildren().addAll(titleLabel, scrollPane);
        homePage.getChildren().addAll(header, contentCard);
    }

    private VBox createMovieCard(Movie movie) {
        VBox movieCard = new VBox(12);
        movieCard.setAlignment(Pos.TOP_CENTER);
        movieCard.setPadding(new Insets(20));
        movieCard.setMaxWidth(280);
        movieCard.setPrefWidth(280);
        movieCard.setMinHeight(220);
        movieCard.setPrefHeight(220);
        movieCard.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8f9fa);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #e1e5e9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0.3, 0, 3);"
        );

        // Add subtle hover effect to the entire card
        movieCard.setOnMouseEntered(e -> {
            movieCard.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8f9fa);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #c6cbd1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0.4, 0, 5);" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });

        movieCard.setOnMouseExited(e -> {
            movieCard.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8f9fa);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #e1e5e9;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0.3, 0, 3);" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });

        // Movie title with better text handling
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(240);
        titleLabel.setMaxHeight(50);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-text-fill: #1a202c;" +
            "-fx-font-weight: 700;" +
            "-fx-text-alignment: center;" +
            "-fx-padding: 0 0 8 0;"
        );

        // Content area for movie details
        VBox contentArea = new VBox(8);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setFillWidth(true);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Movie details with improved styling
        VBox detailsBox = new VBox(6);
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setMaxWidth(240);

        if (movie.getReleaseYear() != null) {
            HBox yearBox = new HBox(8);
            yearBox.setAlignment(Pos.CENTER);

            Label yearIcon = new Label("📅");
            yearIcon.setStyle("-fx-font-size: 14px;");

            Label yearLabel = new Label(movie.getReleaseYear().toString());
            yearLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #4a5568;" +
                "-fx-font-weight: 500;"
            );

            yearBox.getChildren().addAll(yearIcon, yearLabel);
            detailsBox.getChildren().add(yearBox);
        }

        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            HBox genreBox = new HBox(8);
            genreBox.setAlignment(Pos.CENTER);

            Label genreIcon = new Label("🎭");
            genreIcon.setStyle("-fx-font-size: 14px;");

            Label genreLabel = new Label(truncateText(movie.getGenres(), 20));
            genreLabel.setWrapText(true);
            genreLabel.setMaxWidth(200);
            genreLabel.setTextAlignment(TextAlignment.CENTER);
            genreLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #4a5568;" +
                "-fx-font-weight: 500;" +
                "-fx-text-alignment: center;"
            );

            genreBox.getChildren().addAll(genreIcon, genreLabel);
            detailsBox.getChildren().add(genreBox);
        }

        if (movie.getDuration() != null) {
            HBox durationBox = new HBox(8);
            durationBox.setAlignment(Pos.CENTER);

            Label durationLabel = new Label("⏱ " + movie.getDuration().toString() + " min");
            durationLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #4a5568;" +
                "-fx-font-weight: 500;"
            );

            durationBox.getChildren().add(durationLabel);
            detailsBox.getChildren().add(durationBox);
        }

        contentArea.getChildren().add(detailsBox);

        // Spacer to push button to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        contentArea.getChildren().add(spacer);

        // Enhanced watch button
        Button watchButton = new Button("▶ Watch Now");
        watchButton.setMaxWidth(Double.MAX_VALUE);
        watchButton.setStyle(
            "-fx-background-color: linear-gradient(#667eea 0%, #764ba2 100%);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0.2, 0, 2);"
        );

        // Enhanced button hover effects
        watchButton.setOnMouseEntered(e -> {
            watchButton.setStyle(
                "-fx-background-color: linear-gradient(#5a6fd8 0%, #6a4190 100%);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 12 24;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 12, 0.3, 0, 4);" +
                "-fx-scale-x: 1.03;" +
                "-fx-scale-y: 1.03;"
            );
        });

        watchButton.setOnMouseExited(e -> {
            watchButton.setStyle(
                "-fx-background-color: linear-gradient(#667eea 0%, #764ba2 100%);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 12 24;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0.2, 0, 2);" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });

        // Watch button action
        watchButton.setOnAction(e -> {
            createMovieWatchPage(movie);
            showMovieWatchPage();
        });

        movieCard.getChildren().addAll(titleLabel, contentArea, watchButton);
        return movieCard;
    }

    // Helper method to truncate long text
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private void createMovieWatchPage(Movie movie) {
        if (movieWatchPage != null) {
            mainContainer.getChildren().remove(movieWatchPage);
        }
        
        movieWatchPage = new VBox(30);
        movieWatchPage.setAlignment(Pos.CENTER);
        movieWatchPage.setPadding(new Insets(30));

        // Header with back button and movie title
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 30, 0));

        Button backButton = new Button("← Back to Movies");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 20;"
        );

        backButton.setOnAction(e -> showHomePage());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label movieTitleLabel = new Label(movie.getTitle());
        movieTitleLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 400;"
        );

        header.getChildren().addAll(backButton, spacer, movieTitleLabel);

        // Main content card
        VBox contentCard = new VBox(25);
        contentCard.setMaxWidth(1200);
        contentCard.setPadding(new Insets(40));
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
        );
        
        List<SceneVariant> segments = controller.getSceneVariantSequence(movie);

        // Container for the 5 segment images
        HBox segmentsContainer = new HBox(20);
        segmentsContainer.setAlignment(Pos.CENTER);
        segmentsContainer.setPadding(new Insets(20, 0, 20, 0));

        // Create 5 segment cards
        for (SceneVariant variant : segments) {
            VBox segmentCard = createSegmentCard(variant);
            segmentsContainer.getChildren().add(segmentCard);
        }

        // Optional: Add scroll pane if segments might overflow
        ScrollPane segmentsScrollPane = new ScrollPane(segmentsContainer);
        segmentsScrollPane.setFitToHeight(true);
        segmentsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        segmentsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        segmentsScrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        
        contentCard.getChildren().addAll(segmentsScrollPane);
        movieWatchPage.getChildren().addAll(header, contentCard);
    }
    
    private void showMovieWatchPage() {
        authPage.setVisible(false);
        homePage.setVisible(false);
        profileSpecPage.setVisible(false);
        movieWatchPage.setVisible(true);
        mainContainer.getChildren().add(movieWatchPage);
        primaryStage.setTitle("Fablemaze - Watch Movie");
    }
    
    private VBox createSegmentCard(SceneVariant variant) {
        VBox segmentCard = new VBox(12);
        segmentCard.setAlignment(Pos.CENTER);
        segmentCard.setPadding(new Insets(15));
        segmentCard.setMaxWidth(200);
        segmentCard.setPrefWidth(200);
        segmentCard.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #e9ecef;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
            "-fx-cursor: hand;"
        );

        // Add hover effect to segment card
        segmentCard.setOnMouseEntered(e -> {
            segmentCard.setStyle(
                "-fx-background-color: #e8f4fd;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #667eea;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 15, 0.3, 0, 4);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.03;" +
                "-fx-scale-y: 1.03;"
            );
        });

        segmentCard.setOnMouseExited(e -> {
            segmentCard.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #e9ecef;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });

        ImageView segmentImage = new ImageView();
        segmentImage.setFitWidth(160);
        segmentImage.setFitHeight(90);
        segmentImage.setPreserveRatio(true);
        segmentImage.setSmooth(true);

        // Load image from file or URL
        try {
            Image image = new Image(getClass().getResource("/images/" + variant.getFilePath()).toExternalForm());
            segmentImage.setImage(image);
        } catch (Exception ex) {
            // Fallback to placeholder if image not found
            segmentImage.setImage(null);
        }

        // Overlay play icon on image
        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(segmentImage);

        Label playIcon = new Label("▶");
        playIcon.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);" +
            "-fx-background-color: rgba(0,0,0,0.5);" +
            "-fx-background-radius: 50%;" +
            "-fx-padding: 8;"
        );
        imageContainer.getChildren().add(playIcon);

        // Segment info
        Label segmentLabel = new Label(variant.getVariantName() + "  ⏱" + variant.getDuration() + " min");
        segmentLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-weight: 600;"
        );

        segmentCard.getChildren().addAll(imageContainer, segmentLabel);
        return segmentCard;
    }
    
    private void createProfileSpecPage() {
        profileSpecPage = new VBox(30);
        profileSpecPage.setAlignment(Pos.CENTER);
        profileSpecPage.setPadding(new Insets(40));
        
        // Header
        Label headerLabel = new Label("Complete Your Profile");
        headerLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 300;"
        );
        
        // Main content card
        VBox contentCard = new VBox(25);
        contentCard.setMaxWidth(700);
        contentCard.setMaxHeight(650);
        contentCard.setPadding(new Insets(40));
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );
        
        Label titleLabel = new Label("Help us personalize your experience");
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-weight: 600;"
        );
        
        Label descLabel = new Label("Answer a few questions to get a more personalized experience.");
        descLabel.setWrapText(true);
        descLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-text-alignment: center;"
        );
        
        // ScrollPane for questions
        ScrollPane scrollPane = new ScrollPane();
        VBox questionsContainer = new VBox(40);
        questionsContainer.setPadding(new Insets(20));

        Map<String, Integer> answers = new HashMap<>();
        List<ToggleGroup> toggleGroups = new ArrayList<>();
        
        int questionIndex = 1;
        for (String question : controller.getQuestions()) {
            VBox questionBox = new VBox(15);
            
            Label questionLabel = new Label(questionIndex++ + ". " + question);
            questionLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #2c3e50;" +
                "-fx-font-weight: 600;"
            );
            
            HBox optionBox = new HBox(8);
            ToggleGroup group = new ToggleGroup();
            
            for (Map.Entry<Integer, String> entry : controller.getOptions().entrySet()) {
                String optionText = entry.getValue();
                int optionValue = entry.getKey();
                
                RadioButton radioButton = new RadioButton(optionText);
                radioButton.setToggleGroup(group);
                radioButton.setStyle(
                    "-fx-text-fill: #2c3e50;" +
                    "-fx-font-size: 14px;"
                );
                
                radioButton.setOnAction(e -> {
                    answers.put(question, optionValue);
                });
                
                optionBox.getChildren().add(radioButton);
            }
            
            toggleGroups.add(group);
            questionBox.getChildren().addAll(questionLabel, optionBox);
            questionsContainer.getChildren().add(questionBox);
        }
        
        scrollPane.setContent(questionsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button skipButton = new Button("Skip for now");
        skipButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 12 25;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e9ecef;" +
            "-fx-border-radius: 20;"
        );
        
        Button completeButton = new Button("Complete Profile");
        completeButton.setStyle(
            "-fx-background-color: linear-gradient(#667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 12 30;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        
        addButtonHoverEffect(skipButton);
        addButtonHoverEffect(completeButton);
        
        skipButton.setOnAction(e -> showHomePage());
        
        completeButton.setOnAction(e -> {
            boolean allAnswered = toggleGroups.stream().allMatch(
                group -> group.getSelectedToggle() != null);
            
            if (!allAnswered) {
                showMessage(messageLabel, "Questions remain unanswered!", false);
                return;
            }
            
            controller.setAnswers(answers);
            controller.completeProfile();
            showHomePage();
            showMessage(messageLabel, "Profile Completed.", true);
        });
        
        buttonBox.getChildren().addAll(skipButton, completeButton);
        
        contentCard.getChildren().addAll(titleLabel, descLabel, scrollPane, messageLabel, buttonBox);
        profileSpecPage.getChildren().addAll(headerLabel, contentCard);
    }
    
    private void showAuthPage() {
        authPage.setVisible(true);
        homePage.setVisible(false);
        profileSpecPage.setVisible(false);
        movieWatchPage.setVisible(false);
        primaryStage.setTitle("Account Portal");
    }
    
    private void showHomePage() {
        authPage.setVisible(false);
        homePage.setVisible(true);
        profileSpecPage.setVisible(false);
        movieWatchPage.setVisible(false);
        primaryStage.setTitle("Fablemaze - Home");
    }
    
    private void showProfileSpecPage() {
        authPage.setVisible(false);
        homePage.setVisible(false);
        profileSpecPage.setVisible(true);
        movieWatchPage.setVisible(false);
        primaryStage.setTitle("Fablemaze - Complete Profile");
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(30);
        leftPanel.setPrefWidth(400);
        leftPanel.setPadding(new Insets(60, 40, 60, 40));
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setStyle(
            "-fx-background-color: linear-gradient(#667eea, #764ba2);" +
            "-fx-background-radius: 20 0 0 20;"
        );
        
        // Logo/Brand area
        Label brandLabel = new Label("Fablemaze");
        brandLabel.setStyle(
            "-fx-font-size: 60px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: 300;"
        );
        
        Label titleLabel = new Label("Welcome to Fablemaze");
        titleLabel.setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-weight: 300;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI Light';"
        );
        
        Label descriptionLabel = new Label("Your gateway to a personalized and adaptable film-watching experience.");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);
        descriptionLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: rgba(255,255,255,0.9);" +
            "-fx-text-alignment: center;" +
            "-fx-line-spacing: 5px;"
        );
        
        // Feature highlights
        VBox featuresBox = new VBox(15);
        featuresBox.setAlignment(Pos.CENTER_LEFT);
        featuresBox.setMaxWidth(300);
        
        String[] features = {
            "🔒 Secure & Private",
            "🎯 Personalized Experience"
        };
        
        for (String feature : features) {
            Label featureLabel = new Label(feature);
            featureLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: rgba(255,255,255,0.9);" +
                "-fx-font-weight: 500;"
            );
            featuresBox.getChildren().add(featureLabel);
        }
        
        leftPanel.getChildren().addAll(brandLabel, titleLabel, descriptionLabel, featuresBox);
        return leftPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(30);
        rightPanel.setPrefWidth(500);
        rightPanel.setPadding(new Insets(60, 60, 60, 60));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        
        // Header with toggle buttons
        VBox headerBox = createHeader();
        
        // Forms container
        StackPane formsContainer = new StackPane();
        formsContainer.setAlignment(Pos.TOP_CENTER);
        
        // Create login and signup forms
        loginForm = createLoginForm();
        signupForm = createSignupForm();
        
        // Initially show login form
        formsContainer.getChildren().addAll(signupForm, loginForm);
        signupForm.setVisible(false);
        
        rightPanel.getChildren().addAll(headerBox, formsContainer);
        return rightPanel;
    }
    
    private VBox createHeader() {
        VBox headerBox = new VBox(20);
        headerBox.setAlignment(Pos.CENTER);
        
        // Toggle buttons
        HBox toggleBox = new HBox(0);
        toggleBox.setAlignment(Pos.CENTER);
        toggleBox.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 30;" +
            "-fx-padding: 5;"
        );
        
        loginToggle = new Button("Sign In");
        signupToggle = new Button("Sign Up");
        
        String activeStyle = 
            "-fx-background-color: white;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 25;" +
            "-fx-padding: 12 30;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
        
        String inactiveStyle = 
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-background-radius: 25;" +
            "-fx-padding: 12 30;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;";
        
        loginToggle.setStyle(activeStyle);
        signupToggle.setStyle(inactiveStyle);
        
        loginToggle.setOnAction(e -> switchToLogin());
        signupToggle.setOnAction(e -> switchToSignup());
        
        toggleBox.getChildren().addAll(loginToggle, signupToggle);
        
        Label subtitleLabel = new Label("Enter your details to continue");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #7f8c8d;"
        );
        
        headerBox.getChildren().addAll(toggleBox, subtitleLabel);
        return headerBox;
    }
    
    private VBox createLoginForm() {
        VBox form = new VBox(25);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        
        // Username field
        VBox usernameBox = createInputField("Username", "Enter your username", false);
        TextField usernameField = (TextField) ((VBox) usernameBox.getChildren().get(1)).getChildren().get(0);
        
        // Password field
        VBox passwordBox = createInputField("Password", "Enter your password", true);
        PasswordField passwordField = (PasswordField) ((VBox) passwordBox.getChildren().get(1)).getChildren().get(0);
        
        // Remember me checkbox
        CheckBox rememberBox = new CheckBox("Remember me");
        rememberBox.setStyle(
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-size: 13px;"
        );
        
        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setStyle(
            "-fx-background-color: linear-gradient(#667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15 0;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        
        // Forgot password link
        Label forgotLabel = new Label("Forgot your password?");
        forgotLabel.setStyle(
            "-fx-text-fill: #667eea;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-underline: true;"
        );
        
        addButtonHoverEffect(loginButton);
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showMessage(messageLabel, "Please fill in all fields", false);
                return;
            }
            
            String result = controller.login(username, password);
            
            if (result.endsWith("Login successful!")) {
                showMessage(messageLabel, result, true);
                currentUsername = username;
                usernameField.clear();
                passwordField.clear();
                showHomePage();
                
            } else {
                showMessage(messageLabel, result, false);
            }
        });
        
        form.getChildren().addAll(
            usernameBox, passwordBox, rememberBox, loginButton, messageLabel, forgotLabel
        );
        
        return form;
    }
    
    private VBox createSignupForm() {
        ScrollPane scrollPane = new ScrollPane();
        VBox form = new VBox(20);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        form.setPadding(new Insets(0, 10, 0, 15)); // Add right padding for scrollbar
        
        // Username field
        VBox usernameBox = createInputField("Username", "Choose a username", false);
        TextField usernameField = (TextField) ((VBox) usernameBox.getChildren().get(1)).getChildren().get(0);
        
        // Password field
        VBox passwordBox = createInputField("Password", "Create a password", true);
        PasswordField passwordField = (PasswordField) ((VBox) passwordBox.getChildren().get(1)).getChildren().get(0);
        
        // Confirm password field
        VBox confirmPasswordBox = createInputField("Confirm Password", "Confirm your password", true);
        PasswordField confirmPasswordField = (PasswordField) ((VBox) confirmPasswordBox.getChildren().get(1)).getChildren().get(0);
        
        // Date of birth field
        VBox dobBox = new VBox(8);
        Label dobLabel = new Label("Date of Birth");
        dobLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 600;");
        
        VBox dobFieldBox = new VBox();
        dobFieldBox.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #e9ecef;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select your birth date");
        dobPicker.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #2c3e50;"
        );
        dobPicker.setMaxWidth(Double.MAX_VALUE);
        
        dobFieldBox.getChildren().add(dobPicker);
        dobBox.getChildren().addAll(dobLabel, dobFieldBox);
        
        // Gender field
        VBox genderBox = new VBox(8);
        Label genderLabel = new Label("Gender");
        genderLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 600;");
        
        VBox genderFieldBox = new VBox();
        genderFieldBox.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #e9ecef;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Non-binary", "Prefer not to say");
        genderComboBox.setPromptText("Select gender");
        genderComboBox.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #2c3e50;"
        );
        genderComboBox.setMaxWidth(Double.MAX_VALUE);
        
        genderFieldBox.getChildren().add(genderComboBox);
        genderBox.getChildren().addAll(genderLabel, genderFieldBox);
        
        // Terms checkbox
        CheckBox termsBox = new CheckBox("I agree to the Terms of Service and Privacy Policy");
        termsBox.setWrapText(true);
        termsBox.setStyle(
            "-fx-text-fill: #7f8c8d;" +
            "-fx-font-size: 12px;"
        );
        
        // Signup button
        Button signupButton = new Button("Create Account");
        signupButton.setStyle(
            "-fx-background-color: linear-gradient(#667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15 0;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;"
        );
        signupButton.setMaxWidth(Double.MAX_VALUE);
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        
        addButtonHoverEffect(signupButton);
        
        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            LocalDate dob = dobPicker.getValue();
            String gender = genderComboBox.getValue();
            
            if (username.isEmpty() || password.isEmpty() || 
                confirmPassword.isEmpty() || dob == null || gender == null) {
                showMessage(messageLabel, "Please fill in all fields", false);
                return;
            }
            
            if (username.length() < 5) {
                showMessage(messageLabel, "Username must be at least 5 characters long.", false);
                return;
            }
            
            if (password.length() < 8) {
                showMessage(messageLabel, "Password must be at least 8 characters long.", false);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                showMessage(messageLabel, "Passwords do not match", false);
                return;
            }
            
            if (!termsBox.isSelected()) {
                showMessage(messageLabel, "Please accept the terms and conditions", false);
                return;
            }
            
            String result = controller.signUp(username, password, dob, gender);
            
            if (result.equals("Sign-Up successful!")) {
                showMessage(messageLabel, result, true);
                currentUsername = username;
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                dobPicker.setValue(null);
                genderComboBox.setValue(null);
                termsBox.setSelected(false);
                showProfileSpecPage();
                
            } else {
                showMessage(messageLabel, result, false);
            }
        });
        
        form.getChildren().addAll(
            usernameBox, passwordBox, confirmPasswordBox, 
            dobBox, genderBox, termsBox, signupButton, messageLabel
        );
        
        // Wrap in ScrollPane
        scrollPane.setContent(form);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(400);
        
        VBox container = new VBox();
        container.getChildren().add(scrollPane);
        return container;
    }
    
    private VBox createInputField(String labelText, String placeholder, boolean isPassword) {
        VBox fieldBox = new VBox(8);
        
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-font-weight: 600;");
        
        VBox inputContainer = new VBox();
        inputContainer.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #e9ecef;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        TextField inputField;
        if (isPassword) {
            inputField = new PasswordField();
        } else {
            inputField = new TextField();
        }
        
        inputField.setPromptText(placeholder);
        inputField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-prompt-text-fill: #95a5a6;"
        );
        
        // Focus effects
        inputField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                inputContainer.setStyle(
                    "-fx-background-color: #f8f9fa;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;"
                );
            } else {
                inputContainer.setStyle(
                    "-fx-background-color: #f8f9fa;" +
                    "-fx-border-color: #e9ecef;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;"
                );
            }
        });
        
        inputContainer.getChildren().add(inputField);
        fieldBox.getChildren().addAll(label, inputContainer);
        
        return fieldBox;
    }
    
    private void switchToLogin() {
        if (!isLoginMode) {
            isLoginMode = true;
            
            // Update toggle button styles
            loginToggle.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #2c3e50;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 12 30;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );
            
            signupToggle.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #7f8c8d;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 12 30;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
            
            // Show login form
            loginForm.setVisible(true);
            signupForm.setVisible(false);
        }
    }
    
    private void switchToSignup() {
        if (isLoginMode) {
            isLoginMode = false;
            
            // Update toggle button styles
            signupToggle.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #2c3e50;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 12 30;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );
            
            loginToggle.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #7f8c8d;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 12 30;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"
            );
            
            // Show signup form
            signupForm.setVisible(true);
            loginForm.setVisible(false);
        }
    }
    
    private void showMessage(Label messageLabel, String message, boolean isSuccess) {
        messageLabel.setText(message);
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: 500;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: 500;");
        }
    }
    
    private void addButtonHoverEffect(Button button) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> {
            button.setStyle(button.getStyle() + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
