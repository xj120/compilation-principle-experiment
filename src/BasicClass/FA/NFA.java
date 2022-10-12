package BasicClass.FA;

public class NFA extends FA {
    public State accept;

    public NFA() {
        this.start = new State(StateType.start);
        this.accept = new State(StateType.accept);
        transitTable.addVertex(this.start);
        transitTable.addVertex(this.accept);
    }

    public State getAcceptState() {
        return this.accept;
    }

    public void setAcceptState(State accept) {
        this.accept = accept;
    }

    public void showNFA() {
        System.out.println("Start State:" + this.start.getId());
        System.out.println("Accept State:" + this.accept.getId());
        System.out.println("the transitTable is: \r");
        String edgeInfo;
        for (RelationshipEdge edge : transitTable.edgeSet()) {
            edgeInfo = "(" + this.transitTable.getEdgeSource(edge).getId() + "---" + edge.getLabel() + "-->" + this.transitTable.getEdgeTarget(edge).getId() + ")\r";
            System.out.println(edgeInfo);
        }
    }
}
