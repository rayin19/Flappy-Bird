package com.flappydemo.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public class GameStateManager {
    private Stack<State> states;

    public GameStateManager(){
        states = new Stack<State>();
    }

    public void push(State state){
        states.push(state);
    }
    //Just removing the state from stack and not returning it
    public void pop(){
        states.pop().dispose();
    }

    //Poping and pushing simultaneously
    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    //Update and render methods
    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }
}
