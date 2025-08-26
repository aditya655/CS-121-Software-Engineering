import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    // Run "java -ea Main" to run with assertions enabled (If you run
    // with assertions disabled, the default, then assert statements
    // will not execute!)
    
	
		public static void main(String[] args) {
			test1();
			test2();
			test3();
			testSelfLoop();
			testMultipleComponents();
			testRemoval();
			testLargeGraph();
			testEdgeGraphAdapter();
		}
	
		public static void test1() {
			Graph g = new ListGraph();
			assert g.addNode("a") : "Failed to add node a";
			assert g.hasNode("a") : "Node a should be present";
		}
	
		public static void test2() {
			// Example test case
		}
	
		public static void test3() {
			// Part 1: Testing ListGraph implementation
			Graph g = new ListGraph();
			assert g.addNode("a") : "Failed to add node a";
			assert g.addNode("b") : "Failed to add node b";
			assert g.addNode("c") : "Failed to add node c"; // Ensure node c is added
			assert g.addEdge("a", "b") : "Failed to add edge a->b";
			assert g.addEdge("a", "c") : "Failed to add edge a->c"; // Ensure edge a->c is added
			assert g.addEdge("c", "b") : "Failed to add edge c->b"; // Ensure edge c->b is added
			System.out.println("Original graph nodes: " + g.nodes());
			System.out.println("Original graph edges: ");
			for (String node : g.nodes()) {
				System.out.println("Successors of " + node + ": " + g.succ(node));
			}
			assert g.hasEdge("a", "b") : "Edge a->b should be present";
			assert g.succ("a").contains("b") : "Node b should be a successor of a";
			assert g.pred("b").contains("a") : "Node a should be a predecessor of b";
			assert g.connected("a", "b") : "a should be connected to b";
			assert !g.connected("b", "a") : "b should not be connected to a";
			assert g.removeEdge("a", "b") : "Failed to remove edge a->b";
			assert !g.hasEdge("a", "b") : "Edge a->b should not be present";
			assert g.connected("a", "b") : "a should be connected to b through c";
	
			// Test for union
			Graph g2 = new ListGraph();
			assert g2.addNode("d") : "Failed to add node d to g2";
			assert g2.addNode("a") : "Failed to add node a to g2";  // Ensure node "a" is added to g2
			assert g2.addEdge("d", "a") : "Failed to add edge d->a in g2";
			Graph unionGraph = g.union(g2);
			System.out.println("Union graph nodes: " + unionGraph.nodes());
			for (String node : unionGraph.nodes()) {
				System.out.println("Union graph successors of " + node + ": " + unionGraph.succ(node));
			}
			assert unionGraph.hasNode("a") : "Union graph should have node a";
			assert unionGraph.hasNode("b") : "Union graph should have node b";
			assert unionGraph.hasNode("c") : "Union graph should have node c";
			assert unionGraph.hasNode("d") : "Union graph should have node d";
			assert unionGraph.hasEdge("d", "a") : "Union graph should have edge d->a";
	
			// Test for subGraph
			Set<String> subGraphNodes = new HashSet<>(Arrays.asList("a", "b", "c"));
			Graph subGraph = g.subGraph(subGraphNodes);
			System.out.println("Subgraph nodes: " + subGraph.nodes());
			for (String node : subGraph.nodes()) {
				System.out.println("Subgraph successors of " + node + ": " + subGraph.succ(node));
			}
			assert subGraph.hasNode("a") : "Subgraph should have node a";
			assert subGraph.hasNode("b") : "Subgraph should have node b";
			assert subGraph.hasNode("c") : "Subgraph should have node c";
			assert subGraph.hasEdge("a", "c") : "Subgraph should have edge a->c";
			assert subGraph.hasEdge("c", "b") : "Subgraph should have edge c->b";
			assert !subGraph.hasNode("d") : "Subgraph should not have node d";
			assert !subGraph.hasEdge("d", "a") : "Subgraph should not have edge d->a";
	
			// Additional tests for succ and pred in subGraph
			assert subGraph.succ("a").contains("c") : "Subgraph: Node c should be a successor of a";
			assert subGraph.pred("b").contains("c") : "Subgraph: Node c should be a predecessor of b";
		}
	
		// Additional Tests
	
		public static void testSelfLoop() {
			Graph g = new ListGraph();
			assert g.addNode("a") : "Failed to add node a";
			assert g.addEdge("a", "a") : "Failed to add self-loop edge a->a";
			assert g.hasEdge("a", "a") : "Self-loop edge a->a should be present";
			assert g.connected("a", "a") : "Node a should be connected to itself";
			System.out.println("Self-loop test passed.");
		}

		public static void testMultipleComponents() {
    Graph g = new ListGraph();
    assert g.addNode("a") : "Failed to add node a";
    assert g.addNode("b") : "Failed to add node b";
    assert g.addNode("c") : "Failed to add node c";
    assert g.addNode("d") : "Failed to add node d";
    assert g.addEdge("a", "b") : "Failed to add edge a->b";
    assert g.addEdge("c", "d") : "Failed to add edge c->d";
    assert g.connected("a", "b") : "a should be connected to b";
    assert g.connected("c", "d") : "c should be connected to d";
    assert !g.connected("a", "c") : "a should not be connected to c";
    assert !g.connected("b", "d") : "b should not be connected to d";
    System.out.println("Multiple components test passed.");
}

public static void testRemoval() {
    Graph g = new ListGraph();
    assert g.addNode("a") : "Failed to add node a";
    assert g.addNode("b") : "Failed to add node b";
    assert g.addEdge("a", "b") : "Failed to add edge a->b";
    assert g.removeNode("a") : "Failed to remove node a";
    assert !g.hasNode("a") : "Node a should be removed";
    assert !g.hasEdge("a", "b") : "Edge a->b should be removed along with node a";
    System.out.println("Node removal test passed.");
}

	
public static void testLargeGraph() {
    Graph g = new ListGraph();
    int nodeCount = 1000;
    for (int i = 1; i <= nodeCount; i++) {
        assert g.addNode("n" + i) : "Failed to add node n" + i;
    }
    for (int i = 1; i < nodeCount; i++) {
        assert g.addEdge("n" + i, "n" + (i + 1)) : "Failed to add edge n" + i + "->n" + (i + 1);
    }
    assert g.connected("n1", "n" + nodeCount) : "n1 should be connected to n" + nodeCount;
    System.out.println("Large graph test passed.");
}

	
public static void testEdgeGraphAdapter() {
    Graph g = new ListGraph();
    assert g.addNode("a") : "Failed to add node a";
    assert g.addNode("b") : "Failed to add node b";
    assert g.addEdge("a", "b") : "Failed to add edge a->b";

    EdgeGraph eg = new EdgeGraphAdapter(g);
    assert eg.addEdge(new Edge("b", "c")) : "Failed to add edge b->c";
    assert eg.hasNode("c") : "Node c should be present";
    assert eg.hasEdge(new Edge("b", "c")) : "Edge b->c should be present";
    List<Edge> edges = eg.edges();
    assert edges.contains(new Edge("a", "b")) : "Edge a->b should be present in EdgeGraph";
    assert edges.contains(new Edge("b", "c")) : "Edge b->c should be present in EdgeGraph";
    System.out.println("EdgeGraphAdapter test passed.");
}

	}