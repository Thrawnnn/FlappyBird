package com.example.flappybirdgame;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirdGame extends Application {
    private Pane gamePane;
    private Circle bird;
    private List<Rectangle> obstacles;
    private static final double GRAVITY = 0.6;
    private static final double JUMP_FORCE = -10;
    private double birdVelocity = 0;
    private boolean gameRunning = true;
    private AnimationTimer gameLoop;
    private int interval = 0;
    private Random rand = new Random();
    private int score = 0;
    private Text scoreText;

    @Override
    public void start(Stage primaryStage) {
        gamePane = new Pane();
        Scene scene = new Scene(gamePane, 1900, 1000);
        scene.setFill(Color.LIGHTBLUE);
        primaryStage.setTitle("JavaFX Flappy Bird DEMO");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        createHills();
        createSun();
        createBird();
        DrawMainMenu();
        obstacles = new ArrayList<>();

        scene.setOnMouseClicked(event -> {
            if (gameRunning) {
                birdVelocity = JUMP_FORCE;
            } else {
                resetGame();
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    updateGame();
                }
            }
        };
        gameLoop.start();
        primaryStage.show();

        Timeline obstacleTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.01), event -> {
                    if (gameRunning) {
                        interval = interval + 250;
                        double gap = rand.nextDouble(150, 400);
                        double obstacleHeight = 100 + rand.nextDouble() * 300;
                        createObstacle(400 + interval, obstacleHeight, gap);
                    }
                })
        );

        Timeline scoreTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1.75), event -> {
                    if (gameRunning) {
                        score++;
                        updateScore();
                    }
                })
        );

        obstacleTimeline.setCycleCount(Animation.INDEFINITE);
        obstacleTimeline.play();

        scoreTimeline.setCycleCount(Animation.INDEFINITE);
        scoreTimeline.play();
    }

    private void createBird() {
        bird = new Circle(50, 300, 20, Color.YELLOW);
        gamePane.getChildren().add(bird);
    }

    private void createHills() {
        Ellipse hill_left = new Ellipse(120, 1015, 1000, 420);
        hill_left.setFill(Color.LIGHTGREEN);
        Ellipse hill_right = new Ellipse(1000, 1015, 1350, 435);
        hill_right.setFill(Color.LIGHTGREEN);

        gamePane.getChildren().add(hill_left); // Hills!!!
        gamePane.getChildren().add(hill_right);
    }

    public void DrawDeathBox() {
        Rectangle rectangle = new Rectangle(); // death box derma for when you die in the game, parent of the main menu
        Rectangle outline = new Rectangle();

        outline.setX(808.0f);
        outline.setY(403.0f);
        outline.setWidth(304.0f);
        outline.setHeight(154.0f);

        outline.setArcWidth(33.0);
        outline.setArcHeight(23.0);

        rectangle.setX(810.0f);
        rectangle.setY(405.0f);
        rectangle.setWidth(300.0f);
        rectangle.setHeight(150.0f);

        rectangle.setArcWidth(30.0);
        rectangle.setArcHeight(20.0);

        Text death = new Text(825, 480, "You have died. Click To Restart!");
        death.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        Text deathScoreText = new Text(875, 500, "Your score was: " + score);
        deathScoreText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        Group root = new Group(outline,rectangle, death, deathScoreText);
        rectangle.setFill(Color.WHITESMOKE);
        outline.setFill(Color.BLACK);

        gamePane.getChildren().add(root);
    }

    public void DrawMainMenu() {
        gameRunning = false; // stops the game (DON'T USE THE gameLoop.end() method!!! This will break the program!)
        gamePane.getChildren().remove(bird);

        Rectangle rectangle = new Rectangle();
        Rectangle outline = new Rectangle();

        outline.setX(808.0f); // outline for the rectangle
        outline.setY(403.0f);
        outline.setWidth(304.0f);
        outline.setHeight(154.0f);

        outline.setArcWidth(33.0); // arcs for the curved outline edges
        outline.setArcHeight(23.0);

        rectangle.setX(810.0f); // actual rectangle which the text is drawn on
        rectangle.setY(405.0f);
        rectangle.setWidth(300.0f);
        rectangle.setHeight(150.0f);

        rectangle.setArcWidth(30.0); // arcs for the curved rectangle edges
        rectangle.setArcHeight(20.0);

        Text mainString = new Text(855, 480, "Click anywhere to Play!"); // text for what is supposed to be displayed
        mainString.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        Group menu = new Group(outline,rectangle);
        Group root = new Group(menu,mainString);
        rectangle.setFill(Color.WHITESMOKE);
        outline.setFill(Color.BLACK);

        gamePane.getChildren().add(root);
    }

    private void createSun() {
        Circle sun = new Circle(50, 50, 40, Color.YELLOW);
        gamePane.getChildren().add(sun); // sun object -_-
    }

    private void createObstacle(double x, double obstacleHeight, double gap) {
        Rectangle topObstacle = new Rectangle(x, 0, 50, obstacleHeight);
        Rectangle bottomObstacle = new Rectangle(x, obstacleHeight + gap, 50, gamePane.getHeight() - obstacleHeight - gap);

        topObstacle.setFill(Color.GREEN);
        bottomObstacle.setFill(Color.GREEN);

        obstacles.add(topObstacle);
        obstacles.add(bottomObstacle);

        gamePane.getChildren().add(topObstacle);
        gamePane.getChildren().add(bottomObstacle);
    }

    private void updateGame() {
        birdVelocity += GRAVITY;
        bird.setCenterY(bird.getCenterY() + birdVelocity);

        if (bird.getCenterY() < 0 || bird.getCenterY() > gamePane.getHeight()) {
            death();
        }

        for (Rectangle obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - 2);

            if (obstacle.getBoundsInParent().intersects(bird.getBoundsInParent())) {
                death();
            }
        }
    }

    private void resetGame() {
        gamePane.getChildren().clear();
        birdVelocity = 0;
        bird.setCenterY(300);
        gameRunning = true;
        createHills();
        createBird();
        createSun();
        obstacles.clear();
        interval = 0;
        score = 0;
        updateScore();
        gameLoop.start();
    }

    private void death() {
        gameRunning = false;
        gameLoop.stop();
        gamePane.getChildren().remove(bird); // "death" effect for the game, ends the loop and stops the animation. After that it draws the UI box seen in the above code
        DrawDeathBox();
    }

    private void updateScore() {
        if (scoreText != null) {
            gamePane.getChildren().remove(scoreText);
        }
        scoreText = new Text(20, 50, "Score: " + score);
        scoreText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
        gamePane.getChildren().add(scoreText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
