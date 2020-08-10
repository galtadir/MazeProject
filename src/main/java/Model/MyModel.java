package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;

import algorithms.mazeGenerators.Maze;
import Server.IServerStrategy;
import Server.ServerStrategyGenerateMaze;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import Server.ServerStrategySolveSearchProblem;
import Client.IClientStrategy;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.InvalidationListener;
import java.util.Observable;
import java.util.Properties;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import sun.misc.IOUtils;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import sun.rmi.runtime.Log;


public class MyModel extends Observable implements IModel {

    private Maze maze;
    private Position pos;
    private Solution sol;
    private Server server1;
    private Server server2;
    private boolean server1Used=false;
    private boolean server2Used = false;


    public MyModel(){ }




    private static final Logger LOG = LogManager.getLogger();


    public void generateMaze(int r, int c) {
        IServerStrategy mazeg = new ServerStrategyGenerateMaze();
        connectToServer(mazeg);
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {

                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{r, c};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = ((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[mazeDimensions[0] * mazeDimensions[1] + 12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        pos = maze.getStartPosition();
                        setChanged();
                        notifyObservers();

                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            Properties prop = new Properties();
            InputStream input = new FileInputStream("./resources/config.properties");
            prop.load(input);
            /*
            poolThreadTextBox.setText(prop.getProperty("db.pool"));
            propChoiceBox.setValue(prop.getProperty("db.searchingAlgorithm"));
            mazeGenChoiceBox.setValue(prop.getProperty("db.mazeGenerator"));
            */
            LOG.info("Cient connected to server with port 5400 via strategy " + client.getClientStrategy() + " With pool " + prop.getProperty("db.pool") + ".");
            LOG.info("Cient gentrated a maze via " +prop.getProperty("db.mazeGenerator") + " generating strategy");
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        } catch (FileNotFoundException e) {
            LOG.error("Could not load ./resources/config.properties");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Could not communicate with server.");
            e.printStackTrace();
        }

    }

    @Override
    public void solveMaze() {
        IServerStrategy mazes = new ServerStrategySolveSearchProblem();
        connectToServer(mazes);
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        sol = (Solution)fromServer.readObject();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
            Properties prop = new Properties();
            InputStream input = new FileInputStream("./resources/config.properties");
            prop.load(input);
            LOG.info("Cient connected to server with port 5401 via strategy " + client.getClientStrategy() + " With pool " + prop.getProperty("db.pool") + ".");
            LOG.info("Cient solved a maze via " +prop.getProperty("db.searchingAlgorithm") + " solving strategy. Maze was solved in " + sol.getSolutionPath().size() + " steps.");
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        } catch (FileNotFoundException e) {
            LOG.error("Could not load ./resources/config.properties");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Could not communicate with server.");
            e.printStackTrace();
        }

    }

    public void tryAgain(){
        pos = maze.getStartPosition();
        setChanged();
        notifyObservers();
    }

    @Override
    public Solution getSolution() {
        return sol;
    }

    @Override
    public void moveChar(KeyCode move) {
        boolean changed = false;
        Position p;
        int[][] mat = maze.getMat();
        switch (move) {
            case UP:
                if(pos.getRowIndex()-1>=0 &&mat[pos.getRowIndex()-1][pos.getColumnIndex()]==0){
                    p= new Position(pos.getRowIndex()-1,pos.getColumnIndex());
                    pos=p;
                    changed=true;
                }
                break;
            case W:
                if(pos.getRowIndex()-1>=0 &&mat[pos.getRowIndex()-1][pos.getColumnIndex()]==0){
                    p= new Position(pos.getRowIndex()-1,pos.getColumnIndex());
                    pos=p;
                    changed=true;
                }
                break;
            case DOWN:
                if(pos.getRowIndex()+1<mat.length &&mat[pos.getRowIndex()+1][pos.getColumnIndex()]==0) {
                    p = new Position(pos.getRowIndex() + 1, pos.getColumnIndex());
                    pos = p;
                    changed=true;
                }
                break;
            case X:
                if(pos.getRowIndex()+1<mat.length &&mat[pos.getRowIndex()+1][pos.getColumnIndex()]==0) {
                    p = new Position(pos.getRowIndex() + 1, pos.getColumnIndex());
                    pos = p;
                    changed=true;
                }
                break;
            case RIGHT:
                if(pos.getColumnIndex()+1<mat[0].length && mat[pos.getRowIndex()][pos.getColumnIndex()+1]==0) {
                    p = new Position(pos.getRowIndex(), pos.getColumnIndex() + 1);
                    pos = p;
                    changed=true;
                }
                break;
            case D:
                if(pos.getColumnIndex()+1<mat[0].length && mat[pos.getRowIndex()][pos.getColumnIndex()+1]==0) {
                    p = new Position(pos.getRowIndex(), pos.getColumnIndex() + 1);
                    pos = p;
                    changed=true;
                }
                break;
            case LEFT:
                if(pos.getColumnIndex()-1>=0 &&mat[pos.getRowIndex()][pos.getColumnIndex()-1]==0){
                    p= new Position(pos.getRowIndex(),pos.getColumnIndex()-1);
                    pos=p;
                    changed=true;
                }
                break;
            case A:
                if(pos.getColumnIndex()-1>=0 &&mat[pos.getRowIndex()][pos.getColumnIndex()-1]==0){
                    p= new Position(pos.getRowIndex(),pos.getColumnIndex()-1);
                    pos=p;
                    changed=true;
                }
                break;
            case Q:
                if(pos.getColumnIndex()-1>=0 && pos.getRowIndex()-1>=0 && mat[pos.getRowIndex()-1][pos.getColumnIndex()-1]==0 && ( mat[pos.getRowIndex()-1][pos.getColumnIndex()]==0||mat[pos.getRowIndex()][pos.getColumnIndex()-1]==0)){
                    p= new Position(pos.getRowIndex()-1,pos.getColumnIndex()-1);
                    pos=p;
                    changed=true;
                }
                break;
            case E:
                if(pos.getColumnIndex()+1<mat[0].length && pos.getRowIndex()-1>=0 && mat[pos.getRowIndex()-1][pos.getColumnIndex()+1]==0 && ( mat[pos.getRowIndex()-1][pos.getColumnIndex()]==0||mat[pos.getRowIndex()][pos.getColumnIndex()+1]==0)){
                    p= new Position(pos.getRowIndex()-1,pos.getColumnIndex()+1);
                    pos=p;
                    changed=true;
                }
                break;
            case C:
                if(pos.getColumnIndex()+1<mat[0].length && pos.getRowIndex()+1<mat.length && mat[pos.getRowIndex()+1][pos.getColumnIndex()+1]==0 && ( mat[pos.getRowIndex()+1][pos.getColumnIndex()]==0||mat[pos.getRowIndex()][pos.getColumnIndex()+1]==0)){
                    p= new Position(pos.getRowIndex()+1,pos.getColumnIndex()+1);
                    pos=p;
                    changed=true;
                }
                break;
            case Z:
                if(pos.getColumnIndex()-1>=0&& pos.getRowIndex()+1<=mat.length && mat[pos.getRowIndex()+1][pos.getColumnIndex()-1]==0 && ( mat[pos.getRowIndex()+1][pos.getColumnIndex()]==0||mat[pos.getRowIndex()][pos.getColumnIndex()-1]==0)){
                    p= new Position(pos.getRowIndex()+1,pos.getColumnIndex()-1);
                    pos=p;
                    changed=true;
                }
                break;



        }
        if(changed==true){
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze){
        this.maze=maze;
    }

    @Override
    public Position getPosition() {
        return pos;
    }

    @Override
    public void SaveMaze(File saveFile ) {

        if(saveFile!=null){
            //String name = saveFile.getName();
            //File file = new File(name);
            try{
                //Files.copy(file.toPath(),saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                byte[] tempb = maze.toByteArray();
                byte[] tempc = new byte[tempb.length + 2];
                for(int i = 0; i < tempb.length; i++){
                    tempc[i] = tempb[i];
                }
                tempc[tempb.length] = (byte)pos.getRowIndex();
                tempc[tempb.length+1] = (byte)pos.getColumnIndex();
                saveFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(saveFile);
                fos.write(tempc);

            } catch (IOException e) {
                LOG.error("Could not save maze.");
                e.printStackTrace();

            }
        }
    }


    @Override
    public void LoadMaze(File loadFile) {
        if(loadFile!=null){
            try {
                byte[] b = Files.readAllBytes(Paths.get(loadFile.getPath()));
                int row = (b[0] & 0xFF)*255 + (b[1] & 0xFF) ;
                int col = (b[2] & 0xFF)*255 +(b[3] & 0xFF);
                int l = row;
                int w = col;
                int[][] mat = new int[l][w];
                Position startPosition;
                Position goalPosition;
                startPosition = new Position(b[4] * 255 + (b[5] & 0xFF), b[6] * 255 + (b[7] & 0xFF));
                goalPosition = new Position(b[8] * 255 + (b[9] & 0xFF), b[10] * 255 + (b[11] & 0xFF));

                for(int i = 0; i < mat.length; ++i) {
                    for(int j = 0; j < mat[0].length; ++j) {
                        mat[i][j] = b[j + i * mat[0].length + 12];
                    }
                }

                Maze m = new Maze();
                m.setMaze(mat);
                m.setStartPosition(startPosition);
                m.setGoalPosition(goalPosition);
                setMaze(m);
                int pR = b[b.length-2] & 0xFF;
                int pC = b[b.length-1] & 0xFF;
                Position p = new Position(pR,pC);
                setPos(p);
            } catch (IOException e) {
                LOG.error("Could not load maze.");
                e.printStackTrace();
            }
        }
    }



    public void setPos(Position pos){
        this.pos=pos;
    }


    @Override
    public void connectToServer(IServerStrategy srvst) {

        if(srvst instanceof ServerStrategyGenerateMaze) {
            if(!server1Used){
                server1 = new Server(5400, 1000, srvst);
                server1Used=true;
            }
            server1.start();
        }
        else if(srvst instanceof ServerStrategySolveSearchProblem){
            if(!server2Used){
                server2 = new Server(5401, 1000, srvst);
                server2Used=true;
            }
            server2.start();
        }
    }

    @Override
    public void stopServer() {
        if (server1Used) {
            server1.stop();
            LOG.info("Stopping server: " + server1.toString());
        }
        if (server2Used) {
            server2.stop();
            LOG.info("Stopping server: " + server2.toString());
        }

    }

}
