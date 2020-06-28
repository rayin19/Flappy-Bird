package com.flappydemo.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.flappydemo.game.FlappyDemo;
import com.flappydemo.game.sprites.Bird;
import com.flappydemo.game.sprites.Tube;

public class PlayState extends State {
    //Spacing between two tubes
    //end of first to start of second
    private static final int TUBE_SPACING = 125;
    //Max No of tubes  game will have at any time
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -70;
    private static final String clickToRestartString = "CLICK TO RESTART";
    private static float fontScale = 0.5f;
    private static final int DEFAULT_FRAME_RATE = 30;

    private Preferences preferences;
    private Bird bird;
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1 , groundPos2;

    private Texture gameoverImg;
    private Texture clickToRestart;
    private boolean gameOver;

    private BitmapFont scoreFont;
    private long score;
    private long highScore;
    private long finalScore;

    private Texture finalScorecard;
    private Texture bronzeMedal;
    private Texture silverMedal;
    private Texture goldMedal;
    private Texture platinumMedal;
    private Texture newRecord;

    //Array os tubes
    private Array<Tube> tubes;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50,300);
        bg = new Texture("bg.png");
        ground = new Texture("ground.png");
        gameoverImg = new Texture("gameover.png");
        clickToRestart = new Texture("click_to_restart3.png");

        groundPos1 = new Vector2(cam.position.x- cam.viewportWidth / 2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth /2) + ground.getWidth(),GROUND_Y_OFFSET);

        gameOver = false;

        //Coordinate System from bottom left
        cam.setToOrtho(false, FlappyDemo.WIDTH/2,FlappyDemo.HEIGHT/2);

        tubes = new Array<Tube>();

        for (int i=1;i<=TUBE_COUNT;i++){
            tubes.add(new Tube(i* (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));
        score = 0;
        finalScore = 0;

        finalScorecard = new Texture("scorecard/flappy_bird_scorecard.png");
        bronzeMedal = new Texture("scorecard/flappy_medal_bronze.png");
        silverMedal = new Texture("scorecard/flappy_medal_silver.png");
        goldMedal = new Texture("scorecard/flappy_medal_gold.png");
        platinumMedal = new Texture("scorecard/flappy_medal_platinum.png");
        newRecord = new Texture("scorecard/new_record.png");

        preferences = Gdx.app.getPreferences("scores");
        highScore = preferences.getLong("highscore",0L);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.isTouched()) {
            if(gameOver)
                gsm.set(new PlayState(gsm));
            else
                bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        bird.update(dt);
        //80 is offset value in front of bird
        cam.position.x = bird.getPosition().x + 80;

        for (int i=0;i< tubes.size;i++){
            Tube tube = tubes.get(i);

            if(cam.position.x - (cam.viewportWidth/2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING)*TUBE_COUNT));
            }
            if(!gameOver && bird.getPosition().x > (tube.getPosTopTube().x + Tube.TUBE_WIDTH)
                    && (tube.getPosTopTube().x + Tube.TUBE_WIDTH) >= (cam.position.x - cam.viewportWidth/2 - 80)){
                score++;
                finalScore = (long) Math.floor(score/(12*Gdx.graphics.getFramesPerSecond()/DEFAULT_FRAME_RATE));
                //System.out.println(score);


            }

            if(tube.collides(bird.getBounds())){
                bird.colliding = true;
                gameOver = true;
            }
        }

        if(bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET){
            gameOver = true;
            bird.colliding = true;
        }


        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //Right now Cam.position is cam.viewportWidth/2 and cant be zero as it cam is gonna change its position
            sb.draw(bg,cam.position.x - (cam.viewportWidth)/2,0);
            sb.draw(bird.getTexture(),bird.getPosition().x,bird.getPosition().y);
            for (Tube tube:tubes){
                sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
                sb.draw(tube.getBottomTube(),tube.getPosBottomTube().x,tube.getPosBottomTube().y);
            }
            sb.draw(ground , groundPos1.x , groundPos1.y);
            sb.draw(ground, groundPos2.x, groundPos2.y);
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + finalScore);
            if(!gameOver){
                while(scoreLayout.width > 130 && fontScale > 0.2f){
                    fontScale -= 0.05f;
                    scoreFont.getData().setScale(fontScale);
                }
                if(fontScale <= 0.2f)
                    scoreFont.getData().setScale(0.2f);
                scoreFont.draw(sb,scoreLayout, cam.position.x + cam.viewportWidth/2 - scoreLayout.width - 10,
                        cam.position.y + cam.viewportHeight/2 - 10);
            }
            if(gameOver){
                sb.draw(gameoverImg, cam.position.x - gameoverImg.getWidth() / 2, cam.position.y + cam.viewportHeight/4);
                sb.draw(clickToRestart, cam.position.x - clickToRestart.getWidth() / 4 ,
                        clickToRestart.getHeight() + 10
                        , clickToRestart.getWidth()/2, clickToRestart.getHeight()/2);
                sb.draw(finalScorecard,cam.position.x - cam.viewportWidth/2 + 10 , cam.position.y - cam.viewportHeight/6,
                        finalScorecard.getWidth()/4,finalScorecard.getHeight()/4);
                scoreFont.setColor(Color.RED);
                scoreFont.getData().setScale(fontScale);
                while(scoreLayout.width > 130 && fontScale > 0.2f){
                    fontScale -= 0.05f;
                    scoreFont.getData().setScale(fontScale);
                }
                if(fontScale <= 0.2f)
                    scoreFont.getData().setScale(0.2f);
                if(finalScore >= 100000000000L){
                    String scoreString = "" + finalScore;
                    scoreLayout.setText(scoreFont,scoreString.substring(0,12) + "\n" + scoreString.substring(12));
                }

                scoreFont.draw(sb,scoreLayout, cam.position.x + cam.viewportWidth/3 - scoreLayout.width + 20 ,
                        cam.position.y + 15 );



                if(finalScore>highScore){
                    preferences.putLong("highscore", finalScore);
                    preferences.flush();
                    scoreFont.draw(sb,scoreLayout, cam.position.x + cam.viewportWidth/3 - scoreLayout.width + 20 ,
                            cam.position.y - 27 );
                    sb.draw(newRecord,cam.position.x + 22,cam.position.y - 20, newRecord.getWidth()/4,
                            newRecord.getHeight()/4);
                }else {
                    scoreLayout.setText(scoreFont,"" + highScore);
                    while(scoreLayout.width > 130 && fontScale > 0.2f){
                        fontScale -= 0.05f;
                        scoreFont.getData().setScale(fontScale);
                    }
                    if(fontScale <= 0.2f)
                        scoreFont.getData().setScale(0.2f);
                    if(highScore >= 100000000000L){
                        String scoreString = "" + highScore;
                        scoreLayout.setText(scoreFont,scoreString.substring(0,12) + "\n" + scoreString.substring(12));
                    }
                    scoreFont.draw(sb,scoreLayout, cam.position.x + cam.viewportWidth/3 - scoreLayout.width + 20 ,
                            cam.position.y - 27 );
                }
                if(finalScore >=10 && finalScore < 40)
                    sb.draw(bronzeMedal,cam.position.x - cam.viewportWidth/2 + 33 , cam.position.y - cam.viewportHeight/6 + 28,
                            bronzeMedal.getWidth()/6,bronzeMedal.getHeight()/6);
                if(finalScore >= 40 && finalScore < 100)
                    sb.draw(silverMedal,cam.position.x - cam.viewportWidth/2 + 33 , cam.position.y - cam.viewportHeight/6 + 25,
                            silverMedal.getWidth()/6,silverMedal.getHeight()/6);
                if(finalScore >= 100 && finalScore < 300)
                    sb.draw(goldMedal,cam.position.x - cam.viewportWidth/2 + 32 , cam.position.y - cam.viewportHeight/6 + 23,
                            goldMedal.getWidth()/6,goldMedal.getHeight()/6);
                if(finalScore >= 300)
                    sb.draw(platinumMedal,cam.position.x - cam.viewportWidth/2 + 30 , cam.position.y - cam.viewportHeight/6 + 20,
                            platinumMedal.getWidth()/5,platinumMedal.getHeight()/5);

            }

        sb.end();
    }

    private void updateGround(){
        if (cam.position.x - (cam.viewportWidth/2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth()*2,0);
        if (cam.position.x - (cam.viewportWidth/2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth()*2,0);
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        gameoverImg.dispose();
        clickToRestart.dispose();
        scoreFont.dispose();
        finalScorecard.dispose();
        bronzeMedal.dispose();
        silverMedal.dispose();
        goldMedal.dispose();
        platinumMedal.dispose();
        newRecord.dispose();

        for (Tube tube:tubes)
            tube.dispose();
        System.out.println("Play State Disposed");
    }
}
