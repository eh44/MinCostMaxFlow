import java.io.File;
import java.util.*;

public class Graph {
    private int vertexCt;  // Number of vertices in the graph.
    private int[][] capacity;  // Adjacency  matrix
    private int[][] residual; // residual matrix
    private int[][] edgeCost; // cost of edges in the matrix
    private int[] pred;
    private int[] cost;
    private String graphName;  //The file from which the graph was created.
    private int totalFlow; // total achieved flow
    private int source = 0; // start of all paths
    private int sink; // end of all paths

    public Graph(String fileName) {
        this.vertexCt = 0;
        source  = 0;
        this.graphName = "";
        makeGraph(fileName);

    }

    /**
     * Method to add an edge
     *
     * @param source      start of edge
     * @param destination end of edge
     * @param cap         capacity of edge
     * @param weight      weight of edge, if any
     * @return edge created
     */
    private boolean addEdge(int source, int destination, int cap, int weight) {
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        capacity[source][destination] = cap;
        residual[source][destination] = cap;
        edgeCost[source][destination] = weight;
        edgeCost[destination][source] = -weight;
        return true;
    }

    /**
     * Method to get a visual of the graph
     *
     * @return the visual
     */
    public String printMatrix(String label, int[][] m) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n " + label+ " \n     ");
        for (int i=0; i < vertexCt; i++)
            sb.append(String.format("%5d", i));
        sb.append("\n");
        for (int i = 0; i < vertexCt; i++) {
            sb.append(String.format("%5d",i));
            for (int j = 0; j < vertexCt; j++) {
                sb.append(String.format("%5d",m[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method to make the graph
     *
     * @param filename of file containing data
     */
    private void makeGraph(String filename) {
        try {
            graphName = filename;
            System.out.println("\n****Find Flow " + filename);
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            capacity = new int[vertexCt][vertexCt];
            residual = new int[vertexCt][vertexCt];
            edgeCost = new int[vertexCt][vertexCt];
            cost = new int[vertexCt];
            pred = new int[vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                for (int j = 0; j < vertexCt; j++) {
                    capacity[i][j] = 0;
                    residual[i][j] = 0;
                    edgeCost[i][j] = 0;
                }
            }

            // If weights, need to grab them from file
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                int weight = reader.nextInt();
                if (!addEdge(v1, v2, cap, weight))
                    throw new Exception();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sink = vertexCt - 1;
        System.out.println( printMatrix("Edge Cost" ,edgeCost));
    }
    public void findWeightedFlow(){
        System.out.println("Paths found in order");
        Flow(source, sink);
    }
    //The Max Flow algorithm. Takes the source and sink. no return value
    public void Flow(int s, int t){
        int prev;
        while(hasAugmentingCheapestPath(s,t)){
            double availFlow = 500;
        for(int v = t; v!= s; v = prev){
            prev = pred[v];
            availFlow = Math.min(availFlow, residual[prev][v]);
        }
        for(int v = t; v != s; v = prev){
            prev = pred[v];
            residual[prev][v] -= availFlow;
            residual[v][prev] += availFlow;
        }
        totalFlow +=availFlow;
        String path = "";
        int current = t;
        while (current != s) {
            int previous = pred[current];
            path = current + " " + path;
            current = previous;
            }
        path = s + " " + path;
        System.out.println(path + "(" + availFlow + ")" + " $ " + cost[t]);

        }

    }

    //The min cost algorithm. Takes the source and sink and returns a boolean indicating if it has an augmenting cheapest path
    private boolean hasAugmentingCheapestPath(int s, int t) {
        pred = new int[vertexCt];
        for (int i = 0; i < cost.length; i++){
            cost[i] = 500;
        }
        cost[s]=0;
        for (int i = 0; i < vertexCt; i++) {
            for (int u = 0; u < vertexCt; u++) {
                for (int v = 0; v < vertexCt; v++) {
                    if (residual[u][v] != 0) {
                        if (cost[u] + edgeCost[u][v] < cost[v]) {
                            cost[v] = cost[u] + edgeCost[u][v];
                            pred[v] = u;
                        }
                    }
                }
            }
        }
        return pred[t] != 0;
    }
    public void finalEdgeFlow(){
        System.out.println("Final flow on each edge");
        for(int i = 0; i < residual.length-1; i++){
            if (residual[i][0] == 0) {
                for (int j = 0; j < residual[i].length-1; j++) {
                    if (residual[i][j] != 0) {
                        System.out.println("Flow " + j + " -> " + i + "(" + residual[i][j] + ")" + " $ " + edgeCost[j][i]);
                    }
                }
            }
        }
    }



    public void minCostMaxFlow(){
        System.out.println( printMatrix("Capacity", capacity));
        findWeightedFlow();
        System.out.println(printMatrix("Residual", residual));
        finalEdgeFlow();
    }

    public static void main(String[] args) {
        String[] files = {"match0.txt", "match1.txt", "match2.txt", "match3.txt", "match4.txt", "match5.txt","match6.txt", "match7.txt", "match8.txt", "match9.txt"};
        for (String fileName : files) {
            Graph graph = new Graph(fileName);
            graph.minCostMaxFlow();
        }
    }
}