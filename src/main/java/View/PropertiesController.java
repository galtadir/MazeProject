package View;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable {

    public javafx.scene.control.ChoiceBox propChoiceBox;
    public javafx.scene.control.TextField poolThreadTextBox;
    public javafx.scene.control.ChoiceBox mazeGenChoiceBox;
    public javafx.scene.control.Button applyButton;


   public void clickedOnApply(){
       try {
           OutputStream output = new FileOutputStream("./resources/config.properties");
           Properties prop = new Properties();
           //prop.load(input);
           prop.setProperty("db.pool",poolThreadTextBox.getText());
           prop.setProperty("db.searchingAlgorithm",propChoiceBox.getValue().toString());
           prop.setProperty("db.mazeGenerator",mazeGenChoiceBox.getValue().toString());
           prop.store(output,null);
           Stage stage = (Stage) applyButton.getScene().getWindow();
           stage.close();

       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        propChoiceBox.getItems().addAll("BFS","DFS","BESTFS");
        mazeGenChoiceBox.getItems().addAll("MyMaze","Empty","SimpleMaze");
        try {
            InputStream input = new FileInputStream("./resources/config.properties");
            Properties prop = new Properties();
            prop.load(input);
            poolThreadTextBox.setText(prop.getProperty("db.pool"));
            propChoiceBox.setValue(prop.getProperty("db.searchingAlgorithm"));
            mazeGenChoiceBox.setValue(prop.getProperty("db.mazeGenerator"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
