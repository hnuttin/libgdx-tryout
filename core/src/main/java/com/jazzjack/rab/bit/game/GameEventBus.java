package com.jazzjack.rab.bit.game;

import com.jazzjack.rab.bit.actor.player.PlayerMovedSubscriber;
import com.jazzjack.rab.bit.level.Level;
import com.jazzjack.rab.bit.level.NewLevelSubscriber;

import java.util.ArrayList;
import java.util.List;

public class GameEventBus {

    private final static List<NewLevelSubscriber> NEW_LEVEL_SUBSCRIBERS = new ArrayList<>();
    public static void registerSubscriber(NewLevelSubscriber newLevelSubscriber) {
        NEW_LEVEL_SUBSCRIBERS.add(newLevelSubscriber);
    }
    public static void publishNewLevelEvent(Level newLevel) {
        NEW_LEVEL_SUBSCRIBERS.forEach(listener -> listener.onNewLevel(newLevel));
    }

    private final static List<PlayerMovedSubscriber> PLAYER_MOVED_SUBSCRIBERS = new ArrayList<>();
    public static void registerSubscriber(PlayerMovedSubscriber playerMovedSubscriber) {
        PLAYER_MOVED_SUBSCRIBERS.add(playerMovedSubscriber);
    }
    public static void publishPlayerMovedEvent() {
        PLAYER_MOVED_SUBSCRIBERS.forEach(PlayerMovedSubscriber::playerMoved);
    }
}
