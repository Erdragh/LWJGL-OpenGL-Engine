package com.github.erdragh.app.game;

import static org.lwjgl.glfw.GLFW.*;

import com.github.erdragh.app.engine.GameItem;
import com.github.erdragh.app.engine.IGameLogic;
import com.github.erdragh.app.engine.MouseInput;
import com.github.erdragh.app.engine.Window;
import com.github.erdragh.app.engine.graphics.Camera;
import com.github.erdragh.app.engine.graphics.Mesh;
import com.github.erdragh.app.engine.graphics.ModelLoader;
import com.github.erdragh.app.engine.graphics.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestGame implements IGameLogic {
    private static final float MOUSE_SENSITIVITY = .2f, CAMERA_POS_STEP = .05f;

    private final Camera camera;
    private final Vector3f cameraInc;
    private final Renderer renderer;

    private GameItem[] gameItems;

    public TestGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        
        // Mesh mesh = ModelLoader.loadMesh("/models/cube.obj");
        Mesh mesh = ModelLoader.loadMesh("/models/bunny.obj");
        // Texture texture = new Texture("lwjgl-opengl-engine/src/main/resources/textures/grassblock.png");
        // mesh.setTexture(texture);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(.5f);
        gameItem.setPosition(0,0,-2);
        gameItems = new GameItem[]{gameItem};

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        //Update Camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        //Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) gameItem.getMesh().cleanup();
    }
}
