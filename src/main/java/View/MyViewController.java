package View;

import ViewModel.MyViewModel;
//import animatefx.animation.FadeIn;
import animatefx.animation.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyViewController implements IView , Observer  {
    public Pane pane;
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public TextField rowNumber;
    public TextField colNumber;
    private Stage stage;
    public Button solution;
    public Button tryAgain;
    public Button generate;
    public MenuItem menuItemSave;
    public MenuItem menuItemHelp;
    public MediaPlayer player;
    private  Timer timer;

    private boolean solutionPushed = false;
    private boolean isChanged = false;
    private boolean oneMove = true;
    private double xP;
    private double yP;

    public void selectOptionProperties(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("Properties.fxml"));
            Scene scene = new Scene(root, 379  , 154);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {

        }
    }

    @Override
    public void selectFileNew() {
        selectGenerateMaze();
    }

    @Override
    public void selectFileSave() {

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.maze"));
        fc.setInitialFileName("MyMaze");
        File saveFile = fc.showSaveDialog(stage);
        viewModel.SaveMaze(saveFile);
        isChanged = false;

    }

    @Override
    public void selectFileLoad() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.maze"));
        File loadFile = fc.showOpenDialog(stage);
        if(loadFile==null){
            System.out.println("Error");
        }
        else {
            viewModel.LoadMaze(loadFile);
            new FadeIn(mazeDisplayer).play();
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setPosition(viewModel.getPos());
            mazeDisplayer.requestFocus();
            menuItemSave.setDisable(false);
            isChanged = false;
        }

    }

    @Override
    public void selectMenuExit() {
        if(isChanged) {
            Stage alert = new Stage();
            //timer.cancel();
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Exit Window");
            alert.setMinWidth(250);

            Label label = new Label();
            Button save = new Button("Save");
            save.setOnAction(event -> {
                selectFileSave();
                alert.close();
                stage.close();
            });
            Button dontSave = new Button("Don't save");
            dontSave.setOnAction(event -> {
                alert.close();
                stage.close();
            });
            Button cancel = new Button("Cancel");
            cancel.setOnAction(event -> alert.close());

            label.setText("Want to save your changes?");
            VBox layout = new VBox(10);
            layout.getChildren().addAll(label, save, dontSave, cancel);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout);
            alert.setScene(scene);
            alert.show();
        }
        else{
            stage.close();
            //timer.cancel();
        }
        viewModel.stopSever();

    }

    @Override
    public void selectHelp() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("Help.fxml"));
            Scene scene = new Scene(root, 1241  , 684);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("HelpStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void selectAbout() {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("About.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 847  , 527);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("AboutStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            AboutController controller = fxmlLoader.getController();
            double sceneWidth = scene.getHeight();
            double msgWidth = controller.getLabtest().getLayoutBounds().getHeight();

            KeyValue initKeyValue = new KeyValue(controller.getLabtest().translateYProperty(), sceneWidth);
            KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

            KeyValue endKeyValue = new KeyValue(controller.getLabtest().translateYProperty(), -1.0
                    * msgWidth);
            KeyFrame endFrame = new KeyFrame(Duration.seconds(13), endKeyValue);

            Timeline timeline = new Timeline(initFrame, endFrame);

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

        } catch (Exception e) {

        }
    }


    public void clickOnMaze(){
        mazeDisplayer.requestFocus();
    }



    @Override
    public void selectGenerateMaze() {

        if(solutionPushed){
            selectSolveMaze();
        }
        mazeDisplayer.setCompleted(false);
        int row;
        int col;
        if(isValidNumber(rowNumber.getText())&& isValidNumber(colNumber.getText())){
            row = Integer.parseInt(rowNumber.getText());
            col = Integer.parseInt(colNumber.getText());
            viewModel.generateMaze(row,col);
            new FadeIn(mazeDisplayer).play();
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setMvc(this);
            isChanged=true;
            solution.setDisable(false);
            tryAgain.setDisable(false);
            menuItemSave.setDisable(false);
            player.stop();


            Media media = new Media(new File("./resources/Music/Sailing.mp3").toURI().toString());
            player = new MediaPlayer(media);
            player.setVolume(0.3);
            player.play();


        }
        else {
            Stage alert = new Stage();
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("invalid number");
            alert.setMinWidth(250);

            Label label = new Label();
            Button b = new Button("Return");
            b.setOnAction(event -> alert.close());
            label.setText("invalid number, please insert again");
            VBox layout = new VBox(10);
            layout.getChildren().addAll(label,b);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout);
            alert.setScene(scene);
            alert.showAndWait();

        }


    }

    @Override
    public void selectSolveMaze() {
        if(solutionPushed==false){
            viewModel.solveMaze();
            mazeDisplayer.setSolution(viewModel.getSolution());
            mazeDisplayer.drawPosition();
            solution.setText("Clean Solution");
            solutionPushed=true;
            Media media = new Media(new File("./resources/Music/solveSound.wav").toURI().toString());
            MediaPlayer player2 = new MediaPlayer(media);
            player2.play();
        }
        else{
            solution.setText("Solve Maze");
            solutionPushed = false;
            if(mazeDisplayer.isCompleted() == false) {
                mazeDisplayer.drawMaze();
                mazeDisplayer.drawPosition();
            }
        }
    }

    public void selectTryAgain() {
        viewModel.tryAgain();

    }


    private boolean isValidNumber(String str){
        int number;
        try{
            number = Integer.parseInt(str);

        }catch (NumberFormatException e){
            return false;
        }
        if(number<1 || number>1000){
            return false;
        }
        return true;
    }



    @Override
    public void update(Observable o, Object arg) {
        if(o==viewModel){
            mazeDisplayer.setPosition(viewModel.getPos());
            //isChanged=true;
        }
    }

    public void setViewModel(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        stage.setScene(scene);
        stage.setResizable(true);
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.widthProperty().bind(pane.widthProperty());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.heightProperty().bind(pane.heightProperty());
            }
        });
        stage.setScene(scene);
        stage.hide();
        stage.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        if(viewModel.getMaze()!=null)
            isChanged = true;
        keyEvent.consume();
    }

    public void setStage(Stage primaryStage) {

        stage = primaryStage;
        oneMove = true;
        rowNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    rowNumber.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        colNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    colNumber.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


    }




    public void handleScroll(ScrollEvent event) {
        //System.out.println(mazeDisplayer.getScaleX());
        if (!event.isControlDown()) return;
            if (mazeDisplayer.getScaleX() <= 1 && mazeDisplayer.getScaleX() >= 0.25) {
                // System.out.println(mazeDisplayer.getScaleX());
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 2.0 - zoomFactor;
                }

                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomFactor);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomFactor);
            } else if (mazeDisplayer.getScaleX() > 1) {
                mazeDisplayer.setScaleX(1);
                mazeDisplayer.setScaleY(1);
            } else if (mazeDisplayer.getScaleX() < 0.25) {
                mazeDisplayer.setScaleX(0.25);
                mazeDisplayer.setScaleY(0.25);
            }
            event.consume();
        }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public void setPlayer(MediaPlayer player){
        this.player = player;
    }

    /*
    public void handleDrag(MouseEvent mouseEvent) {
        if(oneMove == false)
            return;
        timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        double a = mouseEvent.getX() - xP;
                        try {
                            if (a > 30) {

                                oneMove = false;
                                Robot robot = new Robot();
                                robot.keyPress(java.awt.event.KeyEvent.VK_RIGHT);
                                robot.keyRelease(java.awt.event.KeyEvent.VK_RIGHT);
                            } else if (a < -30) {
                                oneMove = false;
                                Robot robot = new Robot();
                                robot.keyPress(java.awt.event.KeyEvent.VK_LEFT);
                                robot.keyRelease(java.awt.event.KeyEvent.VK_LEFT);
                            }
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 200); // 1000 = 1 Sek.

        oneMove = true;
    }

    public void handlePress(MouseEvent mouseEvent) {
        xP = mouseEvent.getX();
        yP = mouseEvent.getY();
    }

    public void handleRelease(MouseEvent mouseEvent) {
        timer.cancel();
        oneMove = true;
    }
    */
}
