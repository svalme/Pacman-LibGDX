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

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public Vector2 moveTowardsTarget(Vector2 position, float deltaTime) {
        if (isPathEmpty()) return null; // Don't move if no path

        Vector2 nextMove = path.get(0); // Use first item in path
        Vector2 direction = nextMove.cpy().sub(position).nor();
        position.add(direction.scl(100f * deltaTime));

        // Snap to tile if close enough
        if (position.epsilonEquals(nextMove, 0.1f)) {
            position.set(nextMove);
            path.remove(0); // Remove only after reaching the step
        }

        return (!isPathEmpty()) ? path.get(0) : null; // Return next move if available
    }

}

