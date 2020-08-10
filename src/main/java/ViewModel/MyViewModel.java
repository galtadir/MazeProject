package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;

import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o==model){
            setChanged();
            notifyObservers();
        }
    }

    public void tryAgain(){
        model.tryAgain();
    }

    public void generateMaze(int r, int c){
        model.generateMaze(r,c);
    }

    public void solveMaze(){model.solveMaze();}

    public void moveCharacter(KeyCode move){
        model.moveChar(move);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public Solution getSolution(){return model.getSolution();}

    public Position getPos(){
        return model.getPosition();
    }

    public void SaveMaze(File saveFile ){
        model.SaveMaze(saveFile);
    }

    public void LoadMaze(File loadFile){
        model.LoadMaze(loadFile);
    }
    public void stopSever(){
        model.stopServer();


    }

}
