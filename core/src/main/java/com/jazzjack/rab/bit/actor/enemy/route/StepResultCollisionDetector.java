package com.jazzjack.rab.bit.actor.enemy.route;

import com.jazzjack.rab.bit.collision.Collidable;
import com.jazzjack.rab.bit.collision.CollisionDetector;
import com.jazzjack.rab.bit.collision.CollisionResult;

import java.util.ArrayList;
import java.util.List;

class StepResultCollisionDetector implements CollisionDetector {

    private final CollisionDetector collisionDetector;
    private final List<StepResult> stepResults;

    StepResultCollisionDetector(CollisionDetector collisionDetector) {
        this.collisionDetector = collisionDetector;
        stepResults = new ArrayList<>();
    }

    @Override
    public CollisionResult collides(Collidable collidable) {
        CollisionResult collisionResult = collidesWithStepResults(collidable);
        if (collisionResult.isCollision()) {
            return collisionResult;
        } else {
            return collisionDetector.collides(collidable);
        }
    }

    private CollisionResult collidesWithStepResults(Collidable collidable) {
        return stepResults.stream()
                .filter(collidable::collidesWith)
                .findFirst()
                .map(CollisionResult::collision)
                .orElse(CollisionResult.noCollision());
    }

    void addStepResult(StepResult stepResult) {
        stepResults.add(stepResult);
    }

}
