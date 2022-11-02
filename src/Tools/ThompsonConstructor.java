package Tools;

import BasicClass.FA.NFA;
import BasicClass.FA.RelationshipEdge;
import BasicClass.FA.StateType;
import BasicClass.PatternTree.NodeType;
import BasicClass.PatternTree.PatternTreeNode;

public class ThompsonConstructor {
    public static NFA translate(PatternTreeNode node) {  //将RE的语法分析树转化为NFA的函数
        if (node == null) { //传入结点为空时
            System.out.println("语法分析树为空！");
            return null;
        }
        NodeType type = node.getType();  //存放当前结点的类型
        NFA nfa = new NFA();  //要返回的NFA

        if (type == NodeType.BASIC) { //结点类型为basis，如 字母
            nfa.getTransitTable().addEdge(nfa.getStartState(), nfa.getAcceptState(), new RelationshipEdge(node.getValue()));
            return nfa;
        } else if (type == NodeType.KLNEENE_CLOSURE) {  //结点类型为闭包
            NFA temp_nfa = translate(node.getFirstChild()); //用来存放闭包括号内的RE形成的NFA
            temp_nfa.getStartState().setType(StateType.middle);
            temp_nfa.getAcceptState().setType(StateType.middle);
            nfa.merge(temp_nfa);
            nfa.getTransitTable().addEdge(nfa.getStartState(), temp_nfa.getStartState(), new RelationshipEdge('ε'));
            nfa.getTransitTable().addEdge(temp_nfa.getAcceptState(), nfa.getAcceptState(), new RelationshipEdge('ε'));
            nfa.getTransitTable().addEdge(nfa.getStartState(), nfa.getAcceptState(), new RelationshipEdge('ε'));
            nfa.getTransitTable().addEdge(temp_nfa.getAcceptState(), temp_nfa.getStartState(), new RelationshipEdge('ε'));
            return nfa;
        } else {
            PatternTreeNode n = node.getFirstChild();

            NFA temp01_nfa = translate(n);
            nfa.merge(temp01_nfa);
            temp01_nfa.getAcceptState().setType(StateType.middle);
            while (n.getNextSibling() != null) {
                NFA temp02_nfa = translate(n.getNextSibling());
                n = n.getNextSibling();
                nfa.merge(temp02_nfa);
                temp02_nfa.getStartState().setType(StateType.middle);
                if (type == NodeType.CONCATENATION) { //结点类型为连接符
                    nfa.getTransitTable().addEdge(temp01_nfa.getAcceptState(), temp02_nfa.getStartState(), new RelationshipEdge('ε'));
                    nfa.setStartState(temp01_nfa.getStartState());
                    nfa.setAcceptState(temp02_nfa.getAcceptState());
                } else { //结点类型为Union
                    temp01_nfa.getStartState().setType(StateType.middle);
                    temp02_nfa.getAcceptState().setType(StateType.middle);
                    nfa.getTransitTable().addEdge(nfa.getStartState(), temp01_nfa.getStartState(), new RelationshipEdge('ε'));
                    nfa.getTransitTable().addEdge(nfa.getStartState(), temp02_nfa.getStartState(), new RelationshipEdge('ε'));
                    nfa.getTransitTable().addEdge(temp01_nfa.getAcceptState(), nfa.getAcceptState(), new RelationshipEdge('ε'));
                    nfa.getTransitTable().addEdge(temp02_nfa.getAcceptState(), nfa.getAcceptState(), new RelationshipEdge('ε'));
                }
                temp01_nfa = nfa;
                temp01_nfa.getAcceptState().setType(StateType.middle);
            }
            return nfa;
        }
    }
}
