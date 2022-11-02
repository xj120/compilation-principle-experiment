package Tools;

import BasicClass.FA.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

public class SubsetConstructor {
    public static DFA toDFA(NFA nfa) {  //将NFA转换为DFA的函数
        ArrayList<Character> inputSymbol = new ArrayList<>();
        Set<RelationshipEdge> symbolSet = nfa.getTransitTable().edgeSet();
        for (RelationshipEdge ne : symbolSet) {  // 获取当前DFA的输入符号
            if (ne.getLabel() != 'ε') {
                if (!inputSymbol.contains(ne.getLabel())) {
                    inputSymbol.add(ne.getLabel());
                }
            }
        }
        DFA dfa = new DFA(); //要返回的DFA

        ArrayList<State> s0 = new ArrayList<>(); //将NFA的开始状态的闭包初始化为DFA的start
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
                dfa.getTransitTable().addVertex(T);
                for (char a : inputSymbol) { //对于每个输入符号
                    Dstate U = closure(move(T, a, nfa), nfa);
                    if (!dfa.contains(U)) { //DFA未包含U的情况
                        dfa.getD_states().add(U);
                        dfa.getTransitTable().addVertex(U);
                        dfa.getTransitTable().addEdge(T, U, new RelationshipEdge(a));
                    } else {
                        for (Dstate ds: dfa.getD_states()) { //找出DFA中和U相同的那个状态
                            if (ds.equals(U)) {
                                dfa.getTransitTable().addEdge(T, ds, new RelationshipEdge(a));
                            }
                        }
                    }
                }
            }
        }
        //统计接收状态有哪些
        State acc = nfa.getAcceptState();
        ArrayList<Dstate> accSet = new ArrayList<>();
        for (Dstate ds : dfa.getD_states()) {
            if (ds.getNfa_state().contains(acc)) {
                accSet.add(ds);
            }
        }
        dfa.setAccept(accSet);

        return dfa;
    }

    public static Dstate closure(ArrayList<State> s, NFA nfa) { //求ε 闭包的函数
        Dstate result = new Dstate();
        Stack<State> stack = new Stack<>();
        for (State state : s) { //初始化栈
            stack.push(state);
            result.getNfa_state().add(state);
        }
        while (!stack.empty()) { //栈不为空时
            State t = stack.pop();
            Set<RelationshipEdge> set = nfa.getTransitTable().edgesOf(t);
            for (RelationshipEdge e : set) { //遍历相连的每条边
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

    public static ArrayList<State> move(Dstate T, char a, NFA nfa) { //求从状态集T出发经过符号a能到达的状态集
        ArrayList<State> states = new ArrayList<>();
        ArrayList<State> s = T.getNfa_state();
        for (State t : s) { //遍历T中的状态
            Set<RelationshipEdge> set = nfa.getTransitTable().edgesOf(t);
            for (RelationshipEdge e : set) { //遍历和当前这个状态相连的每条边
                if (e.getLabel() == a) { //筛选出符号为a的边
                    State u = nfa.getTransitTable().getEdgeTarget(e);
                    if (!states.contains(u)) { //判断u是否已经添加
                        states.add(u);
                    }
                }
            }
        }
        return states;
    }

    public static Dstate getUnmarkedIn(ArrayList<Dstate> dstates) { //获取DFA中未标记的状态
        for (Dstate ds : dstates) { //遍历每个状态，查看mark
            if (!ds.isMark()) {
                return ds;
            }
        }
        Dstate temp = new Dstate(); //到这里说明无未标记状态，返回一个无意义的Dstate表示
        Dstate.D_STATE_ID--;
        temp.setId(-1);
        return temp;
    }
}
