package com.pacman.entities;

import com.pacman.entities.*;

import com.badlogic.gdx.math.Vector2;
import com.pacman.screens.WallAtlasRegion;


import java.util.*;

class Pathfinding {
    private static final Vector2[] DIRECTIONS = {
        new Vector2(1, 0),  // Right
        new Vector2(-1, 0), // Left
        new Vector2(0, 1),  // Down
        new Vector2(0, -1)  // Up
    };

    public List<Vector2> aStarPathfinding(Vector2 start, Vector2 target, int[][] map) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Vector2, Node> allNodes = new HashMap<>(); // Track nodes by position

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Goal reached
            if (current.position.epsilonEquals(target, 0.1f)) {
                return reconstructPath(current);
            }

            for (Vector2 neighborPos : getNeighbors(current.position, map)) {
                float gCost = current.gCost + 1; // Assuming uniform movement cost

                Node neighbor = allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Float.MAX_VALUE, heuristic(neighborPos, target)));

                if (gCost < neighbor.gCost) { // Found a better path
                    System.out.println("Adding to a path");
                    neighbor.gCost = gCost;
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;

                    allNodes.put(neighborPos, neighbor);
                    openSet.remove(neighbor); // Remove to update priority
                    openSet.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    public List<Vector2> getNeighbors(Vector2 position, int[][] map) {
        List<Vector2> neighbors = new ArrayList<>();
        int x = (int) position.x;
        int y = (int) position.y;

        // Check all 4 possible directions
        for (Vector2 dir : DIRECTIONS) {
            int newX = x + (int)dir.x;
            int newY = y + (int)dir.y;

            if (isValidMove(newX, newY, map)) {
                neighbors.add(new Vector2(newX, newY));
            }
        }

        return neighbors;
    }

    private boolean isValidMove(int x, int y, int[][] map) {
        // Check bounds
        if (x < 0 || y < 0 || x >= map[0].length || y >= map.length) {
            return false;
        }

        // Check if tile is walkable and not already visited
        int tile = map[y][x];
        return tile == WallAtlasRegion.EMPTY.ordinal() || tile == WallAtlasRegion.PELLET_SMALL.ordinal() || tile == WallAtlasRegion.PELLET_LARGE.ordinal();
    }

    private float heuristic(Vector2 a, Vector2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);  // Manhattan Distance
    }

    private List<Vector2> reconstructPath(Node currentNode) {
        List<Vector2> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode.position);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
