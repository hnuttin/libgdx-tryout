package com.jazzjack.rab.bit.cmiyc.render.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.jazzjack.rab.bit.cmiyc.actor.Actor;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.Enemy;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.route.Route;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.Step;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames;
import com.jazzjack.rab.bit.cmiyc.level.Level;
import com.jazzjack.rab.bit.cmiyc.level.meta.MarkerObject;
import com.jazzjack.rab.bit.cmiyc.render.GameAssetManager;
import com.jazzjack.rab.bit.cmiyc.render.Renderer;
import com.jazzjack.rab.bit.cmiyc.shared.position.HasPosition;

import static com.jazzjack.rab.bit.cmiyc.render.AlphaDrawer.alphaDrawer;
import static com.jazzjack.rab.bit.cmiyc.render.level.TextDrawer.Position.BOTTOM;
import static com.jazzjack.rab.bit.cmiyc.render.level.TextDrawer.Position.TOP;

public class LevelRenderer extends OrthoCachedTiledMapRenderer implements Renderer {

    private static final float ROUTE_ALPHA = 0.7f;
    private static final float LEVEL_CAMERA_SCALE = 1.5f;

    private final Level level;
    private final GameAssetManager assetManager;
    private final LevelCamera camera;
    private final Batch batch;
    private final TextDrawer textDrawer;
    private final FogOfWarRenderer fogOfWarRenderer;

    public LevelRenderer(Level level, GameAssetManager assetManager, int numberOfHorizontalTilesToRender) {
        super(level.getLevelTiledMap(), 1 / level.getLevelTiledMap().getTilePixelSize());
        this.level = level;
        this.assetManager = assetManager;
        this.camera = new LevelCamera(level, numberOfHorizontalTilesToRender, LEVEL_CAMERA_SCALE);
        this.batch = new SpriteBatch();
        this.textDrawer = new TextDrawer(assetManager, this.batch, this.camera);
        this.fogOfWarRenderer = new FogOfWarRenderer(this.level, this.batch, assetManager);
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    @Override
    public void render() {
        camera.update();
        setView(camera);
        batch.setProjectionMatrix(camera.combined);
        renderMap();
    }

    private void renderMap() {
        fogOfWarRenderer.buffer();
        renderLevel();
        batch.begin();
        renderPlayer();
        renderEnemies();
        renderEndPosition();
        renderItems();
        batch.end();
        fogOfWarRenderer.render();
    }

    private void renderLevel() {
        super.render();
    }

    private void renderPlayer() {
        drawActor(level.getPlayer());
    }

    private void renderEnemies() {
        level.getEnemies().forEach(this::drawEnemy);
    }

    private void drawEnemy(Enemy enemy) {
        if (level.getLevelSight().isEnemyInSight(enemy)) {
            drawActor(enemy);
            alphaDrawer(batch)
                    .withAlpha(ROUTE_ALPHA)
                    .draw(() -> drawEnemyRoutes(enemy));
        }
    }

    private void drawEnemyRoutes(Enemy enemy) {
        enemy.getRoutes().forEach(this::drawEnemyRoute);
    }

    private void drawEnemyRoute(Route route) {
        drawPercentage(route);
        route.getSteps().forEach(this::drawActor);
    }

    private void drawPercentage(Route route) {
        Step lastStep = route.getLastStep();
        textDrawer.drawText(
                route.getPercentage() + "%",
                lastStep.getX(),
                lastStep.getY(),
                percentagePositionForStep(lastStep),
                level.getLevelTiledMap().getTilePixelSize());
    }

    private TextDrawer.Position percentagePositionForStep(Step step) {
        return StepNames.ENDING_BOTTOM.equals(step.getName()) ? BOTTOM : TOP;
    }

    private void renderEndPosition() {
        HasPosition endPosition = level.getLevelEndPosition();
        batch.draw(
                assetManager.getPlayerEndTexture(),
                endPosition.getX(),
                endPosition.getY(),
                1,
                1);
    }

    private void renderItems() {
        level.getItems().forEach(this::drawItem);
    }

    private void drawItem(MarkerObject item) {
        batch.draw(
                assetManager.getTextureForName(item.getType()),
                item.getX(),
                item.getY(),
                1,
                1);
    }

    private void drawActor(Actor actor) {
        batch.draw(
                assetManager.getTextureForName(actor.getName()),
                actor.getX(),
                actor.getY(),
                1,
                1);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        fogOfWarRenderer.dispose();
    }
}
