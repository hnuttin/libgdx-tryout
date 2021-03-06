package com.jazzjack.rab.bit.cmiyc.actor.enemy.route;

import com.jazzjack.rab.bit.cmiyc.actor.enemy.Enemy;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyAddedEvent;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyAddedEventSubscriber;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyDestroyedEvent;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyDestroyedEventSubscriber;
import com.jazzjack.rab.bit.cmiyc.collision.Collidable;
import com.jazzjack.rab.bit.cmiyc.collision.CollidablesCollisionDetector;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionDetector;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionDetectorCombiner;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionResult;
import com.jazzjack.rab.bit.cmiyc.event.GameEventBus;
import com.jazzjack.rab.bit.cmiyc.level.LevelCollisionDetector;
import com.jazzjack.rab.bit.cmiyc.shared.Direction;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class EnemyRouteCollisionDetector implements CollisionDetector, EnemyAddedEventSubscriber, EnemyDestroyedEventSubscriber {

    private final CollidablesCollisionDetector collisionDetectorWithEnemies;
    private final CollisionDetectorCombiner collisionDetectorCombiner;
    private final List<Enemy> enemies;

    public EnemyRouteCollisionDetector(LevelCollisionDetector levelCollisionDetector) {
        this.collisionDetectorWithEnemies = new CollidablesCollisionDetector();
        this.collisionDetectorCombiner = new CollisionDetectorCombiner(asList(levelCollisionDetector, collisionDetectorWithEnemies));
        this.enemies = new ArrayList<>();
        GameEventBus.registerSubscriber(this);
    }

    @Override
    public CollisionResult collides(Collidable collidable, Direction direction) {
        CollisionResult collisionResult = collidesWithEnemyRoutes(collidable, direction);
        if (collisionResult.isCollision()) {
            return collisionResult;
        } else {
            return collisionDetectorCombiner.collides(collidable, direction);
        }
    }

    private CollisionResult collidesWithEnemyRoutes(Collidable collidable, Direction direction) {
        return enemies.stream()
                .flatMap(enemy -> enemy.getRoutes().stream())
                .flatMap(route -> route.getSteps().stream())
                .filter(step -> step.willCollideWith(collidable))
                .findFirst()
                .map(step -> CollisionResult.collision(collidable, step, direction))
                .orElse(CollisionResult.noCollision());
    }

    @Override
    public void enemyAdded(EnemyAddedEvent event) {
        enemies.add(event.getEnemy());
        collisionDetectorWithEnemies.addCollidable(event.getEnemy());
    }

    @Override
    public void enemyDestroyed(EnemyDestroyedEvent event) {
        enemies.remove(event.getEnemy());
        collisionDetectorWithEnemies.removeCollidable(event.getEnemy());
    }
}
