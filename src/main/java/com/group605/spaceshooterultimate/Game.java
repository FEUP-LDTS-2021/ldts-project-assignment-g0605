package com.group605.spaceshooterultimate;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;
import com.group605.spaceshooterultimate.state.GameState;
import com.group605.spaceshooterultimate.state.MenuState;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;


public class Game {

    private final TerminalScreen screen;
    private final int width;
    private final int height;
    private int highscore = 0;

    //FPS Variables
    private boolean running = false;
    private final int FPS = 30;
    private double averageFPS;

    private GameState gameState;

    //KeyPress Part
    public enum action {
        UP, DOWN, LEFT, RIGHT,
        SHOOT, QUIT, NEXT, SINGLE,
        DOUBLE, BURST, OTHER
    }

    public Game(int width, int height) throws IOException, FontFormatException {
        AWTTerminalFontConfiguration fontConfig = loadSpaceShooterUltimateFont();
        Terminal terminal = createTerminal(width, height, fontConfig);
        this.screen = createScreen(terminal);
        addCloseScreenListener();
        this.height=height;
        this.width=width;
        this.gameState = new MenuState(this, screen); //Defines the Menu State has the initial state passing the object itself (game object)
    }

    public void changeGameState(GameState gameState){
        this.gameState = gameState;
    }


    public Terminal createTerminal(int width, int height, AWTTerminalFontConfiguration fontConfig) throws IOException {
        TerminalSize terminalSize = new TerminalSize(width, height + 1);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                .setInitialTerminalSize(terminalSize);
        terminalFactory.setForceAWTOverSwing(true);

        terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig);
        Terminal terminal = terminalFactory.createTerminal();
        return terminal;
    }

    private void addCloseScreenListener(){
        ((AWTTerminalFrame) screen.getTerminal()).addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                ((AWTTerminalFrame) screen.getTerminal()).dispose();
                System.exit(0);
            }
        });
    }

    public AWTTerminalFontConfiguration loadSpaceShooterUltimateFont() throws FontFormatException, IOException {
        File fontFile = new File("resources/Font/SpaceShooterUltimateGameFont.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        Font loadedFont = font.deriveFont(Font.PLAIN, 22);

        return AWTTerminalFontConfiguration.newInstance(loadedFont);
    }

    public Screen getScreen(){
        return screen;
    }


    public TerminalScreen createScreen(Terminal terminal) throws IOException {
        final TerminalScreen terminalScreen;
        terminalScreen = new TerminalScreen(terminal);

        terminalScreen.setCursorPosition(null);
        terminalScreen.startScreen();
        terminalScreen.doResizeIfNecessary();
        return terminalScreen;
    }

    public void closeTerminal() {
        try{
            screen.close(); // screen must be started
            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException, InterruptedException{
        running = true;

        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;

        // GAME LOOP
        while(running){

            this.gameState.selectController(); //When game starts running we advance to the controller of the first state

            startTime = System.nanoTime();
            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;


            try {
                Thread.sleep(waitTime);
            } catch (Exception e){
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == maxFrameCount){
                averageFPS = 1000.0 / ((totalTime / frameCount) / 100000000);
                frameCount = 0;
                totalTime = 0;

            }
        }
    }


    public action handleKeyPress() throws IOException{
        KeyStroke key = screen.pollInput();
        if (key == null) return action.OTHER;
        if (key.getKeyType() == KeyType.Escape) return action.QUIT;
        else if (key.getKeyType() == KeyType.Character && (key.getCharacter()=='w' || key.getCharacter()=='W')) return action.UP;
        else if (key.getKeyType() ==  KeyType.Character && (key.getCharacter()=='d' || key.getCharacter()=='D')) return action.RIGHT;
        else if (key.getKeyType() ==  KeyType.Character && (key.getCharacter()=='s' || key.getCharacter()=='S')) return action.DOWN;
        else if (key.getKeyType() ==  KeyType.Character && (key.getCharacter()=='a' || key.getCharacter()=='A')) return action.LEFT;
        else if (key.getKeyType() ==  KeyType.Enter) return action.NEXT;
        else if (key.getKeyType() == KeyType.Character && (key.getCharacter()==' ')) return action.SHOOT;
        else if (key.getKeyType() == KeyType.Character && (key.getCharacter()=='1')) return action.SINGLE;
        else if (key.getKeyType() == KeyType.Character && (key.getCharacter()=='2')) return action.DOUBLE;
        else if (key.getKeyType() == KeyType.Character && (key.getCharacter()=='3')) return action.BURST;
        else return action.OTHER;
    }

    public int getWidth() { return width;}

    public int getHeight() { return height;}

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }
}

