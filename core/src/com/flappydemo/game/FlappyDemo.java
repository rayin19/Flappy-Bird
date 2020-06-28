package com.flappydemo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.flappydemo.game.states.GameStateManager;
import com.flappydemo.game.states.MenuState;

public class FlappyDemo extends ApplicationAdapter {
	private SpriteBatch batch;


	public static final int WIDTH = 480;
	public static final int HEIGHT = 750;

	public static final String TITLE = "Flappy Bird";

	private GameStateManager gsm;

	private Music music;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();

		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		music.play();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		music.dispose();
		batch.dispose();
	}
}
