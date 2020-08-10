package View;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    public javafx.scene.control.Label labtest;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Label getLabtest() {
        return labtest;
    }

    public void setLabtest(Label labtest) {
        this.labtest = labtest;
    }
}
