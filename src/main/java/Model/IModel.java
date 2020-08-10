package Model;

import Server.IServerStrategy;
import Server.ServerStrategyGenerateMaze;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {
    void generateMaze(int r, int c);
    void solveMaze();
    Solution getSolution();
    void connectToServer(IServerStrategy srvst);
    void moveChar(KeyCode move);
    Maze getMaze();
    Position getPosition();
    void SaveMaze(File saveFile );
    void LoadMaze(File loadFile);
    void tryAgain();

    void stopServer();
}
