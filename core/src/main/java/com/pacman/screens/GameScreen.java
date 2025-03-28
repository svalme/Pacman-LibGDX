package com.pacman.screens;

import com.badlogic.gdx.math.Vector2;
import com.pacman.entities.*;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Map map;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private static final int TILE_SIZE = 24; // Tile size
    private int columns = 28;
    private int rows = 31;
    private OrthographicCamera camera;
    private FitViewport viewport;


    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        int mapWidth = columns * TILE_SIZE;
        int mapHeight = rows * TILE_SIZE;

        // Set up the camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWidth, mapHeight, camera);

        // Center the camera
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();

        batch = new SpriteBatch();
        map = new Map();

        pacman = new Pacman(map);
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(new Vector2(14, 16), new Texture("ghost_orange_soft.png"), map.map));
        for (Ghost ghost : ghosts) {
            ghost.setState(GhostState.SCATTER);
        }

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) pacman.setDirection(Direction.UP);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) pacman.setDirection(Direction.DOWN);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) pacman.setDirection(Direction.LEFT);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) pacman.setDirection(Direction.RIGHT);

        update(delta);

        // Start drawing
        batch.begin();

        map.drawMap(batch);
        pacman.render(batch);


        // Draw ghosts
        for (Ghost ghost : ghosts) {
            ghost.render(batch);
        }

        batch.end();

        // Reset the batch color back to opaque (after rendering)
        batch.setColor(1f, 1f, 1f, 1f);

    }

    public void update(float deltaTime) {
        // Update Pacman's position based on input
        pacman.update(deltaTime);

        // Update ghost positions and behaviors
        for (Ghost ghost : ghosts) {
            ghost.updateTarget(pacman.getPacmanTilePosition(pacman.getCenterX(), pacman.getCenterY(), 15), deltaTime); // Passing Pacman's position for targeting
            // Move ghost along the path
            //ghost.interpolateMove(deltaTime);

        }
/*
        // Check for collisions with the map
        checkCollisionsWithMap();

        // Check for collisions between Pacman and ghosts
        checkCollisionsWithGhosts();*/
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        dispose(); // Clean up resources when this screen is hidden
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
    }
}
