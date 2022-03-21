package com.github.erdragh.app.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import com.github.erdragh.app.engine.GameItem;
import com.github.erdragh.app.engine.Utils;
import com.github.erdragh.app.engine.Window;
import com.github.erdragh.app.engine.graphics.ShaderProgram;
import com.github.erdragh.app.engine.graphics.Transformation;

import org.joml.Matrix4f;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f, Z_FAR = 1000.f;

    private Transformation transformation;

    private ShaderProgram shaderProgram;

    public Renderer() {      
        transformation = new Transformation();  
    }
    
    public void init(Window window) throws Exception { 
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));       
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");

        window.setClearColor(0f,0f,0f,0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems) {
        clear();

        if (window.isResized()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        //Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Render each gameItem
        for (GameItem gameItem : gameItems) {
            //Set world matrix for this item
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                gameItem.getPosition(), 
                gameItem.getRotation(), 
                gameItem.getScale());
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            //render the mesh for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) shaderProgram.cleanup();
    }
}
