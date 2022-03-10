package com.github.erdragh.app.engine.graphics;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
    private final int program;
    private int vertexShader, fragmentShader;

    public ShaderProgram() throws Exception {
        program = glCreateProgram();
        if (program == 0) throw new Exception("Could not create Shader");
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShader = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShader = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shader = glCreateShader(shaderType);
        if (shader == 0) throw new Exception("Error creating shader. Type: " + shaderType);

        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shader, 1024));
        }

        glAttachShader(program, shader);

        return shader;
    }

    public void link() throws Exception {
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0)
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));

        if (vertexShader != 0) {
            glDetachShader(program, vertexShader);
        }
        if (fragmentShader != 0) {
            glDetachShader(program, fragmentShader);
        }

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(program, 1024));
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (program != 0) {
            glDeleteProgram(program);
        }
    }
}
