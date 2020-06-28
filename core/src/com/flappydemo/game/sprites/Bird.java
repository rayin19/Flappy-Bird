package com.flappydemo.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.flappydemo.game.FlappyDemo;

public class Bird {
    private static final int GRAVITY = -15;
    private static final int MOVEMENT = 100;
    private Vector3 position;
    private Vector3 velocity;

    private Animation birdAnimation;
    private Rectangle bounds;

    public boolean colliding;

    private Sound flap;

    public Bird(int x,int y){
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        birdAnimation = new Animation(new Texture("birdanimation.png"), 3, 0.5f);
        bounds = new Rectangle(x , y , birdAnimation.getFrame().getRegionWidth(), birdAnimation.getFrame().getRegionHeight());

        colliding = false;

        flap = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_wing.ogg"));
    }

    public void update(float dt){
        birdAnimation.update(dt);
        if(position.y > 0)
            velocity.add(0,GRAVITY,0);
        //Scaling velocity wrt dt
        velocity.scl(dt);

        //setting position
        if(!colliding)
        position.add(MOVEMENT * dt,velocity.y,0);
        //Downward Limit of Bird
        if(position.y < 0)
            position.y = 0;
        //Upward Limit of Bird since we are zooming in the camera
        if(position.y > (FlappyDemo.HEIGHT/2)-birdAnimation.getFrame().getRegionHeight())
            position.y = (FlappyDemo.HEIGHT/2)-birdAnimation.getFrame().getRegionHeight();

        velocity.scl(1/dt);
        bounds.setPosition(position.x , position.y);
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {
        return birdAnimation.getFrame();
    }

    public void jump(){
        velocity.y = 150;
        flap.play(0.5f);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose(){
        flap.dispose();
    }
}
