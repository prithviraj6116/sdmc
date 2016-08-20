package g1.a1;
import java.util.List;
import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;

/**
 * This class demonstrates some of the operations that can be performed on
 * directed graphs. After constructing a basic directed graph, it computes all
 * the strongly connected components of this graph. It then finds the shortest
 * path from one vertex to another using Dijkstra's shortest path algorithm.
 * The sample code should help to clarify to users of JGraphT that the class
 * org.jgrapht.alg.DijkstraShortestPath can be used to find shortest paths
 * within directed graphs.
 *
 * @author Minh Van Nguyen
 * @since 2008-01-17
 */
public class DirectedGraphDemo {
    public static void main(String args[]) {
        // constructs a directed graph with the specified vertices and edges
        DirectedGraph<String, DefaultEdge> directedGraph =
            new DefaultDirectedGraph<String, DefaultEdge>
            (DefaultEdge.class);

        directedGraph.addVertex("a");
        directedGraph.addVertex("b");
        directedGraph.addVertex("c");
        directedGraph.addVertex("d");
        directedGraph.addVertex("e");
        directedGraph.addVertex("f");
        directedGraph.addVertex("g");
        directedGraph.addVertex("h");
        directedGraph.addVertex("i");
        
        
        
        directedGraph.addEdge("a", "b");
        directedGraph.addEdge("b", "d");
        directedGraph.addEdge("d", "c");
        directedGraph.addEdge("c", "a");
        directedGraph.addEdge("e", "d");
        directedGraph.addEdge("e", "f");
        directedGraph.addEdge("f", "g");
        directedGraph.addEdge("g", "e");
        directedGraph.addEdge("h", "e");
        directedGraph.addEdge("i", "h");
        System.out.println(directedGraph.vertexSet());
        System.out.println(directedGraph.edgeSet());

        // computes all the strongly connected components of the directed graph
        StrongConnectivityInspector sci =
            new StrongConnectivityInspector(directedGraph);
        List stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();

        // prints the strongly connected components
        System.out.println("Strongly connected components:");
        for (int i = 0; i < stronglyConnectedSubgraphs.size(); i++) {
            System.out.println(stronglyConnectedSubgraphs.get(i));
        }
        System.out.println();

        // Prints the shortest path from vertex i to vertex c. This certainly
        // exists for our particular directed graph.
        System.out.println("Shortest path from i to c:");
        List path =
            DijkstraShortestPath.findPathBetween(directedGraph, "i", "c");
        System.out.println(path + "\n");

        // Prints the shortest path from vertex c to vertex i. This path does
        // NOT exist for our particular directed graph. Hence the path is
        // empty and the variable "path"; must be null.
        System.out.println("Shortest path from c to i:");
        path = DijkstraShortestPath.findPathBetween(directedGraph, "c", "i");
        System.out.println(path);
    }
}
