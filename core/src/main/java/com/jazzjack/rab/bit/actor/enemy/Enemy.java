package com.jazzjack.rab.bit.actor.enemy;

import com.google.common.collect.ImmutableList;
import com.jazzjack.rab.bit.actor.SimpleActor;
import com.jazzjack.rab.bit.actor.enemy.route.AnimationRoute;
import com.jazzjack.rab.bit.actor.enemy.route.Route;
import com.jazzjack.rab.bit.actor.enemy.route.RouteGenerator;
import com.jazzjack.rab.bit.actor.enemy.route.Step;
import com.jazzjack.rab.bit.actor.player.Player;
import com.jazzjack.rab.bit.collision.CollisionDetector;
import com.jazzjack.rab.bit.collision.CollisionResult;
import com.jazzjack.rab.bit.common.Predictability;
import com.jazzjack.rab.bit.common.Randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Enemy extends SimpleActor {

    private final Predictability predictability;
    private final List<Route> routes;

    private final int damageOutput;

    public Enemy(int startX, int startY) {
        super("enemy1", startX, startY);
        this.predictability = Predictability.HIGH;
        this.routes = new ArrayList<>();
        this.damageOutput = 1;
    }

    public Predictability getPredictability() {
        return predictability;
    }

    public ImmutableList<Route> getRoutes() {
        return ImmutableList.copyOf(routes);
    }

    public int getDamageOutput() {
        return damageOutput;
    }

    public void generateRoutes(RouteGenerator routeGenerator) {
        routes.clear();
        routes.addAll(routeGenerator.generateRoutes(this, 2, 4));
    }

    public CompletableFuture<Void> moveAlongRandomRoute(EnemyMovementContext context) {
        if (routes.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        } else {
            AnimationRoute routeToAnimate = new AnimationRoute(chooseRoute(context.getRandomizer()));
            routes.clear();
            routes.add(routeToAnimate);
            EnemyRouteAnimation animation = new EnemyRouteAnimation(context.getCollisionDetector(), this, routeToAnimate);
            return context.getAnimationRegister().registerAnimation(animation);
        }
    }

    private Route chooseRoute(Randomizer randomizer) {
        return randomizer.chooseRandomChance(routes);
    }

    CollisionResult moveToStep(CollisionDetector collisionDetector, Step step) {
        CollisionResult collisionResult = super.moveToDirection(collisionDetector, step.getDirection());
        if (collisionResult.isCollision() && collisionResult.getCollidable() instanceof Player) {
            Player player = (Player) collisionResult.getCollidable();
            player.damangeFromEnemy(this);
        }
        return collisionResult;
    }

    void removeRoute(Route route) {
        routes.remove(route);
    }
}
