package com.github.erdragh.app;

import org.lwjgl.Version;

public class App {
    //The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
    }

    public static void main(String[] args) {
        new App().run();
    }
}