package com.jazzjack.rab.bit.cmiyc.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.jazzjack.rab.bit.cmiyc.actor.player.PlayerProfile;
import com.jazzjack.rab.bit.cmiyc.animation.AnimationHandler;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionResolver;
import com.jazzjack.rab.bit.cmiyc.level.LevelContext;
import com.jazzjack.rab.bit.cmiyc.level.LevelFactory;
import com.jazzjack.rab.bit.cmiyc.logic.GameController;
import com.jazzjack.rab.bit.cmiyc.render.GameAssetManager;
import com.jazzjack.rab.bit.cmiyc.render.GameRenderer;
import com.jazzjack.rab.bit.cmiyc.shared.RandomInteger;
import com.jazzjack.rab.bit.cmiyc.shared.Randomizer;

import static com.jazzjack.rab.bit.cmiyc.actor.player.PlayerProfile.playerProfileBuilder;

public class Game extends ApplicationAdapter {

    private GameAssetManager assetManager;
    private AnimationHandler animationHandler;
    private GameController gameController;
    private GameRenderer gameRenderer;

    @Override
    public void create() {
        initGameObjects();
        startNewGame();
    }

    private void initGameObjects() {
        assetManager = new GameAssetManager();
        animationHandler = new AnimationHandler();
        PlayerProfile playerProfile = playerProfileBuilder().build();
        gameController = new GameController(new LevelFactory(createLevelContext(playerProfile), assetManager), playerProfile);
        gameRenderer = new GameRenderer(assetManager);
    }

    private LevelContext createLevelContext(PlayerProfile playerProfile) {
        return new LevelContext(new CollisionResolver(), new Randomizer(new RandomInteger()), animationHandler, playerProfile);
    }

    private void startNewGame() {
        Gdx.input.setInputProcessor(gameController);
        gameController.startGame();
    }

    @Override
    public void render() {
        animationHandler.continueAnimations(Gdx.graphics.getDeltaTime());
        gameRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        gameRenderer.resize(width, height);
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        assetManager.dispose();
    }

}
