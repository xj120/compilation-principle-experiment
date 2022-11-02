package Tools;

import BasicClass.FA.DFA;
import BasicClass.FA.Dgroup;
import BasicClass.FA.Dstate;
import BasicClass.FA.RelationshipEdge;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.*;

public class StateMinimization {
    private static int groupCnt = 0;

    public DFA minimizeDFA(DFA dfa) {
        DFA minDFA = new DFA();

        DirectedPseudograph<Dstate, RelationshipEdge> transitTable = dfa.getTransitTable();
        ArrayList<Dstate> excStates = new ArrayList<>(dfa.getD_states());
        ArrayList<Dstate> accStates = new ArrayList<>(Arrays.asList(dfa.getAccept()));

        excStates.removeAll(accStates);
        Dgroup excGroup = new Dgroup(groupCnt++, new HashSet<>(excStates));
        Dgroup accGroup = new Dgroup(groupCnt++, new HashSet<>(accStates));
        Set<Dgroup> curPartition = new HashSet<>();
        Set<Dgroup> finalPartition = new HashSet<>();
        finalPartition.add(excGroup);
        finalPartition.add(accGroup);

        char[] inputSymbols = dfa.getSymbols();

        for(char symbol : inputSymbols){
            curPartition = finalPartition;
            finalPartition = divideGroups(symbol, finalPartition, transitTable);
        }

        for(Dgroup group : finalPartition){
            if(group.getdStateSet().contains(dfa.getStart())){
                minDFA.setStart(new Dstate());
                Dstate start = dfa.getStart();
                Set<RelationshipEdge> set = transitTable.edgesOf(start);
                for(char symbol : inputSymbols){
                    for(RelationshipEdge edge : set){

                    }
                }
            }
        }

        return minDFA;
    }

    public Set<Dgroup> divideGroups(char symbol, Set<Dgroup> partition,
                                    DirectedPseudograph<Dstate, RelationshipEdge> transitTable){
        Set<Dgroup> finalPartition = new HashSet<>();
        Set<Dgroup> currentPartition = partition;
        Queue<Dgroup> dgroupQueue = new LinkedList<>(currentPartition);

        while(!dgroupQueue.isEmpty()){
            Dgroup curGroup = dgroupQueue.poll();
            Map<Dgroup, List<Dstate>> map = new HashMap<>();
            for(Dstate stateVertex : curGroup.getdStateSet()){
                Dgroup targetGroup = findTargetGroup(symbol, stateVertex, currentPartition, transitTable);
                if(!map.containsKey(targetGroup)){
                    map.put(targetGroup, new ArrayList<>());
                }
                map.get(targetGroup).add(stateVertex);
            }
            if(map.size() == 1){
                finalPartition.add(curGroup);
            }
            else{
                currentPartition.remove(curGroup);
                for(List<Dstate> list : map.values()){
                    Dgroup newGroup = new Dgroup(groupCnt++, new HashSet<>(list));
                    currentPartition.add(newGroup);
                    dgroupQueue.add(newGroup);
                }
            }
        }

        return finalPartition;
    }

    public Dgroup findTargetGroup(char symbol, Dstate stateVertex, Set<Dgroup> partition,
                                  DirectedPseudograph<Dstate, RelationshipEdge> transitTable){
        Set<RelationshipEdge> set = transitTable.edgesOf(stateVertex);
        Dstate targetVertex = null;
        for(RelationshipEdge e : set){
            if(e.getLabel() == symbol){
                targetVertex = transitTable.getEdgeTarget(e);
            }
        }
        for(Dgroup group : partition){
            if(group.getdStateSet().contains(targetVertex)){
                return group;
            }
        }
        return null;
    }

}
