package org.example.javafxdb_sql_shellcode;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * The SplashScreen class provides functionality to display a splash screen
 * when the application starts.
 */
public class SplashScreen {

    /**
     * Displays a splash screen with a fade-in and fade-out effect.
     *
     * @param primaryStage     The primary stage of the application.
     * @param finishedCallback A Runnable to be executed when the splash screen finishes.
     */
    public static void show(Stage primaryStage, Runnable finishedCallback) {
        // Create the layout for the splash screen
        StackPane splashLayout = new StackPane();
        ImageView imageView = new ImageView(new Image("/SUNY_Farmingdale.png"));
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        splashLayout.getChildren().add(imageView);

        // Create a new scene for the splash screen
        Scene splashScene = new Scene(splashLayout, 807, 535);
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);
        splashStage.setScene(splashScene);

        // Create fade-in transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), splashLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Create fade-out transition
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), splashLayout);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            splashStage.close();
            finishedCallback.run();
        });

        // Set up the sequence of animations
        fadeIn.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> fadeOut.play());
            pause.play();
        });

        // Show the splash screen and start the fade-in animation
        splashStage.show();
        fadeIn.play();
    }
}