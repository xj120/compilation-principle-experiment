package Tools;

import BasicClass.FA.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

public class SubsetConstructor {
    public static DFA toDFA(NFA nfa) {
        State.STATE_ID = 0;
        DFA dfa = new DFA();

        ArrayList<State> s0 = new ArrayList<>();
        s0.add(nfa.getStartState());
        Dstate start = closure(s0, nfa);
        dfa.setStart(start);
        dfa.getD_states().add(start);

        while (true) {
            Dstate T = getUnmarkedIn(dfa.getD_states());
            if (T.getId() == -1) {  //返回-1表示没有未标记的项
                break;
            } else {
                T.setMark(true);
                for (each inputSymbol a) {
                    Dstate U = closure(move(T, a), nfa);
                    if (!dfa.contains(U)) {
                        dfa.getD_states().add(U);
                    }
                    dfa.getTransitTable().addVertex(T);
                    dfa.getTransitTable().addVertex(U);
                    dfa.getTransitTable().addEdge(T, U, a);
                }
            }
        }

        return dfa;
    }

    public static Dstate closure(ArrayList<State> s, NFA nfa) {
        Dstate result = new Dstate();
        Stack<State> stack = new Stack<>();
        for (State state : s) {
            stack.push(state);
            result.getNfa_state().add(state);
        }

        while (!stack.empty()) {
            State t = stack.pop();
            Set<RelationshipEdge> set = nfa.getTransitTable().edgesOf(t);
            for (RelationshipEdge e : set) {
                if (e.getLabel() == 'ε') {
                    State u = nfa.getTransitTable().getEdgeTarget(e);
                    if (!result.contains(u)) {
                        result.getNfa_state().add(u);
                        stack.push(u);
                    }
                }
            }
        }
        return result;
    }

    public static ArrayList<State> move(Dstate T, inputSymbol a) {

    }

    public static Dstate getUnmarkedIn(ArrayList<Dstate> dstates) {
        for (Dstate ds : dstates) {
            if (!ds.isMark()) {
                return ds;
            }
        }
        Dstate temp = new Dstate();
        Dstate.D_STATE_ID--;
        temp.setId(-1);
        return temp;
    }
}
