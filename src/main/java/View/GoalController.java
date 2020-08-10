package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class GoalController implements Initializable {

    public ImageView goalGif;
    public MazeDisplayer md;
    public javafx.scene.control.Button playa;
    public javafx.scene.control.Label labtest;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("./resources/Images/original.gif");
        Image image = new Image(file.toURI().toString());
        goalGif.setImage(image);


    }


    public void playAgain(){
        md.getMvc().selectGenerateMaze();
        Stage stage = (Stage) playa.getScene().getWindow();
        stage.close();
    }


    public MazeDisplayer getMd() {
        return md;
    }

    public void setMd(MazeDisplayer md) {
        this.md = md;
    }

    public Label getLabtest() {
        return labtest;
    }

    public void setLabtest(Label labtest) {
        this.labtest = labtest;
    }
}
