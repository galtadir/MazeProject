package View;

//import animatefx.animation.FadeIn;
import animatefx.animation.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;

public class MazeDisplayer extends Canvas {

    private int[][]mat;
    private ArrayList<AState> solPath;
    private int sR;
    private int sC;
    private int eR;
    private int eC;
    private int rP;
    private int cP;
    private MyViewController mvc;
    private boolean completed;
    private double xP;
    private double yP;


    public MazeDisplayer(){
        widthProperty().addListener(e-> {drawMaze(); drawPosition();});
        heightProperty().addListener(e-> {drawMaze(); drawPosition();});
        addMouseMotionListener(new MouseAdapter() {

            int previousY;

            @Override
            public void mousePressed(MouseEvent e) {
                previousY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                int y = e.getY();
                if (y < previousY) {
                    System.out.println("UP");
                } else if (y > previousY) {
                    System.out.println("DOWN");
                }

                previousY = y;
            }
        });
    };

    public void setMaze(Maze maze){
        this.mat= maze.getMat();
        Position start = maze.getStartPosition();
        Position end = maze.getGoalPosition();
        rP= start.getRowIndex();
        cP=start.getColumnIndex();
        sR=start.getRowIndex();
        sC=start.getColumnIndex();
        eR=end.getRowIndex();
        eC=end.getColumnIndex();
        drawMaze();
        drawPosition();
    }


    public void drawMaze(){
        if (mat != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / mat.length;
            double cellWidth = canvasWidth / mat[0].length;
            try {
                Image landImage = new Image(new FileInputStream(ImageMoveableBlock.get()));
                Image wallImage = new Image(new FileInputStream(ImageWallBlock.get()));
                Image goalImage = new Image(new FileInputStream(ImageGoalBlock.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat[i].length; j++) {
                        if (mat[i][j] == 0) {
                            gc.drawImage(landImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        else if (mat[i][j] == 1){
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                gc.drawImage(goalImage,eC * cellWidth, eR * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawSolution() {
        try {
            if (solPath != null) {
                double canvasHeight = getHeight();
                double canvasWidth = getWidth();
                double cellHeight = canvasHeight / mat.length;
                double cellWidth = canvasWidth / mat[0].length;
                Image solImage = new Image(new FileInputStream(ImageSolBlock.get()));
                GraphicsContext gc = getGraphicsContext2D();
                for (AState as : solPath) {
                    MazeState ms = (MazeState) as;
                    Position p = ms.getPosition();
                    if ((p.getRowIndex() != eR && p.getColumnIndex() != eC) || (p.getRowIndex() == eR && p.getColumnIndex() != eC) || (p.getRowIndex() != eR && p.getColumnIndex() == eC)){
                        new Wobble(gc.getCanvas()).play();
                        gc.drawImage(solImage, p.getColumnIndex() * cellWidth, p.getRowIndex() * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSolution(Solution sol) {
        solPath = sol.getSolutionPath();
        drawSolution();
    }

    public void setPosition(Position p){
        cleanPosition();
        rP = p.getRowIndex();
        cP = p.getColumnIndex();
        drawPosition();
    }

    public void drawPosition(){
        if(mat!=null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / mat.length;
            double cellWidth = canvasWidth / mat[0].length;
            xP = cP * cellWidth;
            yP = rP * cellHeight;
            try {
                Image charImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.drawImage(charImage, cP * cellWidth, rP * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(rP==eR && cP==eC){
                completed = true;
                Media media = new Media(new File("./resources/Music/goalSound2.mp3").toURI().toString());
                MediaPlayer player2 = new MediaPlayer(media);
                player2.play();
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Good Job!");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Goal.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root, 500  , 486);
                    scene.getStylesheets().add(getClass().getClassLoader().getResource("GoalStyle.css").toExternalForm());
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.show();
                    GoalController controller = fxmlLoader.getController();
                    double sceneWidth = scene.getWidth();
                    double msgWidth = controller.getLabtest().getLayoutBounds().getWidth();

                    KeyValue initKeyValue = new KeyValue(controller.getLabtest().translateXProperty(), sceneWidth);
                    KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

                    KeyValue endKeyValue = new KeyValue(controller.getLabtest().translateXProperty(), -1.0
                            * msgWidth);
                    KeyFrame endFrame = new KeyFrame(Duration.seconds(3), endKeyValue);

                    Timeline timeline = new Timeline(initFrame, endFrame);

                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();
                    controller.setMd(this);
                } catch (Exception e) {

                }
            }
        }
    }

    private void cleanPosition(){
        if(mat!=null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / mat.length;
            double cellWidth = canvasWidth / mat[0].length;
            GraphicsContext graphicsContext2D = getGraphicsContext2D();
            if(rP==sR && cP==sC){
                graphicsContext2D.setFill(Color.GOLD);
                graphicsContext2D.fillRect(cP * cellWidth, rP * cellHeight, cellWidth, cellHeight);
            }
            else{
                try {
                    Image wallImage = new Image(new FileInputStream(ImageMoveableBlock.get()));
                    GraphicsContext gc = getGraphicsContext2D();
                    gc.drawImage(wallImage, cP * cellWidth, rP * cellHeight, cellWidth, cellHeight);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public MyViewController getMvc() {
        return mvc;
    }

    public void setMvc(MyViewController mvc) {
        this.mvc = mvc;
    }

    public double getxP() {
        return xP;
    }

    public double getyP() {
        return yP;
    }

    //region Properties
    private StringProperty ImageMoveableBlock = new SimpleStringProperty();
    private StringProperty ImageWallBlock = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageSolBlock = new SimpleStringProperty();
    private StringProperty ImageGoalBlock = new SimpleStringProperty();

    public String getImageMoveableBlock() {
        return ImageMoveableBlock.get();
    }

    public void setImageMoveableBlock(String imageMoveableBlock) {
        this.ImageMoveableBlock.set(imageMoveableBlock);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageWallBlock(){
        return ImageWallBlock.get();
    }

    public void setImageWallBlock(String imageWallBlock) {
        this.ImageWallBlock.set(imageWallBlock);
    }

    public String getImageSolBlock() {
        return ImageSolBlock.get();
    }

    public void setImageSolBlock(String imageSolBlock) {
        this.ImageSolBlock.set(imageSolBlock);
    }

    public String getImageGoalBlock() {
        return ImageGoalBlock.get();
    }

    public void setImageGoalBlock(String imageGoalBlock) {
        this.ImageGoalBlock.set(imageGoalBlock);
    }

    //endregion
}
