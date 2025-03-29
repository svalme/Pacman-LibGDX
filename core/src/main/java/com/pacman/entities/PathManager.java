package com.pacman.entities;

import java.util.List;
import com.badlogic.gdx.math.Vector2;

public class PathManager {
    private Pathfinding pathfinding;
    private int[][] map;
    private List<Vector2> path;
    private Vector2 lastTarget;

    public PathManager(int[][] map) {
        this.map = map;
        this.pathfinding = new Pathfinding(); // will need different paths for different ghosts
        this.lastTarget = null;
    }

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    // get a new path
    public void updatePath(Vector2 position, Vector2 target) {
        if (isPathEmpty() || !target.equals(lastTarget)) {
            lastTarget = target;
            path = pathfinding.aStarPathfinding(position, target, map);
        }
    }


    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public Vector2 getNextMove() {
        return (path != null && path.size() > 1) ? path.get(1) : null;
    }

    public boolean isPathEmpty() {
        return path == null || path.isEmpty();
    }

    public Vector2 moveTowardsTarget(Vector2 position, float deltaTime) {
        if (path != null && !path.isEmpty()) {
            Vector2 nextTile = path.get(0); // Get the next tile in the path (integer coordinates)
            Vector2 direction = nextTile.cpy().sub(position).nor(); // Direction to next tile

            // Move towards the next tile at the appropriate speed
            position.add(direction.scl(deltaTime * 5f));

            // Snap to tile once close enough
            if (position.epsilonEquals(nextTile, 0.1f)) {
                position.set(nextTile); // Snap to exact tile position
                path.remove(0); // Move to the next step in the path
            }

            return position;
        }
        return null;
    }

}

