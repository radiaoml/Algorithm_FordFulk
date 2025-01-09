import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class GraphVisualisation extends Application {

    private BorderPane root;
    private int[][] capacityMatrix;
    private int numNodes;
    private Canvas graphCanvas;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Create buttons
        Button promptButton = createStyledButton("Configure Graph", Color.web("#ADD8E6"));
        Button visualizeButton = createStyledButton("Visualize Graph", Color.web("#90EE90"));
        Button displayMatrixButton = createStyledButton("Show Capacity Matrix", Color.web("#FFDAB9"));
        Button fordFulkersonButton = createStyledButton("Run Ford-Fulkerson", Color.web("#FFB6C1"));
        Button showListButton = createStyledButton("Show List", Color.web("#FFA07A"));

        // Set button actions
        promptButton.setOnAction(e -> promptForNodeCount());
        visualizeButton.setOnAction(e -> visualizeGraph());
        displayMatrixButton.setOnAction(e -> displayCapacityMatrix(primaryStage));
        fordFulkersonButton.setOnAction(e -> runFordFulkerson());
        showListButton.setOnAction(e -> showAdjacencyList());

        // Layout
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30, 50, 30, 50));
        buttonBox.getChildren().addAll(promptButton, visualizeButton, displayMatrixButton, fordFulkersonButton, showListButton);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(buttonBox);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Graph Visualization with Ford-Fulkerson");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + toHexString(color) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 3px; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10;");
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        return button;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void promptForNodeCount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Graph Configuration");
        dialog.setHeaderText("Enter the number of nodes:");
        dialog.setContentText("Number of nodes:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                numNodes = Integer.parseInt(value);
                capacityMatrix = generateCapacityMatrix(numNodes);
                System.out.println("Graph configured with " + numNodes + " nodes.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of nodes.");
            }
        });
    }

    private int[][] generateCapacityMatrix(int n) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matrix[i][j] = (int) (Math.random() * 20) + 1;
            }
        }
        return matrix;
    }

    private void visualizeGraph() {
        if (capacityMatrix == null) {
            System.out.println("Graph not initialized.");
            return;
        }

        Stage graphStage = new Stage();
        graphCanvas = new Canvas(600, 600);

        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        double angleStep = 2 * Math.PI / numNodes;
        double radius = 200;

        double[] xPositions = new double[numNodes];
        double[] yPositions = new double[numNodes];

        for (int i = 0; i < numNodes; i++) {
            xPositions[i] = 300 + radius * Math.cos(i * angleStep);
            yPositions[i] = 300 + radius * Math.sin(i * angleStep);
        }

        for (int i = 0; i < numNodes; i++) {
            gc.setFill(Color.BLUE);
            gc.fillOval(xPositions[i] - 15, yPositions[i] - 15, 30, 30);
            gc.setFill(Color.WHITE);
            gc.fillText(String.valueOf(i), xPositions[i] - 5, yPositions[i] + 5);
        }

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (capacityMatrix[i][j] > 0) {
                    double[] adjustedStart = adjustLine(xPositions[i], yPositions[i], xPositions[j], yPositions[j], 15);
                    double[] adjustedEnd = adjustLine(xPositions[j], yPositions[j], xPositions[i], yPositions[i], 15);

                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(2);
                    gc.strokeLine(adjustedStart[0], adjustedStart[1], adjustedEnd[0], adjustedEnd[1]);
                }
            }
        }

        Scene graphScene = new Scene(new VBox(graphCanvas), 600, 600);
        graphStage.setTitle("Graph Visualization");
        graphStage.setScene(graphScene);
        graphStage.show();
    }

    private double[] adjustLine(double x1, double y1, double x2, double y2, double radius) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double ratio = radius / distance;
        double newX = x1 + dx * ratio;
        double newY = y1 + dy * ratio;
        return new double[]{newX, newY};
    }

    private void displayCapacityMatrix(Stage primaryStage) {
        if (capacityMatrix == null) {
            System.out.println("Capacity matrix not initialized.");
            return;
        }

        System.out.println("Capacity Matrix:");
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                System.out.print(capacityMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void showAdjacencyList() {
        System.out.println("Capacity Matrix (from showAdjacencyList):");
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                System.out.print(capacityMatrix[i][j] + " ");
            }
            System.out.println();
        }

        StringBuilder adjacencyList = new StringBuilder("Adjacency List:\n");
        for (int i = 0; i < numNodes; i++) {
            adjacencyList.append("Node ").append(i).append(" -> ");
            boolean hasConnections = false;

            for (int j = 0; j < numNodes; j++) {
                if (capacityMatrix[i][j] > 0) {
                    adjacencyList.append(String.format("(Node %d, Capacity %d) ", j, capacityMatrix[i][j]));
                    hasConnections = true;
                }
            }

            if (!hasConnections) {
                adjacencyList.append("No connections");
            }

            adjacencyList.append("\n");
        }

        Stage listStage = new Stage();
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(20));
        listBox.setAlignment(Pos.CENTER);

        javafx.scene.control.TextArea listArea = new javafx.scene.control.TextArea(adjacencyList.toString());
        listArea.setEditable(false);
        listArea.setWrapText(true);
        listArea.setPrefSize(400, 300);

        listBox.getChildren().add(listArea);

        Scene listScene = new Scene(listBox, 450, 350);
        listStage.setTitle("Adjacency List");
        listStage.setScene(listScene);
        listStage.show();

        System.out.println(adjacencyList.toString());
    }

    private void runFordFulkerson() {
        if (capacityMatrix == null) {
            System.out.println("Capacity matrix not initialized.");
            return;
        }

        int source = 0;
        int sink = numNodes - 1;

        int[][] originalCapacity = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            System.arraycopy(capacityMatrix[i], 0, originalCapacity[i], 0, numNodes);
        }

        int maxFlow = fordFulkerson(capacityMatrix, source, sink);
        showSaturatedEdges(originalCapacity, capacityMatrix);
        System.out.println("The maximum flow is: " + maxFlow);
    }

    private void showSaturatedEdges(int[][] originalCapacity, int[][] residualCapacity) {
        Stage saturatedStage = new Stage();
        Canvas saturatedCanvas = new Canvas(600, 600);

        GraphicsContext gc = saturatedCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, saturatedCanvas.getWidth(), saturatedCanvas.getHeight());

        double angleStep = 2 * Math.PI / numNodes;
        double radius = 200;

        double[] xPositions = new double[numNodes];
        double[] yPositions = new double[numNodes];

        // Calculate positions of nodes
        for (int i = 0; i < numNodes; i++) {
            xPositions[i] = 300 + radius * Math.cos(i * angleStep);
            yPositions[i] = 300 + radius * Math.sin(i * angleStep);
        }

        // Draw nodes
        for (int i = 0; i < numNodes; i++) {
            gc.setFill(Color.BLUE);
            gc.fillOval(xPositions[i] - 15, yPositions[i] - 15, 30, 30);
            gc.setFill(Color.WHITE);
            gc.fillText(String.valueOf(i), xPositions[i] - 5, yPositions[i] + 5);
        }

        // Draw edges
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (originalCapacity[i][j] > 0) {
                    double[] adjustedStart = adjustLine(xPositions[i], yPositions[i], xPositions[j], yPositions[j], 15);
                    double[] adjustedEnd = adjustLine(xPositions[j], yPositions[j], xPositions[i], yPositions[i], 15);

                    if (residualCapacity[i][j] == 0) {
                        gc.setStroke(Color.RED);
                        gc.setLineWidth(3);
                    } else {
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(2);
                    }

                    gc.strokeLine(adjustedStart[0], adjustedStart[1], adjustedEnd[0], adjustedEnd[1]);
                }
            }
        }

        Scene saturatedScene = new Scene(new VBox(saturatedCanvas), 600, 600);
        saturatedStage.setTitle("Saturated Edges Visualization");
        saturatedStage.setScene(saturatedScene);
        saturatedStage.show();
    }

    private int fordFulkerson(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[] parent = new int[n];
        int maxFlow = 0;

        while (bfs(capacity, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                capacity[u][v] -= pathFlow;
                capacity[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean bfs(int[][] capacity, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[capacity.length];
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < capacity.length; v++) {
                if (!visited[v] && capacity[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;

                    if (v == sink) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
