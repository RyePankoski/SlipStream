package com.Rye.DarknessGame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AStar {

    private static final int CELL_SIZE = 32;
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Diagonal movements
    };

    public static int[][] imageToGrid(String imagePath) throws IOException {
        File file = new File(imagePath);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        int gridWidth = width / CELL_SIZE;
        int gridHeight = height / CELL_SIZE;

        int[][] grid = new int[gridHeight][gridWidth];

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                int centerX = x * CELL_SIZE + CELL_SIZE / 2;
                int centerY = y * CELL_SIZE + CELL_SIZE / 2;
                int invertedY = height - 1 - centerY;
                int pixel = image.getRGB(centerX, invertedY);
                int alpha = (pixel >> 24) & 0xff;
                grid[y][x] = (alpha == 255) ? 255 : 0;
            }
        }
        return grid;
    }

    public static List<int[]> aStar(int[] start, int[] goal, int[][] grid) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(
            Comparator.comparingDouble(node -> node.f * 1.001)
        );

        // More efficient closed set tracking
        boolean[][] closedSet = new boolean[grid.length][grid[0].length];

        Node startNode = new Node(start[0], start[1]);
        Node goalNode = new Node(goal[0], goal[1]);

        startNode.g = 0;
        startNode.f = heuristic(startNode, goalNode);
        openSet.add(startNode);

        //prevents extremely long searches
        int maxIterations = grid.length * grid[0].length;
        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            Node current = openSet.poll();

            if (current.x == goalNode.x && current.y == goalNode.y) {
                return reconstructPath(current);
            }

            closedSet[current.y][current.x] = true;

            for (int[] direction : DIRECTIONS) {
                int nx = current.x + direction[0];
                int ny = current.y + direction[1];

                // Improved boundary and collision checks
                if (nx < 0 || ny < 0 || nx >= grid[0].length || ny >= grid.length
                    || grid[ny][nx] == 255 || closedSet[ny][nx]) {
                    continue;
                }

                Node neighbor = new Node(nx, ny);

                // Differentiate movement costs (diagonal movement more expensive)
                double movementCost = (direction[0] != 0 && direction[1] != 0) ? 1.414 : 1.0;
                double tentativeG = current.g + movementCost;

                // More relaxed condition for adding to open set
                if (tentativeG < neighbor.g || !openSet.contains(neighbor)) {
                    neighbor.g = tentativeG;
                    neighbor.f = neighbor.g + heuristic(neighbor, goalNode);
                    neighbor.parent = current;

                    // Remove and re-add to maintain priority queue order
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        System.out.println("Path not found or too complex");
        return Collections.emptyList();
    }

    private static List<int[]> reconstructPath(Node current) {
        List<int[]> path = new ArrayList<>();
        while (current != null) {
            path.add(new int[]{
                current.x * CELL_SIZE + CELL_SIZE / 2,
                current.y * CELL_SIZE + CELL_SIZE / 2
            });
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static double heuristic(Node a, Node b) {
        // Diagonal distance heuristic (more accurate than Manhattan)
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return Math.max(dx, dy) + 0.414 * Math.min(dx, dy);
    }

    public static class Node {
        int x, y;
        double g, f;
        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.g = Double.MAX_VALUE;
            this.f = Double.MAX_VALUE;
            this.parent = null;
        }

        // Override equals and hashCode for proper PriorityQueue handling
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
