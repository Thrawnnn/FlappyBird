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
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
    public Integer points;


    @Override
    public void start(Stage primaryStage) {

        gamePane = new Pane();
        Scene scene = new Scene(gamePane, 1900, 1000);
        scene.setFill(Color.LIGHTBLUE);
        primaryStage.setTitle("JavaFX Flappy Bird DEMO");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        createHills();
        createSun();
        createBird();
        DrawMainMenu();
        obstacles = new ArrayList<>();

        scene.setOnMouseClicked(event -> {
            if (gameRunning) {
                birdVelocity = JUMP_FORCE; // Click event to determine when the bird should move, as well as functionality for the menus.
            } else {
                resetGame(); // resets the game for the functionality, very hacky.
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    updateGame(); // calls the update game method to determine what happens while the game is running
                }
            }
        };
        gameLoop.start(); // technical stuff required for the code to work
        primaryStage.show();

        Timeline obstacleTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> {
                    if (gameRunning) {
                        createObstacle(); // hacky autogeneration fix, creates a timer then uses the end of that timer to create a new obstacle at a different location along the X Axis
                    }
                })
        );
        obstacleTimeline.setCycleCount(Animation.INDEFINITE); // animation technical stuff
        obstacleTimeline.play();
    }

    private void createBird() {
        bird = new Circle(50, 300, 20, Color.YELLOW);
        gamePane.getChildren().add(bird); // bird object (i love OOP!!!!)
    }

    private void createSun() {
        Circle sun = new Circle(50, 50, 40, Color.YELLOW);
        gamePane.getChildren().add(sun); // sun object -_-
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

        death = new Text(825, 480, "You have died. Click To Restart!");
        death.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        Group root = new Group(outline,rectangle, death);
        rectangle.setFill(Color.WHITESMOKE);
        outline.setFill(Color.BLACK);

        gamePane.getChildren().add(root);
    }

    public void DrawMainMenu() {
        gameRunning = false; // stops the game (DONT USE THE gameLoop.end() method!!! This will break the program!)

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

        Text mainString = new Text(855, 480, "Click anywhere to Play!"); // text for what is supposed to be dispplayed
        mainString.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

        Group root = new Group(outline,rectangle, mainString);
        rectangle.setFill(Color.WHITESMOKE);
        outline.setFill(Color.BLACK);

        gamePane.getChildren().add(root);
    }

    private void createHills() {
        Ellipse hill_left = new Ellipse(120, 1015, 1000, 420);
        hill_left.setFill(Color.LIGHTGREEN);
        Ellipse hill_right = new Ellipse(1000, 1015, 1350, 435);
        hill_right.setFill(Color.LIGHTGREEN);

        gamePane.getChildren().add(hill_left); // Hills!!!
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
        gamePane.getChildren().add(bottomObstacle); // obstacles, also handles very basic autogeneration
    }


    private void death() {
        gameRunning = false;
        gameLoop.stop();
        DrawDeathBox();
        gamePane.getChildren().remove(bird); // "death" effect for the game, ends the loop and stops the animation. After that it draws the derma box seen in the above code
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
            } // technical stuff for animation of the JavaFX program
        }
    }

    private void resetGame() {
        gamePane.getChildren().clear();
        birdVelocity = 0;
        bird.setCenterY(300);
        gameRunning = true;
        createHills();
        createSun();
        createBird();
        obstacles.clear();
        createObstacle();
        gameLoop.start();
    }// used to reset the game

    public static void main(String[] args) {
        launch(args);
    } // yeeee boiiii :DDDD
}
