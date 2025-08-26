import java.util.*;

public class EdgeGraphAdapter implements EdgeGraph {

    private Graph g;

    public EdgeGraphAdapter(Graph g) { this.g = g; }

    @Override
    public boolean addEdge(Edge e) {
	     if(!g.hasNode(e.getSrc()))
        g.addNode(e.getSrc());
        if(!g.hasNode(e.getDst()))
         g.addNode(e.getDst());
        return g.addEdge(e.getSrc(), e.getDst());
    }

    @Override
    public boolean hasNode(String n) {
	     return g.hasNode(n);
    }

    @Override
    public boolean hasEdge(Edge e) {
	     return g.hasEdge(e.getSrc(), e.getDst());
    }

    @Override
    public boolean removeEdge(Edge e) {
      return g.removeEdge(e.getSrc(), e.getDst());
    }

    @Override
    public List<Edge> outEdges(String n) {
        List<Edge> edges = new ArrayList<>();
        for(String succ: g.succ(n))
        edges.add(new Edge(n,succ));
        return edges;
    }

    @Override
    public List<Edge> inEdges(String n) {
      List<Edge> edges = new ArrayList<>();
        for(String pred: g.pred(n))
        edges.add(new Edge(pred,n));
        return edges;
    }

    @Override
    public List<Edge> edges() {
      List<Edge> edges = new ArrayList<>();
      for(String node: g.nodes()){
        for(String succ: g.succ(node))
        edges.add(new Edge(node,succ));
      }
      
      return edges;    
    }

    @Override
    public EdgeGraph union(EdgeGraph g) {
      Graph unionGraph = this.g.union(((EdgeGraphAdapter) g).g);
      return new EdgeGraphAdapter(unionGraph);

    }

    @Override
    public boolean hasPath(List<Edge> e) {
      if(e.isEmpty()) return true;
      for(int i = 0; i < e.size() - 1; i++){
        if(!e.get(i).getDst().equals(e.get(i+1).getSrc()))
          throw new BadPath();
      }
      for(Edge a: e){
        if(!hasEdge(a)) return false;
      }
      return true;
    }

}
