package com.jazzjack.rab.bit.route;

import com.jazzjack.rab.bit.actor.SimpleActor;
import com.jazzjack.rab.bit.common.Direction;

public class Step extends SimpleActor {

    private final Direction direction;

    public Step(String name, StepResult stepResult) {
        super(name, stepResult.getX(), stepResult.getY(), stepResult.getSize());
        this.direction = stepResult.getDirection();
    }

    public Direction getDirection() {
        return direction;
    }
}