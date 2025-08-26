import java.util.*;

public class ListGraph implements Graph {
    private HashMap<String, LinkedList<String>> nodes = new HashMap<>();

    @Override
    public boolean addNode(String n) {
        if(nodes.containsKey(n)) return false;
        nodes.put(n,new LinkedList<String>());
        return true;
	     
    }

    @Override
    public boolean addEdge(String n1, String n2) {
        if(!nodes.containsKey(n1) || !nodes.containsKey(n2)){
	     throw new NoSuchElementException();
        }
         LinkedList<String> edges = nodes.get(n1);
        if(edges.contains(n2)) return false;
        edges.add(n2);
        return true;
    }

    @Override
    public boolean hasNode(String n) {
	     return nodes.containsKey(n);
    }

    @Override
    public boolean hasEdge(String n1, String n2) {
	     if(!nodes.containsKey(n1)) return false;
         return nodes.get(n1).contains(n2);
    }

    @Override
    public boolean removeNode(String n) {
        if(!nodes.containsKey(n)) return false;
        nodes.remove(n);
        for(LinkedList<String> edges: nodes.values())
        edges.remove(n);
	    return true;
    }

    @Override
    public boolean removeEdge(String n1, String n2) {
        if(!nodes.containsKey(n1) || !nodes.containsKey(n2))
        throw new NoSuchElementException(); 
        return nodes.get(n1).remove(n2);   
    }

    @Override
    public List<String> nodes() {
	     return new ArrayList<>(nodes.keySet());
    }

    @Override
    public List<String> succ(String n) {
	     if(!nodes.containsKey(n)) throw new NoSuchElementException();
         return new ArrayList<>(nodes.get(n));
    }

    @Override
    public List<String> pred(String n) {
        if(!nodes.containsKey(n)) throw new NoSuchElementException();
        List<String> predecessors = new ArrayList<>();
        for(String node: nodes.keySet()){
            if(nodes.get(node).contains(n))
            predecessors.add(node);
        }
        return predecessors;
    }

    @Override
    public Graph union(Graph g) {

        Graph unionGraph = new ListGraph();

        for(String node: this.nodes())
            unionGraph.addNode(node);
        for(String node: g.nodes())
         unionGraph.addNode(node);
        for(String node: this.nodes()){
            for(String succ: this.succ(node))
             unionGraph.addEdge(node, succ);
        }

        for(String node: g.nodes()){
            for(String succ: g.succ(node))
             unionGraph.addEdge(node, succ);
        }
	    return unionGraph;
    }

    
    @Override
    public Graph subGraph(Set<String> nodes) {
        Graph subGraph = new ListGraph();
      for (String node : nodes) {
        if (this.hasNode(node)) {
            subGraph.addNode(node);
        } else {
        }
     }
      for (String node : nodes) {
        if (this.hasNode(node)) {
            for (String succ : this.succ(node)) {
                if (nodes.contains(succ)) {
                    subGraph.addEdge(node, succ);
                }
            }
        }
    }
    return subGraph;
}



    @Override
    public boolean connected(String n1, String n2) {
        if (!nodes.containsKey(n1) || !nodes.containsKey(n2)) 
            throw new NoSuchElementException();
        
            if (n1.equals(n2)) 
                return true; 
            
	    Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(n1);

        while(!queue.isEmpty()){
            String node = queue.poll();
            if(visited.contains(node)) continue;
            visited.add(node);
            for (String successor : nodes.get(node)) {
                if (successor.equals(n2)) 
                    return true; 
                if (!visited.contains(successor)) 
                    queue.add(successor);
            }
        }
        return false;
    }
}
