package com.jazzjack.rab.bit.cmiyc.actor.enemy.route;

import com.jazzjack.rab.bit.cmiyc.actor.Actor;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.Enemy;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyConfig;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyContext;
import com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.Step;
import com.jazzjack.rab.bit.cmiyc.collision.Collidable;
import com.jazzjack.rab.bit.cmiyc.collision.CollisionDetector;
import com.jazzjack.rab.bit.cmiyc.shared.Direction;
import com.jazzjack.rab.bit.cmiyc.shared.Predictability;
import com.jazzjack.rab.bit.cmiyc.shared.Randomizer;
import com.jazzjack.rab.bit.cmiyc.shared.Sense;
import com.jazzjack.rab.bit.cmiyc.shared.position.HasPosition;
import com.jazzjack.rab.bit.cmiyc.shared.position.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static com.jazzjack.rab.bit.cmiyc.actor.enemy.EnemyConfig.enemyConfig;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.CORNER_BOTTOM_LEFT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.CORNER_BOTTOM_RIGHT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.CORNER_TOP_LEFT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.CORNER_TOP_RIGHT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.ENDING_BOTTOM;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.ENDING_LEFT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.ENDING_RIGHT;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.ENDING_TOP;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.HORIZONTAL;
import static com.jazzjack.rab.bit.cmiyc.actor.enemy.route.step.StepNames.VERTICAL;
import static com.jazzjack.rab.bit.cmiyc.collision.CollidableMatcher.matchesCollidable;
import static com.jazzjack.rab.bit.cmiyc.collision.CollisionResult.collision;
import static com.jazzjack.rab.bit.cmiyc.collision.CollisionResult.noCollision;
import static com.jazzjack.rab.bit.cmiyc.collision.PositionMatcher.matchesPosition;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RouteGeneratorTest {

    @InjectMocks
    private RouteGenerator routeGenerator;
    @Mock
    private CollisionDetector collisionDetector;
    @Mock
    private DirectionChanceCalculator directionChanceCalculator;
    @Mock
    private Randomizer randomizer;

    @BeforeEach
    void setup() {
        when(randomizer.randomPercentages(any(Predictability.class), anyInt())).thenAnswer((Answer<List<Integer>>) invocation -> range(0, invocation.getArgument(1)).boxed().collect(toList()));
    }

    @Test
    void expectNoCollidingSteps() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.LEFT),
                aDirectionChance(Direction.DOWN));
        when(collisionDetector.collides(matchesCollidable(2, 1), eq(Direction.RIGHT))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.RIGHT));
        when(collisionDetector.collides(matchesCollidable(1, 2), eq(Direction.UP))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.UP));
        when(collisionDetector.collides(matchesCollidable(0, 1), eq(Direction.LEFT))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.LEFT));
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.DOWN))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(1, 1, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(1)
                .build()));

        assertThat(routes).hasSize(1);
        Route foute = routes.iterator().next();
        assertThat(foute.getSteps()).hasSize(1);
        assertStep(foute.getSteps().iterator().next(), 1, 0, ENDING_BOTTOM);
    }

    @Test
    void expectNoPreviousSteps() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.LEFT),
                aDirectionChance(Direction.UP));
        when(collisionDetector.collides(matchesCollidable(1, 1), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.DOWN))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 0), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 1), eq(Direction.UP))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(1, 1), eq(Direction.LEFT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 2), eq(Direction.UP))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 1, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(5)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(5);
        Iterator<Step> steps = route.getSteps().iterator();
        assertStep(steps.next(), 1, 1, CORNER_TOP_RIGHT);
        assertStep(steps.next(), 1, 0, CORNER_BOTTOM_LEFT);
        assertStep(steps.next(), 2, 0, CORNER_BOTTOM_RIGHT);
        assertStep(steps.next(), 2, 1, VERTICAL);
        assertStep(steps.next(), 2, 2, ENDING_TOP);
    }

    @Test
    void expectPartiallyOverlappedRoutesAllowed() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                // route 1
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                // route 2
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP));
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.RIGHT))).thenReturn(noCollision(), noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 0), eq(Direction.RIGHT))).thenReturn(noCollision(), noCollision());
        when(collisionDetector.collides(matchesCollidable(3, 0), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 1), eq(Direction.UP))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(2)
                .withMaxRouteLength(3)
                .build()));

        assertThat(routes).hasSize(2);
        Iterator<Route> routeIterator = routes.iterator();
        Route route1 = routeIterator.next();
        assertThat(route1.getSteps()).hasSize(3);
        Iterator<Step> stepsRoute1 = route1.getSteps().iterator();
        assertStep(stepsRoute1.next(), 1, 0, HORIZONTAL);
        assertStep(stepsRoute1.next(), 2, 0, HORIZONTAL);
        assertStep(stepsRoute1.next(), 3, 0, ENDING_RIGHT);
        Route route2 = routeIterator.next();
        assertThat(route2.getSteps()).hasSize(3);
        Iterator<Step> stepsroute2 = route2.getSteps().iterator();
        assertStep(stepsroute2.next(), 1, 0, HORIZONTAL);
        assertStep(stepsroute2.next(), 2, 0, CORNER_BOTTOM_RIGHT);
        assertStep(stepsroute2.next(), 2, 1, ENDING_TOP);
    }

    @Test
    void expectDuplicateRoutesFilteredOut() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                // route1
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                // route2
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT));
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.RIGHT))).thenReturn(noCollision(), noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 0), eq(Direction.RIGHT))).thenReturn(noCollision(), noCollision());
        when(collisionDetector.collides(matchesCollidable(3, 0), eq(Direction.RIGHT))).thenReturn(noCollision(), noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(2)
                .withMaxRouteLength(3)
                .build()));

        assertThat(routes).hasSize(1);
    }

    @Test
    void expectRouteThatEndsOnOtherRouteFilteredOut() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                // route1
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.DOWN),
                // route2
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT));
        // route1
        when(collisionDetector.collides(matchesCollidable(0, 1), eq(Direction.UP))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(1, 1), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.DOWN))).thenReturn(noCollision());
        // route2
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 0), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(3, 0), eq(Direction.RIGHT))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(2)
                .withMaxRouteLength(3)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertStep(route.getSteps().get(0), 1, 0, HORIZONTAL);
        assertStep(route.getSteps().get(1), 2, 0, HORIZONTAL);
        assertStep(route.getSteps().get(2), 3, 0, ENDING_RIGHT);
    }

    @Test
    void expectRouteEndedBecauseNoDirectionsAllowedAnymore() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.LEFT));
        when(collisionDetector.collides(matchesCollidable(1, 1), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(collisionDetector.collides(matchesCollidable(2, 1), eq(Direction.RIGHT))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.RIGHT));
        when(collisionDetector.collides(matchesCollidable(1, 2), eq(Direction.UP))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.UP));
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.DOWN))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.DOWN));
        when(collisionDetector.collides(matchesCollidable(0, 1), eq(Direction.LEFT))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 1, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(2)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(1);
        assertStep(route.getSteps().iterator().next(), 1, 1, ENDING_RIGHT);
    }

    @Test
    void expectAllPossibleStepNames() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.LEFT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.LEFT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.LEFT),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.DOWN));
        when(collisionDetector.collides(any(Collidable.class), any(Direction.class))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(18)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(18);
        Iterator<Step> steps = route.getSteps().iterator();
        assertStep(steps.next(), 1, 0, HORIZONTAL);
        assertStep(steps.next(), 2, 0, CORNER_BOTTOM_RIGHT);
        assertStep(steps.next(), 2, 1, VERTICAL);
        assertStep(steps.next(), 2, 2, CORNER_TOP_RIGHT);
        assertStep(steps.next(), 1, 2, CORNER_BOTTOM_LEFT);
        assertStep(steps.next(), 1, 3, CORNER_TOP_LEFT);
        assertStep(steps.next(), 2, 3, HORIZONTAL);
        assertStep(steps.next(), 3, 3, CORNER_TOP_RIGHT);
        assertStep(steps.next(), 3, 2, CORNER_BOTTOM_LEFT);
        assertStep(steps.next(), 4, 2, HORIZONTAL);
        assertStep(steps.next(), 5, 2, HORIZONTAL);
        assertStep(steps.next(), 6, 2, CORNER_TOP_RIGHT);
        assertStep(steps.next(), 6, 1, VERTICAL);
        assertStep(steps.next(), 6, 0, CORNER_BOTTOM_RIGHT);
        assertStep(steps.next(), 5, 0, CORNER_BOTTOM_LEFT);
        assertStep(steps.next(), 5, 1, CORNER_TOP_RIGHT);
        assertStep(steps.next(), 4, 1, CORNER_TOP_LEFT);
        assertStep(steps.next(), 4, 0, ENDING_BOTTOM);
    }

    @Test
    void expectEndingRight() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(aDirectionChance(Direction.RIGHT));
        when(collisionDetector.collides(any(Collidable.class), any(Direction.class))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(1)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(1);
        assertStep(route.getSteps().iterator().next(), 1, 0, ENDING_RIGHT);
    }

    @Test
    void expectEndingLeft() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(aDirectionChance(Direction.LEFT));
        when(collisionDetector.collides(any(Collidable.class), any(Direction.class))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(1, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(1)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(1);
        assertStep(route.getSteps().iterator().next(), 0, 0, ENDING_LEFT);
    }

    @Test
    void expectEndingTop() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(aDirectionChance(Direction.UP));
        when(collisionDetector.collides(any(Collidable.class), any(Direction.class))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(1)
                .build()));

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(1);
        assertStep(route.getSteps().iterator().next(), 0, 1, ENDING_TOP);
    }

    @Test
    void expectDirectionChanceCalculatorAndRandomizerToBeCalledWithCorrectParameters() {
        Enemy enemy = enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(2)
                .build());
        List<DirectionChance> directionChances = emptyList();
        when(directionChanceCalculator.calculate(matchesPosition(enemy), eq(Direction.valuesAsSet()), eq(enemy.getConfig().getSense()))).thenReturn(directionChances);
        when(randomizer.chooseRandomChance(directionChances)).thenReturn(aDirectionChance(Direction.RIGHT));
        when(collisionDetector.collides(matchesCollidable(1, 0), eq(Direction.RIGHT))).thenReturn(noCollision());
        when(directionChanceCalculator.calculate(matchesPosition(1, 0), eq(new HashSet<>(asList(Direction.RIGHT, Direction.UP, Direction.DOWN))), eq(enemy.getConfig().getSense()))).thenReturn(directionChances);
        when(randomizer.chooseRandomChance(directionChances)).thenReturn(aDirectionChance(Direction.UP));
        when(collisionDetector.collides(matchesCollidable(1, 1), eq(Direction.UP))).thenReturn(collision(mock(Collidable.class), mock(Collidable.class), Direction.UP));
        when(directionChanceCalculator.calculate(matchesPosition(1, 1), eq(new HashSet<>(asList(Direction.RIGHT, Direction.DOWN))), eq(enemy.getConfig().getSense()))).thenReturn(directionChances);
        when(randomizer.chooseRandomChance(directionChances)).thenReturn(aDirectionChance(Direction.RIGHT));
        when(collisionDetector.collides(matchesCollidable(2, 0), eq(Direction.RIGHT))).thenReturn(noCollision());

        List<Route> routes = routeGenerator.generateRoutes(enemy);

        assertThat(routes).hasSize(1);
        Route route = routes.iterator().next();
        assertThat(route.getSteps()).hasSize(2);
        Iterator<Step> steps = route.getSteps().iterator();
        assertStep(steps.next(), 1, 0, HORIZONTAL);
        assertStep(steps.next(), 2, 0, ENDING_RIGHT);
    }

    @Test
    void expectEmptyRoutesToBeFilteredOut() {
        when(directionChanceCalculator.calculate(any(HasPosition.class), anySet(), any(Sense.class))).thenReturn(emptyList());
        when(randomizer.chooseRandomChance(anyList())).thenReturn(
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.RIGHT),
                aDirectionChance(Direction.UP),
                aDirectionChance(Direction.DOWN),
                aDirectionChance(Direction.LEFT));
        when(collisionDetector.collides(any(Collidable.class), any(Direction.class))).thenReturn(
                noCollision(),
                collision(mock(Collidable.class), mock(Collidable.class), Direction.RIGHT),
                collision(mock(Collidable.class), mock(Collidable.class), Direction.UP),
                collision(mock(Collidable.class), mock(Collidable.class), Direction.DOWN));

        List<Route> routes = routeGenerator.generateRoutes(enemy(0, 0, enemyConfig("enemy")
                .withNumberOfRoutesToGenerate(1)
                .withMaxRouteLength(1)
                .build()));

        assertThat(routes).hasSize(1);
    }

    private void assertStep(Actor step, int x, int y, String name) {
        assertThat(step.getX()).isEqualTo(x);
        assertThat(step.getY()).isEqualTo(y);
        assertThat(step.getName()).isEqualTo(name);
    }

    private DirectionChance aDirectionChance(Direction direction) {
        return new DirectionChance(direction, 0);
    }

    private Enemy enemy(int startX, int startY, EnemyConfig config) {
        return new Enemy(mock(EnemyContext.class), config, new Position(startX, startY));
    }

}