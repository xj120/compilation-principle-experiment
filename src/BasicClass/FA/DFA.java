package BasicClass.FA;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;

public class DFA {
    private Dstate start; //标记开始状态
    private Dstate[] accept; //标记接收状态
    private DirectedPseudograph<Dstate, RelationshipEdge> transitTable = new DirectedPseudograph<>(RelationshipEdge.class); //转换表
    private ArrayList<Dstate> D_states = new ArrayList<>(); //状态集合

    public DFA() {
        this.start = new Dstate(StateType.start);
    }

    public Dstate getStart() {
        return start;
    }

    public void setStart(Dstate start) {
        this.start = start;
    }

    public Dstate[] getAccept() {
        return accept;
    }

    public void setAccept(Dstate[] accept) {
        this.accept = accept;
    }

    public DirectedPseudograph<Dstate, RelationshipEdge> getTransitTable() {
        return transitTable;
    }

    public void setTransitTable(DirectedPseudograph<Dstate, RelationshipEdge> transitTable) {
        this.transitTable = transitTable;
    }

    public ArrayList<Dstate> getD_states() {
        return D_states;
    }

    public void setD_states(ArrayList<Dstate> d_states) {
        D_states = d_states;
    }

    public void showDFA() {  //打印NFA  TODO
        System.out.println("Start State:" + this.start.getId());
        //System.out.println("Accept State:" + this.accept.getId());
        System.out.println("the transitTable is: \r");
        String edgeInfo;
        for (RelationshipEdge edge : transitTable.edgeSet()) {
            edgeInfo = "(" + this.transitTable.getEdgeSource(edge).getId() + "---" + edge.getLabel() + "-->" + this.transitTable.getEdgeTarget(edge).getId() + ")\r";
            System.out.println(edgeInfo);
        }
    }

    public void merge(DFA fa) {
        for (RelationshipEdge edge : fa.getTransitTable().edgeSet()) {      //将两个FA放在同一图层中，还未连接
            this.transitTable.addVertex(fa.getTransitTable().getEdgeTarget(edge));
            this.transitTable.addVertex(fa.getTransitTable().getEdgeSource(edge));
            this.transitTable.addEdge(fa.getTransitTable().getEdgeSource(edge), fa.getTransitTable().getEdgeTarget(edge), new RelationshipEdge(edge.getLabel()));
        }
    }

    public boolean contains(Dstate U) {
        for (Dstate ds : D_states) {
            if (ds.equals(U)) {
                return true;
            }
        }
        return false;
    } //判断一个DFA状态是否已经在DFA中
}
