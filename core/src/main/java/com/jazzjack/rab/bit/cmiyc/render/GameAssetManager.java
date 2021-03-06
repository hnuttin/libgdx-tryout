package com.jazzjack.rab.bit.cmiyc.render;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.jazzjack.rab.bit.cmiyc.level.LevelTiledMap;
import com.jazzjack.rab.bit.cmiyc.level.LevelTiledMapLoader;

import java.util.HashMap;
import java.util.Map;

public class GameAssetManager extends AssetManager {

    private static final String MAP1 = "maps/cmiyc-1-tutorial.tmx";
    private static final String MAP2 = "maps/cmiyc2.tmx";

    private static final String ATLAS_CMIYC_ACTORS = "atlas/cmiyc_actors.atlas";
    private static final String ATLAS_REGION_PLAYER_END = "end";
    private static final String ATLAS_REGION_HP_FILLED = "hp-filled";
    private static final String ATLAS_REGION_HP_EMPTY = "hp-empty";
    private static final String ATLAS_REGION_AP_START = "ap-ending-left";
    private static final String ATLAS_REGION_AP_MIDDLE = "ap-horizontal";
    private static final String ATLAS_REGION_AP_END = "ap-ending-right";
    private static final String ATLAS_REGION_ENEMY_HOVERED = "enemy-hovered";

    private static final String ATLAS_CMIYC_LIGHTS = "atlas/cmiyc_lights.atlas";
    private static final String ATLAS_REGION_LIGHTS_TILE_VISITED = "tile-visited";
    private static final String ATLAS_REGION_LIGHTS_SIGHT = "sight";

    private static final String ATLAS_CMIYC_FONTS = "atlas/cmiyc_fonts.atlas";
    private static final String FONT_VCR = "fonts/vcr-df.fnt";
    private static final String SHADER_FONT = "shaders/font.vert";

    private static final Map<Integer, String> TURN_ATLAS_REGION_MAPPING = new HashMap<>();

    static {
        TURN_ATLAS_REGION_MAPPING.put(0, "zero");
        TURN_ATLAS_REGION_MAPPING.put(1, "one");
        TURN_ATLAS_REGION_MAPPING.put(2, "two");
        TURN_ATLAS_REGION_MAPPING.put(3, "three");
        TURN_ATLAS_REGION_MAPPING.put(4, "four");
        TURN_ATLAS_REGION_MAPPING.put(5, "five");
        TURN_ATLAS_REGION_MAPPING.put(6, "six");
        TURN_ATLAS_REGION_MAPPING.put(7, "seven");
        TURN_ATLAS_REGION_MAPPING.put(8, "eight");
        TURN_ATLAS_REGION_MAPPING.put(9, "nine");
    }

    public GameAssetManager() {
        this(new InternalFileHandleResolver());
    }

    public GameAssetManager(FileHandleResolver fileHandleResolver) {
        super(fileHandleResolver);
        init();
    }

    private void init() {
        configureLoaders();
        loadMaps();
        loadTextures();
        loadFonts();
        finishLoading();
    }

    private void configureLoaders() {
        setLoader(TiledMap.class, new LevelTiledMapLoader(getFileHandleResolver()));
    }

    private void loadMaps() {
        load(MAP1, TiledMap.class);
        load(MAP2, TiledMap.class);
    }

    private void loadTextures() {
        load(ATLAS_CMIYC_FONTS, TextureAtlas.class);
        load(ATLAS_CMIYC_ACTORS, TextureAtlas.class);
        load(ATLAS_CMIYC_LIGHTS, TextureAtlas.class);
    }

    private void loadFonts() {
        BitmapFontLoader.BitmapFontParameter parameter = new BitmapFontLoader.BitmapFontParameter();
        parameter.atlasName = ATLAS_CMIYC_FONTS;
        load(FONT_VCR, BitmapFont.class, parameter);
        load(SHADER_FONT, ShaderProgram.class);
    }

    public FileHandle getObjectTypesFileHandle() {
        return getFileHandleResolver().resolve("maps/objecttypes.xml");
    }

    public LevelTiledMap getLevelTiledMap1() {
        return (LevelTiledMap) get(MAP1, TiledMap.class);
    }

    public LevelTiledMap getLevelTiledMap2() {
        return (LevelTiledMap) get(MAP2, TiledMap.class);
    }

    public AtlasRegion getSightTexture() {
        return get(ATLAS_CMIYC_LIGHTS, TextureAtlas.class).findRegion(ATLAS_REGION_LIGHTS_SIGHT);
    }

    public AtlasRegion getTileVisitedTexture() {
        return get(ATLAS_CMIYC_LIGHTS, TextureAtlas.class).findRegion(ATLAS_REGION_LIGHTS_TILE_VISITED);
    }

    public AtlasRegion getTextureForName(String name) {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(name);
    }

    public AtlasRegion getHpFilledTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_HP_FILLED);
    }

    public AtlasRegion getHpEmptyTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_HP_EMPTY);
    }

    public AtlasRegion getApStartTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_AP_START);
    }

    public AtlasRegion getApMiddleTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_AP_MIDDLE);
    }

    public AtlasRegion getApEndTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_AP_END);
    }

    public AtlasRegion getPlayerEndTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_PLAYER_END);
    }

    public AtlasRegion getTurnsLeftTexture(int turnsLeft) {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(TURN_ATLAS_REGION_MAPPING.get(turnsLeft));
    }

    public TextureRegion getEnemyHoveredTexture() {
        return get(ATLAS_CMIYC_ACTORS, TextureAtlas.class).findRegion(ATLAS_REGION_ENEMY_HOVERED);
    }

    public BitmapFont getFont() {
        return get(FONT_VCR, BitmapFont.class);
    }

    public ShaderProgram getFontShaderProgram() {
        return get(SHADER_FONT, ShaderProgram.class);
    }
}
