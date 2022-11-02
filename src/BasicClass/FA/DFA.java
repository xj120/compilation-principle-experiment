package BasicClass.FA;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.Set;

public class DFA {
    private Dstate start; //标记开始状态
    private ArrayList<Dstate> accept; //标记接收状态
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

    public ArrayList<Dstate> getAccept() {
        return accept;
    }

    public void setAccept(ArrayList<Dstate> accept) {
        this.accept = accept;
    }

    public void showDFA() {  //打印DFA
        System.out.println("DFA Information: ");
        System.out.println("Start State:" + this.start.getId());
        System.out.print("Accept States: ");
        for (Dstate dstate : this.accept) {
            System.out.print(dstate.getId() + ", ");
        }
        System.out.println(" ");
        System.out.println("the transitTable is: \r");
        String edgeInfo;
        for (RelationshipEdge edge : transitTable.edgeSet()) {
            edgeInfo = "(" + this.transitTable.getEdgeSource(edge).getId() + "---" + edge.getLabel() + "-->" + this.transitTable.getEdgeTarget(edge).getId() + ")\r";
            System.out.println(edgeInfo);
        }
        System.out.println("\r");
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

    public void showDFATable() { //打印转换表
        System.out.print("NFA states\t\t\t\t\t\tDFA state");
        ArrayList<Character> input = getInputSymbol();
        for (Character a : input) {
            System.out.print("\t\t" + a);
        }
        System.out.println("\r");
        Set<Dstate> dsSet = this.transitTable.vertexSet();
        for (Dstate ds : dsSet) {
            ArrayList<State> ns = ds.getNfa_state();
            for (State s : ns) {
                System.out.print(s.getId() + ",");
            }
            System.out.print("\t\t\t\t\t\t?" + ds.getId());
            Set<RelationshipEdge> edgeSet = this.transitTable.edgesOf(ds);
            for (Character character : input) {
                for (RelationshipEdge e : edgeSet) {
                    if (this.transitTable.getEdgeTarget(e) != ds) {
                        if (e.getLabel() == character) {
                            System.out.print("\t\t" + character + this.transitTable.getEdgeTarget(e).getId());
                        }
                    }
                }
            }
            System.out.println("\r");
        }
    }

    public ArrayList<Character> getInputSymbol() { // 获取输入符号
        ArrayList<Character> inputSymbol = new ArrayList<>();
        Set<RelationshipEdge> symbolSet = this.getTransitTable().edgeSet();
        for (RelationshipEdge ne : symbolSet) {
            if (!inputSymbol.contains(ne.getLabel())) {
                inputSymbol.add(ne.getLabel());
            }
        }
        return inputSymbol;
    }
}
