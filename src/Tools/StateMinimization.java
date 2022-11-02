package Tools;

import BasicClass.FA.*;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.*;

public class StateMinimization {
    private static int groupCnt = 0;

    // DFA状态最小化
    public static DFA minimizeDFA(DFA dfa) {
        // 定义最小化的DFA
        DFA minDFA = new DFA();

        // 获取原DFA的转换表、接受状态集合和剩下的状态集合
        DirectedPseudograph<Dstate, RelationshipEdge> transitTable = dfa.getTransitTable();
        ArrayList<Dstate> excStates = new ArrayList<>(dfa.getD_states());
        ArrayList<Dstate> accStates = new ArrayList<>(dfa.getAccept());

        // 所有状态集合减去剩下的状态集合
        excStates.removeAll(accStates);
        // 建立初始划分的两个组，包含接受状态集合和其他状态集合
        Dgroup excGroup = new Dgroup(groupCnt++, new HashSet<>(excStates));
        Dgroup accGroup = new Dgroup(groupCnt++, new HashSet<>(accStates));
        Set<Dgroup> curPartition = new HashSet<>();
        Set<Dgroup> finalPartition = new HashSet<>();
        // 初始划分中加入两个组
        finalPartition.add(excGroup);
        finalPartition.add(accGroup);

        // 获取DFA的所有输入符号
        ArrayList<Character> inputSymbols = dfa.getInputSymbol();

        // 遍历所有输入符号，得到真正的最终划分
        int cnt = 0;
        for(char symbol : inputSymbols){
            curPartition = finalPartition;
            finalPartition = divideGroups(symbol, finalPartition, transitTable);
            System.out.println("Step"+cnt+": ");
            printPartition(finalPartition);
            cnt++;
        }


        // 建立最小化的DFA
        DirectedPseudograph<Dstate, RelationshipEdge> newTransitTable = new DirectedPseudograph<>(RelationshipEdge.class);
        ArrayList<Dstate> newAllStateList = new ArrayList<>();
        ArrayList<Dstate> newAccStateList = new ArrayList<>();
        boolean isAcc = false;
        // 新建一个minDFA 其中每个DFA状态对应原DFA的一个组
        for(Dgroup group : finalPartition){
            if(group.getdStateSet().contains(dfa.getStart())){
                Dstate startState = new Dstate();
                startState.setId(group.getGroupId());
                minDFA.setStart(startState);
                for(Dstate state : group.getdStateSet()){
                    for(State nfaState : state.getNfa_state()){
                        if(!minDFA.getStart().getNfa_state().contains(nfaState)){
                            minDFA.getStart().getNfa_state().add(nfaState);
                        }
                    }
                }
                newAllStateList.add(startState);
            }
            else{
                for(Dstate accState : accStates){
                    if(group.getdStateSet().contains(accState)){
                        Dstate acceptState = new Dstate();
                        acceptState.setId(group.getGroupId());
                        for(State nfaState : accState.getNfa_state()){
                            if(!acceptState.getNfa_state().contains(nfaState)){
                                acceptState.getNfa_state().add(nfaState);
                            }
                        }
                        minDFA.getAccept().add(acceptState);
                        newAccStateList.add(acceptState);
                        newAllStateList.add(acceptState);
                        isAcc = true;
                        break;
                    }
                }
                if(!isAcc){
                    Dstate otherState = new Dstate();
                    otherState.setId(group.getGroupId());
                    for (Dstate state : group.getdStateSet()){
                        for (State nfaState : state.getNfa_state()){
                            if(!otherState.getNfa_state().contains(nfaState)){
                                otherState.getNfa_state().add(nfaState);
                            }
                        }
                    }
                    newAllStateList.add(otherState);
                }
            }
        }
        // 将接受状态集合与所有状态的集合加入到最小化DFA中
        minDFA.setAccept(newAccStateList);
        minDFA.setD_states(newAllStateList);
        // 构建DFA转换表
        for(Dstate dstate : newAllStateList) {
            newTransitTable.addVertex(dstate);
        }
        Set<RelationshipEdge> edgeSet = transitTable.edgeSet();
        for(RelationshipEdge edge : edgeSet){
            int targetID = groupDstateBelong(finalPartition, transitTable.getEdgeTarget(edge));
            int sourceID = groupDstateBelong(finalPartition, transitTable.getEdgeSource(edge));
            Dstate sourceState = getDFAStateByID(sourceID, minDFA);
            Dstate targetState = getDFAStateByID(targetID, minDFA);
            if (!duplicateEdge(sourceState, targetState, edge.getLabel(), newTransitTable)){
                newTransitTable.addEdge(sourceState, targetState, new RelationshipEdge(edge.getLabel()));
            }
        }
        minDFA.setTransitTable(newTransitTable);

        return minDFA;
    }

