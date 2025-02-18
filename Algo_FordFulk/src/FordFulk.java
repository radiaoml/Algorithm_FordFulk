import java.util.*;

public class FordFulk {
    // Function to perform BFS and check if there is a path from source to sink
    private boolean bfs(int[][] graph, int[] parent, int source, int sink) {
        boolean[] visited = new boolean[graph.length];
        visited[source] = true;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < graph.length; v++) {
                if (!visited[v] && graph[u][v] > 0) { // Check if there's a residual capacity
                    queue.add(v);
                    visited[v] = true;
                    parent[v] = u;

                    if (v == sink) return true;
                }
            }
        }
        return false;
    }

    // Ford-Fulkerson function
    public int fordFulkerson(int[][] graph, int source, int sink) {
        int[][] residualGraph = new int[graph.length][graph.length];
        for (int u = 0; u < graph.length; u++) {
            for (int v = 0; v < graph.length; v++) {
                residualGraph[u][v] = graph[u][v];
            }
        }

        int[] parent = new int[graph.length];
        int maxFlow = 0;

        // Augment the flow while there is a path from source to sink
        while (bfs(residualGraph, parent, source, sink)) {
            int pathFlow = Integer.MAX_VALUE;

            // Find the maximum flow through the path found by BFS
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            // Update residual capacities of edges and reverse edges along the path
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }
        return maxFlow;
    }
}