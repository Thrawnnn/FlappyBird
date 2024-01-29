package com.example.flappybirdgame;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.shape.Ellipse;
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
    private Text death;

    Random rand = new Random();

    @Override
    public void start(Stage primaryStage) {
        gamePane = new Pane();
        Scene scene = new Scene(gamePane, 400, 600);
        scene.setFill(Color.LIGHTBLUE);
        primaryStage.setTitle("Flappy Bird DEMO");
        primaryStage.setScene(scene);
        createHills();
        createBird();
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

        // Use Timeline for obstacle generation with a 5-second delay
        Timeline obstacleTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> {
                    if (gameRunning) {
                        createObstacle();
                    }
                })
        );
        obstacleTimeline.setCycleCount(Animation.INDEFINITE);
        obstacleTimeline.play();
    }

    private void createBird() {
        bird = new Circle(50, 300, 20, Color.YELLOW);
        gamePane.getChildren().add(bird);
    }

    private void createHills() {
        Ellipse hill_left = new Ellipse(120, 725, 900, 320);
        hill_left.setFill(Color.LIGHTGREEN);
        Ellipse hill_right = new Ellipse(1000, 725, 900, 320);
        hill_right.setFill(Color.LIGHTGREEN);

        gamePane.getChildren().add(hill_left);
        gamePane.getChildren().add(hill_right);
    }

    private void createObstacle() {
        double obstacleHeight = 100 + rand.nextDouble() * 300; 
        double gap = rand.nextDouble(150, 400);

        // Top obstacle
        Rectangle topObstacle = new Rectangle(400, 0, 50, obstacleHeight);
        topObstacle.setFill(Color.GREEN);
        obstacles.add(topObstacle);
        gamePane.getChildren().add(topObstacle);

        // Bottom obstacle
        Rectangle bottomObstacle = new Rectangle(400, obstacleHeight + gap, 50, gamePane.getHeight() - obstacleHeight - gap);
        bottomObstacle.setFill(Color.GREEN);
        obstacles.add(bottomObstacle);
        gamePane.getChildren().add(bottomObstacle);
    }


    private void death() {
        gameRunning = false;
        gameLoop.stop();
        //death = new Text(120, 180, "You have died. Click To Restart!");
       //gamePane.getChildren().add(death); // no reason to add these, looks bad.
        gamePane.getChildren().remove(bird);

    }

    private void updateGame() {
        birdVelocity += GRAVITY;
        bird.setCenterY(bird.getCenterY() + birdVelocity);

        if (bird.getCenterY() < 0 || bird.getCenterY() > gamePane.getHeight()) {
            death();
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obstacle = obstacles.get(i);
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
        obstacles.clear();
        createObstacle();
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
