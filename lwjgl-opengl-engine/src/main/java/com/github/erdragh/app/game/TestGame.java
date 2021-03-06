package com.github.erdragh.app.game;

import static org.lwjgl.glfw.GLFW.*;

import com.github.erdragh.app.engine.GameItem;
import com.github.erdragh.app.engine.IGameLogic;
import com.github.erdragh.app.engine.MouseInput;
import com.github.erdragh.app.engine.Window;
import com.github.erdragh.app.engine.graphics.Camera;
import com.github.erdragh.app.engine.graphics.DirectionalLight;
import com.github.erdragh.app.engine.graphics.Material;
import com.github.erdragh.app.engine.graphics.Mesh;
import com.github.erdragh.app.engine.graphics.ModelLoader;
import com.github.erdragh.app.engine.graphics.PointLight;
import com.github.erdragh.app.engine.graphics.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestGame implements IGameLogic {
    private static final float MOUSE_SENSITIVITY = .2f, CAMERA_POS_STEP = .05f;

    private final Camera camera;
    private final Vector3f cameraInc;
    private final Renderer renderer;

    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private Vector3f ambientLight;

    private float lightAngle;

    private GameItem[] gameItems;

    public TestGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        
        float reflectance = 1f;
        // Mesh mesh = ModelLoader.loadMesh("/models/cube.obj");
        Mesh mesh = ModelLoader.loadMesh("/models/bunny.obj");
        Texture texture = new Texture("lwjgl-opengl-engine/src/main/resources/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        
        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(.5f);
        gameItem.setPosition(0,0,-2);
        gameItems = new GameItem[]{gameItem};

        ambientLight = new Vector3f(.3f,.3f,.3f);
        Vector3f lightColor = new Vector3f(1,1,1);
        Vector3f lightPosition = new Vector3f(0,0,1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0f, 0f, 1f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
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
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_M)) {
            pointLight.getPosition().z = lightPos += 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_N)) {
            pointLight.getPosition().z = lightPos -= .1f;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
            window.toggleWireFrame();
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        //Update Camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        //Update camera based on mouse
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update directional light direction, intensity and colour
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) gameItem.getMesh().cleanup();
    }
}
