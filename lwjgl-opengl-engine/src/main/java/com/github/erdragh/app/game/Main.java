package com.github.erdragh.app.game;

import com.github.erdragh.app.engine.GameEngine;
import com.github.erdragh.app.engine.IGameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new TestGame();
            GameEngine gameEngine = new GameEngine("OpenGL Engine", 1920, 1080, vSync, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
