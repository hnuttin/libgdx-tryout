package com.jazzjack.rab.bit.cmiyc.logic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.Enemies;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyMovementCollisionDetector;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyMovementContext;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyRouteCollisionDetector;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.route.RouteGenerator;
import com.jazzjack.rab.bit.cmiyc.actor.player.PlayerMovementCollisionDetector;
import com.jazzjack.rab.bit.cmiyc.animation.AnimationRegister;
import com.jazzjack.rab.bit.cmiyc.collision.LevelCollisionDetectorWithCollidables;
import com.jazzjack.rab.bit.cmiyc.common.Randomizer;
import com.jazzjack.rab.bit.cmiyc.game.GameEventBus;
import com.jazzjack.rab.bit.cmiyc.level.Level;
import com.jazzjack.rab.bit.cmiyc.level.LevelFactory;

public class GameController implements InputProcessor {

    private final AnimationRegister animationRegister;
    private final Randomizer randomizer;
    private final LevelFactory levelFactory;

    private Level currentLevel;
    private Enemies enemies;
    private LevelCollisionDetectorWithCollidables playerMovementColissionDetector;

    private GamePhase currentGamePhase;

    public GameController(LevelFactory levelFactory, AnimationRegister animationRegister, Randomizer randomizer) {
        this.animationRegister = animationRegister;
        this.randomizer = randomizer;
        this.levelFactory = levelFactory;
    }

    public void startGame() {
        startNextLevel();
    }

    private void startNextLevel() {
        startLevel(levelFactory.createNextLevel());
    }

    private void restartLevel() {
        startLevel(levelFactory.createCurrentLevel());
    }

    private void startLevel(Level level) {
        this.currentLevel = level;

        initializeGameObjects(level);

        GameEventBus.publishNewLevelEvent(level);
        startPlayerTurn();
    }

    private void initializeGameObjects(Level level) {
        playerMovementColissionDetector = new PlayerMovementCollisionDetector(level);
        LevelCollisionDetectorWithCollidables enemyMovementColissionDetector = new EnemyMovementCollisionDetector(level);
        EnemyRouteCollisionDetector enemyRouteCollisionDetector = new EnemyRouteCollisionDetector(playerMovementColissionDetector, level.getEnemies());
        RouteGenerator routeGenerator = new RouteGenerator(enemyRouteCollisionDetector, randomizer);
        EnemyMovementContext enemyMovementContext = new EnemyMovementContext(enemyMovementColissionDetector, randomizer, animationRegister);
        enemies = new Enemies(routeGenerator, enemyMovementContext, level.getEnemies());
    }

    private boolean isPlayerTurn() {
        return currentGamePhase == GamePhase.PLAYER_TURN;
    }

    @Override
    public boolean keyDown(int keycode) {
        return isPlayerTurn() && handlePlayerKeys(keycode);
    }

    private boolean handlePlayerKeys(int keycode) {
        if (keycode == Input.Keys.E) {
            endPlayerTurn();
            return true;
        }
        if (movePlayer(keycode)) {
            GameEventBus.publishPlayerMovedEvent();
            if (currentLevel.hasPlayerReachedEnd()) {
                startNextLevel();
            }
            if (!currentLevel.getPlayer().hasActionPointsLeft()) {
                endPlayerTurn();
            }
            return true;
        }
        return false;
    }

    private Boolean movePlayer(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                return currentLevel.getPlayer().moveLeft(playerMovementColissionDetector).success();
            case Input.Keys.RIGHT:
                return currentLevel.getPlayer().moveRight(playerMovementColissionDetector).success();
            case Input.Keys.UP:
                return currentLevel.getPlayer().moveUp(playerMovementColissionDetector).success();
            case Input.Keys.DOWN:
                return currentLevel.getPlayer().moveDown(playerMovementColissionDetector).success();
            default:
                return false;
        }
    }

    private void endPlayerTurn() {
        currentLevel.endTurn();
        if (currentLevel.noTurnsLeft()) {
            restartLevel();
        }
        startEnemyTurn();
    }

    private void startEnemyTurn() {
        currentGamePhase = GamePhase.ENEMY_TURN;
        enemies.moveAllEnemies().thenRun(this::endEnemyTurn);
    }

    private void endEnemyTurn() {
        if (currentLevel.getPlayer().isDead()) {
            restartLevel();
        } else {
            startPlayerTurn();
        }
    }

    private void startPlayerTurn() {
        currentGamePhase = GamePhase.PLAYER_TURN;
        enemies.generateRoutes();
        currentLevel.getPlayer().resetActionPoints();
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
