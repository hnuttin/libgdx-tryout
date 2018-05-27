package com.jazzjack.rab.bit.cmiyc.collision;

import com.jazzjack.rab.bit.cmiyc.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public abstract class LevelCollisionDetectorWithCollidables extends LevelCollisionDetector {

    private final Set<Collidable> collidables;

    protected LevelCollisionDetectorWithCollidables(Level level) {
        super(level);
        this.collidables = new HashSet<>();
    }

    protected void addCollidable(Collidable... collidables) {
        this.collidables.addAll(asList(collidables));
    }

    protected void addCollidable(List<? extends Collidable> collidables) {
        this.collidables.addAll(collidables);
    }

    @Override
    public CollisionResult collides(Collidable collidable) {
        CollisionResult levelCollisionResult = super.collides(collidable);
        if (levelCollisionResult.isCollision()) {
            return levelCollisionResult;
        } else {
            return collidesWithAnyCollidable(collidable);
        }
    }

    private CollisionResult collidesWithAnyCollidable(final Collidable collidable) {
        return collidables.stream()
                .filter(collidableFromDetector -> collidableFromDetector.willCollideWith(collidable))
                .findFirst()
                .map((Collidable sourceCollidable) -> CollisionResult.unresolved(sourceCollidable, CollisionResult.direction))
                .orElse(CollisionResult.noCollision());
    }

}
