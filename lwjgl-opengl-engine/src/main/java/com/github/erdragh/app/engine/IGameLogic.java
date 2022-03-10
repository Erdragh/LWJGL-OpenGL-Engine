package com.github.erdragh.app.engine;

public interface IGameLogic {
    void init() throws Exception;

    void input(Window window);

    void update(float interval);
    
    void render(Window window);

    void cleanup();
}
