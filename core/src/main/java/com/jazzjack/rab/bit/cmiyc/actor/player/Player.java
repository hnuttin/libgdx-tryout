package com.jazzjack.rab.bit.cmiyc.actor.player;

import com.jazzjack.rab.bit.cmiyc.actor.HasPower;
import com.jazzjack.rab.bit.cmiyc.actor.MovableActor;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.Enemy;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionResult;
import com.jazzjack.rab.bit.cmiyc.shared.Direction;
import com.jazzjack.rab.bit.cmiyc.shared.position.HasPosition;

public class Player extends MovableActor implements HasPower {

    private int actionPointsPerTurn;
    private int actionPointsConsumed;

    private int maxHp;
    private int hp;

    private int sight;
    private int power;

    public Player(ActorContext context, HasPosition hasPosition) {
        super(context, "player", hasPosition);

        this.actionPointsPerTurn = 5;
        this.actionPointsConsumed = 0;

        this.maxHp = 5;
        this.hp = 3;

        this.sight = 5;
        this.power = 1;
    }

    public int getSight() {
        return sight;
    }

    @Override
    public int getPower() {
        return power;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public void damangeFromEnemy(Enemy enemy) {
        hp = Math.max(0, hp - enemy.getPower());
    }

    public boolean isDead() {
        return hp == 0;
    }

    @Override
    public CollisionResult moveToDirection(Direction direction) {
        if (hasActionPointsLeft()) {
            CollisionResult collisionResult = super.moveToDirection(direction);
            if (collisionResult.isResolved()) {
                actionPointsConsumed++;
            }
            return collisionResult;
        } else {
            return CollisionResult.noCollision();
        }
    }

    public int getActionPointsPerTurn() {
        return actionPointsPerTurn;
    }

    public int getActionPointsConsumed() {
        return actionPointsConsumed;
    }

    public boolean hasActionPointsLeft() {
        return actionPointsConsumed < actionPointsPerTurn;
    }

    public void resetActionPoints() {
        actionPointsConsumed = 0;
    }
}
