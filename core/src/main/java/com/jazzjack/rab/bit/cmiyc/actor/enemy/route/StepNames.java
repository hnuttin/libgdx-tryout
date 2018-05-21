package com.jazzjack.rab.bit.cmiyc.actor.enemy.route;

import com.jazzjack.rab.bit.cmiyc.common.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StepNames {

    private StepNames() {
        // never instantiate
    }

    public static final String HORIZONTAL = "route-horizontal";
    public static final String VERTICAL = "route-vertical";

    public static final String ENDING_TOP = "route-ending-top";
    public static final String ENDING_BOTTOM = "route-ending-bottom";
    public static final String ENDING_LEFT = "route-ending-left";
    public static final String ENDING_RIGHT = "route-ending-right";

    public static final String CORNER_TOP_RIGHT = "route-corner-top-right";
    public static final String CORNER_TOP_LEFT = "route-corner-top-left";
    public static final String CORNER_BOTTOM_RIGHT = "route-corner-bottom-right";
    public static final String CORNER_BOTTOM_LEFT = "route-corner-bottom-left";

    private static final EnumMap<Direction, String> DIRECTION_TO_ENDING_MAPPING = new EnumMap<>(Direction.class);
    private static final Map<DirectionAndNextDirection, String> DIRECTION_AND_NEXT_DIRECTION_MAPPING = new HashMap<>();

    static {
        DIRECTION_TO_ENDING_MAPPING.put(Direction.RIGHT, ENDING_RIGHT);
        DIRECTION_TO_ENDING_MAPPING.put(Direction.LEFT, ENDING_LEFT);
        DIRECTION_TO_ENDING_MAPPING.put(Direction.UP, ENDING_TOP);
        DIRECTION_TO_ENDING_MAPPING.put(Direction.DOWN, ENDING_BOTTOM);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.UP, Direction.LEFT), CORNER_TOP_RIGHT);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.RIGHT, Direction.DOWN), CORNER_TOP_RIGHT);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.UP, Direction.RIGHT), CORNER_TOP_LEFT);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.LEFT, Direction.DOWN), CORNER_TOP_LEFT);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.DOWN, Direction.LEFT), CORNER_BOTTOM_RIGHT);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.RIGHT, Direction.UP), CORNER_BOTTOM_RIGHT);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.LEFT, Direction.UP), CORNER_BOTTOM_LEFT);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.DOWN, Direction.RIGHT), CORNER_BOTTOM_LEFT);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.RIGHT, Direction.RIGHT), HORIZONTAL);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.LEFT, Direction.LEFT), HORIZONTAL);

        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.UP, Direction.UP), VERTICAL);
        DIRECTION_AND_NEXT_DIRECTION_MAPPING.put(new DirectionAndNextDirection(Direction.DOWN, Direction.DOWN), VERTICAL);
    }

    public static String getEndingForDirection(Direction direction) {
        return DIRECTION_TO_ENDING_MAPPING.get(direction);
    }

    public static String getBasedOnNextDirection(Direction direction, Direction nextDirection) {
        return DIRECTION_AND_NEXT_DIRECTION_MAPPING.get(new DirectionAndNextDirection(direction, nextDirection));
    }

    private static class DirectionAndNextDirection {

        private final Direction direction;
        private final Direction nextDirection;

        private DirectionAndNextDirection(Direction direction, Direction nextDirection) {
            this.direction = direction;
            this.nextDirection = nextDirection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DirectionAndNextDirection that = (DirectionAndNextDirection) o;

            if (direction != that.direction) return false;
            return nextDirection == that.nextDirection;
        }

        @Override
        public int hashCode() {
            int result = direction.hashCode();
            result = 31 * result + nextDirection.hashCode();
            return result;
        }
    }
}