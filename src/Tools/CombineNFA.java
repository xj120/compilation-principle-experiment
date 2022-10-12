package Tools;

import BasicClass.FA.NFA;
import BasicClass.FA.RelationshipEdge;
import BasicClass.FA.StateType;

import java.util.ArrayList;

public class CombineNFA {  //把多个NFA拼接为一个的工具类
    public static NFA CombineNFAIntoOne(ArrayList<NFA> nfa_list) {
        NFA final_nfa = new NFA();

        for (NFA nfa : nfa_list) {
            final_nfa.merge(nfa);
            nfa.getStartState().setType(StateType.middle);
            nfa.getAcceptState().setType(StateType.middle);
            final_nfa.getTransitTable().addEdge(final_nfa.getStartState(), nfa.getStartState(), new RelationshipEdge('ε'));
            final_nfa.getTransitTable().addEdge(nfa.getAcceptState(), final_nfa.getAcceptState(), new RelationshipEdge('ε'));
        }

        return final_nfa;
    }
}
