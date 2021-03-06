package com.jazzjack.rab.bit.cmiyc.level.meta;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.jazzjack.rab.bit.cmiyc.level.InvalidLevelException;
import com.jazzjack.rab.bit.cmiyc.shared.position.HasPosition;
import com.jazzjack.rab.bit.cmiyc.shared.position.Position;

import java.util.Map;

public class MarkerObject implements HasPosition {

    private final MapObject mapObject;
    private final Map<String, String> objectTypeDefaults;
    private final Position position;

    MarkerObject(MapObject mapObject, Map<String, String> objectTypeDefaults, float tilePixelSize) {
        this.mapObject = mapObject;
        this.objectTypeDefaults = objectTypeDefaults;
        this.position = positionForMapObject(mapObject, tilePixelSize);
    }

    private Position positionForMapObject(MapObject mapObject, float tilePixelSize) {
        if (mapObject instanceof EllipseMapObject) {
            EllipseMapObject ellipseMapObject = (EllipseMapObject) mapObject;
            return new Position(
                    (int) (ellipseMapObject.getEllipse().x / tilePixelSize),
                    (int) (ellipseMapObject.getEllipse().y / tilePixelSize));
        } else if (mapObject instanceof RectangleMapObject) {
            RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
            return new Position(
                    (int) (rectangleMapObject.getRectangle().x / tilePixelSize),
                    (int) (rectangleMapObject.getRectangle().y / tilePixelSize));
        } else if (mapObject instanceof PolygonMapObject) {
            PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
            return new Position(
                    (int) (polygonMapObject.getPolygon().getX() / tilePixelSize),
                    (int) (polygonMapObject.getPolygon().getY() / tilePixelSize));
        } else {
            throw new InvalidLevelException("Unsupported marker object " + mapObject.getClass().getSimpleName());
        }
    }

    String getStringProperty(String propertyName) {
        String property = mapObject.getProperties().get(propertyName, String.class);
        if (property == null) {
            String defaultProperty = objectTypeDefaults.get(propertyName);
            if (defaultProperty == null) {
                throw new InvalidLevelException("Unsuppored marker object property: " + propertyName);
            } else {
                return defaultProperty;
            }
        } else {
            return property;
        }
    }

    int getIntProperty(String propertName) {
        String stringProperty = getStringProperty(propertName);
        return Integer.parseInt(stringProperty);
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public int getY() {
        return position.getY();
    }

    protected String getType() {
        return getStringProperty("type");
    }
}
