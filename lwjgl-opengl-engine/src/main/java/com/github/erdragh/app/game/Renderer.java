package com.github.erdragh.app.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import com.github.erdragh.app.engine.GameItem;
import com.github.erdragh.app.engine.Utils;
import com.github.erdragh.app.engine.Window;
import com.github.erdragh.app.engine.graphics.Camera;
import com.github.erdragh.app.engine.graphics.DirectionalLight;
import com.github.erdragh.app.engine.graphics.Mesh;
import com.github.erdragh.app.engine.graphics.PointLight;
import com.github.erdragh.app.engine.graphics.ShaderProgram;
import com.github.erdragh.app.engine.graphics.Transformation;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f, Z_FAR = 1000.f;

    private Transformation transformation;

    private ShaderProgram shaderProgram;

    private float specularPower;

    public Renderer() {      
        transformation = new Transformation();  
        specularPower = 10f;
    }
    
    public void init(Window window) throws Exception { 
        //Create Shaders
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));       
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        //Create uniforms for modelView and projection matrices and texture sampler
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        //create uniform for material
        shaderProgram.createMaterialUniform("material");
        //Create Lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight, DirectionalLight directionalLight) {
        clear();

        if (window.isResized()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        //Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Update view matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        //update light uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        //Get a copy of the light object and transform its position to view space
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);

        // Vector3f lightPos = pointLight.getPosition();
        // Vector4f aux = new Vector4f(lightPos.x, lightPos.y, lightPos.z, 1);
        // aux.mul(viewMatrix);
        // Vector3f viewLightPos = new Vector3f(aux.x,aux.y,aux.z);
        // PointLight viewLight = new PointLight(pointLight.getColor(), viewLightPos, pointLight.getIntensity(), pointLight.getAttenuation());
        // shaderProgram.setUniform("pointLight", viewLight);

        DirectionalLight currDirrLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirrLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirrLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirrLight);

        shaderProgram.setUniform("texture_sampler", 0);

        //Render each gameItem
        for (GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            //Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            //Render the mesh for this game item
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) shaderProgram.cleanup();
    }
}
