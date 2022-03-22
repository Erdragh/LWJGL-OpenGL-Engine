package com.github.erdragh.app.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

public class Window {

    private long window;
    private int width, height;
    private final String title;
    private boolean resized, vSync;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
    }

    public void init() {
        //Setup an error callback. The default implementation will print to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        //Initiate GLFW. Otherwise most GLFW functions won't work
        if (!glfwInit()) throw new IllegalStateException("Couldn't initialize GLFW");

        //Configuring GLFW
        glfwDefaultWindowHints(); //redundant
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); //setting the window to hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); //the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        //Create the Window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Couldn't create the GLFW window");

        //Setup a key callback. It will be called everytime a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        // Setup resize callback
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        //Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        //Center the window
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        //Make the OpenGL context current;
        glfwMakeContextCurrent(window);
        //Enable V-Sync
        if (isvSync()) glfwSwapInterval(1);

        //Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0.0f,0.0f,0.0f,0.0f);

        glPrintDebugInformation();

        glEnable(GL_DEPTH_TEST);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        // System.out.println("Testing if " + keyCode + " is pressed: " + (glfwGetKey(window, keyCode) == GLFW_PRESS));
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }
    
    private void glPrintDebugInformation() {
        System.out.println("--[System Debug Information]--");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("LWJGL Version: " + Version.getVersion());
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
        System.out.println("--[End of Debug Information]--");
    }

}
