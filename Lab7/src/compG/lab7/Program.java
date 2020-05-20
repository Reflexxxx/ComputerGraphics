package compG.lab7;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Program extends Application implements EventHandler<Event>{
    public static void main(String[] args) {
        launch(args);
    }

    private Stage main_stage;
    private Task7 task;

    @Override
    public void start(Stage stage) {
        main_stage = stage;
        SetTask();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, this);
        stage.show();
    }

    @Override
    public void handle(Event event) {
        if (event instanceof KeyEvent) {
            KeyEvent keve = (KeyEvent) event;
            switch (keve.getCode()) {
                case R: {
                    SetTask();
                    break;
                }
            }
        }
    }

    private void SetTask() {
        task = new Task7();

        Scene scene = task.getScene();
        Camera camera = new PerspectiveCamera();
        scene.setCamera(camera);
        scene.setFill(Color.SILVER);

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent ->
                task.AddPointToShape(new Point2D(mouseEvent.getSceneX(),mouseEvent.getSceneY())));

        main_stage.setScene(scene);
    }
}