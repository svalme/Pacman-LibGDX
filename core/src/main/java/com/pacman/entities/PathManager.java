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
    public void updatePath(Vector2 position, Vector2 target) {
        if (!target.equals(lastTarget)) {
            System.out.println("Updating path");
            lastTarget = target;
            System.out.println("Position: " + position);
            System.out.println("Target: " + target);
            path = pathfinding.aStarPathfinding(position, target, map);
            System.out.println("Path: " + path);
        }
    }

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public Vector2 getNextMove() {

        return (path != null && path.size() > 1) ? path.get(1) : null;
    }

    public boolean isPathEmpty() {
        if (path.size() == 1) { // we must have already sent this move through
            return false;
        }
        return true;
    }

    // make sure we don't do the next step in the path unless we've fully crossed to this tile
    public Vector2 moveTowardsTarget(Vector2 position, float deltaTime) {
        System.out.println("Path (moveTowardsTarget: " + path);

        if (path != null && !path.isEmpty()) {
            Vector2 nextMove = path.get(1);
            Vector2 direction = nextMove.cpy().sub(position).nor();
            position.add(direction.scl(100f * deltaTime));

            // Snap to grid when close enough
            //if (position.epsilonEquals(nextMove, 0.1f)) {
                System.out.println("position epsilon equals next move, 0.1f");
                path.remove(0);  // Move to next step in path
                position.set(nextMove);
                return nextMove;
            //}
        }
        return null;
    }
}

/*

public void moveTowardsTarget(Vector2 position, float deltaTime) {
    if (path != null && path.size() > 1) {
        Vector2 nextMove = path.get(1);

        // Check if nextMove is walkable before moving
        if (isValidTarget(nextMove)) {
            Vector2 direction = nextMove.cpy().sub(position).nor();
            position.add(direction.scl(100f * deltaTime));

            if (position.dst2(nextMove) < 0.01f) { // Snap to tile
                position.set(nextMove);
            }
        }
    }
}
 */
