package com.jazzjack.rab.bit.actor;

import com.jazzjack.rab.bit.collision.CollisionDetector;

import java.util.function.Function;

public class Player extends SimpleActor {

    private static final int DEFAULT_SIGHT = 4;

    private int maxNumberOfMoves;
    private int movements;

    private int hp;

    public Player(float startX, float startY) {
        super("player", startX, startY);

        maxNumberOfMoves = 3;
        hp = 3;
    }

    public int getSight() {
        return DEFAULT_SIGHT;
    }

    public int getMovements() {
        return movements;
    }

    public int getHp() {
        return hp;
    }

    public void doDamage(int amount) {
        hp = Math.min(0, hp - amount);
    }

    public boolean isDead() {
        return hp == 0;
    }

    @Override
    public boolean moveRight(CollisionDetector collisionDetector) {
        return doPlayerMove(collisionDetector, super::moveRight);
    }

    @Override
    public boolean moveLeft(CollisionDetector collisionDetector) {
        return doPlayerMove(collisionDetector, super::moveLeft);
    }

    @Override
    public boolean moveUp(CollisionDetector collisionDetector) {
        return doPlayerMove(collisionDetector, super::moveUp);
    }

    @Override
    public boolean moveDown(CollisionDetector collisionDetector) {
        return doPlayerMove(collisionDetector, super::moveDown);
    }

    private boolean doPlayerMove(CollisionDetector collisionDetector, Function<CollisionDetector, Boolean> move) {
        if (hasMovementsLeft() && move.apply(collisionDetector)) {
            movements++;
            return true;
        }
        return false;
    }

    public boolean hasMovementsLeft() {
        return movements < maxNumberOfMoves;
    }

    public void resetMovements() {
        movements = 0;
    }
}