    // 将划分中的组，满足条件的分成多个组
    public static Set<Dgroup> divideGroups(char symbol, Set<Dgroup> partition,
                                    DirectedPseudograph<Dstate, RelationshipEdge> transitTable){
        // 获取划分
        Set<Dgroup> finalPartition = new HashSet<>();
        Set<Dgroup> currentPartition = partition;
        // 使用队列来储存划分中的每一个组
        Queue<Dgroup> dgroupQueue = new LinkedList<>(currentPartition);

        // 当队列不为空
        while(!dgroupQueue.isEmpty()){
            // group出队
            Dgroup curGroup = dgroupQueue.poll();
            Map<Dgroup, List<Dstate>> map = new HashMap<>();
            // 对每个组中包含的状态进行遍历，找到那个状态对应输入符号转换到的组
            for(Dstate stateVertex : curGroup.getdStateSet()){
                // 获取到该状态获取输入符号后转换到的组
                Dgroup targetGroup = findTargetGroup(symbol, stateVertex, currentPartition, transitTable);
                if(!map.containsKey(targetGroup)){
                    map.put(targetGroup, new ArrayList<>());
                }
                map.get(targetGroup).add(stateVertex);
            }

            // 一个组中的状态通过输入符号全部映射到一个组
            if(map.size() == 1){
                finalPartition.add(curGroup);
            }
            // 若一个组中的状态通过输入符号映射到不止一个组
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

    // 输入DFA状态和当前输入符号，得到该状态获取输入符号后转换到的下一个组
    public static Dgroup findTargetGroup(char symbol, Dstate stateVertex, Set<Dgroup> partition,
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


    public static int groupDstateBelong(Set<Dgroup> partition, Dstate state){
        for(Dgroup dgroup : partition){
            if(dgroup.getdStateSet().contains(state)){
                return dgroup.getGroupId();
            }
        }
        return -1;
    }


    public static Dstate getDFAStateByID(int id, DFA dfa){
        for(Dstate dstate : dfa.getD_states()){
            if (dstate.getId() == id){
                return dstate;
            }
        }
        return null;
    }

    public static boolean duplicateEdge(Dstate source, Dstate target, char label,
                                        DirectedPseudograph<Dstate, RelationshipEdge> transitTable){
        Set<RelationshipEdge> edgeSet = transitTable.edgeSet();
        if (edgeSet.isEmpty())
            return false;
        for (RelationshipEdge edge : edgeSet) {
            Dstate sourceState = transitTable.getEdgeSource(edge);
            Dstate targetState = transitTable.getEdgeTarget(edge);
            char slabel = edge.getLabel();
            if (source.getId() == sourceState.getId() && target.getId() == targetState.getId()
            && slabel == label){
                return true;
            }
        }
        return false;
    }

    public static void printPartition(Set<Dgroup> partition){
        for (Dgroup dgroup : partition) {
            System.out.print(dgroup.getGroupId()+":{");
            Iterator<Dstate> it = dgroup.getdStateSet().iterator();
            while(it.hasNext()){
                Dstate state = it.next();
                System.out.print(state.getId());
                if (it.hasNext()){
                    System.out.print(",");
                }
            }
            System.out.print("}");
            System.out.println();
        }
    }
}
