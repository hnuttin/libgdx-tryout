package com.jazzjack.rab.bit.cmiyc.actor.player;

public class PlayerProfile {

    private int actionPointsPerTurn;

    private int maxHp;
    private int hp;

    private int sight;

    private PlayerProfile(Builder builder) {
        this.actionPointsPerTurn = builder.actionPointsPerTurn;
        this.maxHp = builder.maxHp;
        this.hp = builder.hp;
        this.sight = builder.sight;
    }

    public int getActionPointsPerTurn() {
        return actionPointsPerTurn;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    void incrementHp() {
        if (hp < maxHp) {
            hp++;
        }
    }

    void damage(int damage) {
        hp = Math.max(0, hp - damage);
    }

    public boolean isDead() {
        return hp == 0;
    }

    public int getSight() {
        return sight;
    }

    public static Builder playerProfileBuilder() {
        return new Builder();
    }

    public static class Builder {

        private int actionPointsPerTurn = 5;
        private int maxHp = 5;
        private int hp = 3;
        private int sight = 2;

        public Builder withActionPointsPerTurn(int actionPointsPerTurn) {
            this.actionPointsPerTurn = actionPointsPerTurn;
            return this;
        }

        public Builder withMaxHp(int maxHp) {
            this.maxHp = maxHp;
            return this;
        }

        public Builder withHp(int hp) {
            this.hp = hp;
            return this;
        }

        public Builder withSight(int sight) {
            this.sight = sight;
            return this;
        }

        public PlayerProfile build() {
            return new PlayerProfile(this);
        }
    }
}
