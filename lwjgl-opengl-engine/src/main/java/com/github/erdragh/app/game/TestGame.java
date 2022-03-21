package com.github.erdragh.app.game;

import static org.lwjgl.glfw.GLFW.*;

import com.github.erdragh.app.engine.GameItem;
import com.github.erdragh.app.engine.IGameLogic;
import com.github.erdragh.app.engine.Window;
import com.github.erdragh.app.engine.graphics.Mesh;

import org.joml.Vector3f;

public class TestGame implements IGameLogic {
    private int displxInc = 0, displyInc = 0, displzInc = 0, scaleInc = 0;
    private final Renderer renderer;
    private Mesh mesh;

    private GameItem[] gameItems;

    public TestGame() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        float[] positions = new float[] {
            -0.5f,  0.5f, -1.0f,
            -0.5f, -0.5f, -1.0f,
             0.5f, -0.5f, -1.0f,
             0.5f,  0.5f, -1.0f
        };
        float[] colors = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[] {
            0, 1, 3, 3, 1, 2
        };
        mesh = new Mesh(positions, colors, indices);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(0,0,-2);
        gameItems = new GameItem[] { gameItem };
    }

    @Override
    public void input(Window window) {
        displyInc = 0;
        displxInc = 0;
        displzInc = 0;
        scaleInc = 0;
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displyInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displyInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displxInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displxInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            displzInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displzInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_Z)) {
            scaleInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            scaleInc = 1;
        }
    }

    @Override
    public void update(float interval) {
        for (GameItem gameItem : gameItems) {
            //Update position
            Vector3f itemPos = gameItem.getPosition();
            float posx = itemPos.x + displxInc * .01f;
            float posy = itemPos.y + displyInc * .01f;
            float posz = itemPos.z + displzInc * .01f;
            gameItem.setPosition(posx, posy, posz);

            //Update scale
            float scale = gameItem.getScale();
            scale += scaleInc * 0.05f;
            if (scale < 0) scale = 0;
            gameItem.setScale(scale);

            //update rotation angle
            float rotation = gameItem.getRotation().z + 1.5f;
            if (rotation > 360) rotation = 0;
            gameItem.setRotation(0, 0, rotation);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) gameItem.getMesh().cleanup();
    }
}
