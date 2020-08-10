import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
//import animatefx.animation.*;
import animatefx.animation.FadeInDown;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.File;
import java.net.URL;

public class Main extends Application {
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);



        primaryStage.setTitle("Maze");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("Help.fxml");
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 1059, 813);
        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());

        //primaryStage.setScene(scene);


        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);


        Media media = new Media(new File("./resources/Music/Theme.mp3").toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.play();


        primaryStage.setScene(scene);


        MyViewController view = fxmlLoader.getController();

        view.setPlayer(player);
        window.setOnCloseRequest(event -> {
            event.consume();
            view.selectMenuExit();
        });
        view.setStage(primaryStage);
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);



        primaryStage.show();
        new FadeInDown(root).play();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

//  primaryStage.setScene(new Scene(root, 759, 513));