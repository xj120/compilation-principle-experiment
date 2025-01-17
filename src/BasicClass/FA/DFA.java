package BasicClass.FA;

import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.Set;

public class DFA {
    private Dstate start; //标记开始状态
    private ArrayList<Dstate> accept = new ArrayList<>(); //标记接收状态
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

    public boolean contains(Dstate U) { //判断当前DFA是否包含状态U
        for (Dstate ds : D_states) {
            if (ds.equals(U)) {
                return true;
            }
        }
        return false;
    }

    public void showDFATable() { //打印转换表
        System.out.print("NFA states\t\tDFA state");
        ArrayList<Character> input = getInputSymbol();
        for (Character a : input) {
            System.out.print("\t" + a);
        }
        System.out.println("\r");
        Set<Dstate> dsSet = this.transitTable.vertexSet();
        for (Dstate ds : dsSet) {
            ArrayList<State> ns = ds.getNfa_state();
            StringBuilder temp = new StringBuilder();
            for (State s : ns) {
                temp.append(s.getId()).append(",");
            }
            System.out.print(temp + "\t" + ds.getId());
            Set<RelationshipEdge> edgeSet = this.transitTable.edgesOf(ds);
            for (Character character : input) {
                for (RelationshipEdge e : edgeSet) {
                    if (this.transitTable.getEdgeSource(e) == ds) {
                        if (e.getLabel() == character) {
                            System.out.print("\t" + this.transitTable.getEdgeTarget(e).getId());
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
