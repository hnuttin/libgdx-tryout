package com.jazzjack.rab.bit.cmiyc.item;

import com.jazzjack.rab.bit.cmiyc.actor.player.Player;
import com.jazzjack.rab.bit.cmiyc.actor.player.PlayerMovedEvent;
import com.jazzjack.rab.bit.cmiyc.actor.player.PlayerMovedSubscriber;
import com.jazzjack.rab.bit.cmiyc.event.GameEventBus;
import com.jazzjack.rab.bit.cmiyc.level.meta.MarkerObject;
import com.jazzjack.rab.bit.cmiyc.shared.position.HasPosition;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemPickupHandler implements PlayerMovedSubscriber {

    private final List<Handler> itemHandlers;

    public ItemPickupHandler(List<MarkerObject> items) {
        itemHandlers = items.stream().map(Handler::new).collect(toList());
        GameEventBus.registerSubscriber(this);
    }

    private void handleItemPickup(Player player) {
        itemHandlers.stream()
                .filter(handler -> handler.hasSamePositionAs(player))
                .findFirst()
                .ifPresent(handler -> handler.handle(player));
    }

    @Override
    public void playerMoved(PlayerMovedEvent event) {
        handleItemPickup(event.getPlayer());
    }

    private static class Handler implements HasPosition {

        private final MarkerObject markerObject;

        private Handler(MarkerObject markerObject) {
            this.markerObject = markerObject;
        }

        private void handle(Player player) {
            player.incrementHp();
        }

        @Override
        public int getX() {
            return markerObject.getX();
        }

        @Override
        public int getY() {
            return markerObject.getY();
        }
    }
}
