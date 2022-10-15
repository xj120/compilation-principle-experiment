package BasicClass.FA;

import org.jgrapht.graph.DirectedPseudograph;

public class FA {
    protected State start;
    protected DirectedPseudograph<State, RelationshipEdge> transitTable = new DirectedPseudograph<>(RelationshipEdge.class);

    public FA() {
        this.start = new State(StateType.start);
    }

    public State getStartState() {
        return start;
    }

    public void setStartState(State startState) {
        this.start = startState;
    }

    public DirectedPseudograph<State, RelationshipEdge> getTransitTable() {
        return this.transitTable;
    }

    public void merge(FA fa) {
        for (RelationshipEdge edge : fa.getTransitTable().edgeSet()) {      //将两个FA放在同一图层中，还未连接
            this.transitTable.addVertex(fa.getTransitTable().getEdgeTarget(edge));
            this.transitTable.addVertex(fa.getTransitTable().getEdgeSource(edge));
            this.transitTable.addEdge(fa.getTransitTable().getEdgeSource(edge), fa.getTransitTable().getEdgeTarget(edge), new RelationshipEdge(edge.getLabel()));
        }
    }
}
