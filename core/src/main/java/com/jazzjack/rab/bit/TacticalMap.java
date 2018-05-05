package com.jazzjack.rab.bit;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

import javax.xml.ws.Dispatch;

public class TacticalMap {

    private static final String MAP_LAYER = "map";

    private final TiledMap tiledMap;

    public TacticalMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public TiledMapTileLayer getMapLayer() {
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            if (MAP_LAYER.equalsIgnoreCase(mapLayer.getName()) && TiledMapTileLayer.class.isAssignableFrom(mapLayer.getClass())) {
                return (TiledMapTileLayer) mapLayer;
            }
        }
        throw new RuntimeException("Could not find map layer");
    }

    public float getTileWidth() {
        return getMapLayer().getTileWidth();
    }

    public float getTileHeight() {
        return getMapLayer().getTileHeight();
    }

}
