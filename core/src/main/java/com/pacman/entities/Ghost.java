package com.pacman.entities;

import com.pacman.entities.PathManager;
import static com.pacman.entities.GhostState.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Ghost {
    private static final int TILE_SIZE = 24;

    private Vector2 position;
    private Vector2 target;
    private Vector2 nextMove;
    private Vector2 scatterTarget;
    private Vector2 homeBase;

    private GhostState state;

    private PathManager pathManager;
    private int[][] map;

    private Texture normalTexture;
    private Texture frightenedTexture;
    private Texture eatenTexture;

    public Ghost(Vector2 position, Texture texture, int[][] map) {
        this.position = position;
        this.state = GhostState.SCATTER;  // Default state
        this.map = map;
        this.pathManager = new PathManager(map);

        this.scatterTarget = new Vector2(12, 15); // Example scatter corner
        this.homeBase = new Vector2(14, 15); // Example home position
        this.nextMove = homeBase;

        this.normalTexture = texture;
       // loadTextures();
    }
/*
    private void loadTextures() {
        frightenedTexture = new Texture("ghost_frightened.png");
        eatenTexture = new Texture("ghost_eaten.png");
    }
*/

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public void updateTarget(Vector2 pacmanPosition, float deltaTime) {
        switch (state) {
            case CHASE:
                this.target = pacmanPosition;
                break;
            case SCATTER:
                this.target = scatterTarget; // getScatterTarget() per ghost
                System.out.println("SCATTER state");
                break;
            case FRIGHTENED:
                this.target = getRandomTarget();
                break;
            case EATEN:
                this.target = homeBase; // getHomeBase() per ghost
                break;
            default:
                this.target = pacmanPosition;
        }
        System.out.println("Ghost Target: " + target);
        pathManager.updatePath(position, target);
        nextMove = pathManager.moveTowardsTarget(position, deltaTime);
        System.out.println("Position: " + position);

        if (nextMove == null || reachedTarget()) {
            target = getRandomTarget();
            //nextMove = getRandomValidMove();
        } else {
            position = nextMove;
        }

    }

    public void updatePathAndMove(float deltaTime) {
        if (state == GhostState.CHASE || state == GhostState.SCATTER) {
            pathManager.updatePath(position, target); // Use A*
            //nextMove = pathManager.getNextMove();
            nextMove = pathManager.moveTowardsTarget(position, deltaTime);
            if (nextMove == null || reachedTarget()) {
                target = getRandomTarget();
                //nextMove = getRandomValidMove();
            } else {
                position = nextMove;
            }
        } else if (state == GhostState.FRIGHTENED) {
            position.add(nextMove.cpy().scl(deltaTime * SPEED));
        } else if (state == GhostState.EATEN) {
            position = moveDirectlyTo(homeBase, deltaTime);
        }
    }


    // for use with getRandomTarget()
    private boolean isValidTarget(Vector2 target) {
        int tileX = (int) target.x;
        int tileY = (int) target.y;

        // Check if the tile is within map bounds and walkable
        return tileX >= 0 && tileX < map.length &&
            tileY >= 0 && tileY < map[0].length &&
            (map[tileX][tileY] == 0 || map[tileX][tileY] == 26 || map[tileX][tileY] == 27);  // 0: EMPTY, 26: PELLET_LARGE, 27: PELLET_SMALL
    }

    private Vector2 getRandomTarget() {
        Random rand = new Random();
        Vector2 randomTarget;
        do {
            int x = rand.nextInt(map.length);
            int y = rand.nextInt(map[0].length);
            randomTarget = new Vector2(x, y);
        } while (!isValidTarget(randomTarget)); // Keep picking until it's valid

        return randomTarget;
    }

    public void move(float deltaTime) {
        //pathManager.moveTowardsTarget(position, nextMove, deltaTime);
    }

    public void render(SpriteBatch batch) {
        Texture textureToUse =// (state == GhostState.FRIGHTENED) ? frightenedTexture :
                               // (state == GhostState.EATEN) ? eatenTexture :
                                normalTexture;
        batch.draw(textureToUse, position.x * TILE_SIZE, position.y * TILE_SIZE);
    }

    public void setState(GhostState newState) {
        this.state = newState;
        //ghostTarget.onStateChanged();
    }

    public GhostState getState() {
        return this.state;
    }

    public Vector2 getPosition() {
        return position;
    }

}
