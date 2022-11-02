package Tools;

import BasicClass.FA.DFA;
import BasicClass.FA.Dgroup;
import BasicClass.FA.Dstate;
import BasicClass.FA.RelationshipEdge;
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
        for(char symbol : inputSymbols){
            curPartition = finalPartition;
            finalPartition = divideGroups(symbol, finalPartition, transitTable);
        }

        // 打印划分
        for(Dgroup group : finalPartition){
            Set<Dstate> set = group.getdStateSet();
            System.out.print(group.getGroupId()+":{");
            for(Dstate dstate : set){
                System.out.print(dstate.getId()+" ");
            }
            System.out.print("}");
            System.out.println();
        }

        // 建立最小化的DFA
        DirectedPseudograph<Dstate, RelationshipEdge> newTransitTable = new DirectedPseudograph<>(RelationshipEdge.class);
        ArrayList<Dstate> newAllStateList = new ArrayList<>();
        ArrayList<Dstate> newAccStateList = new ArrayList<>();
        boolean isAcc = false;
        // 取划分中的每个组中的一个元素作为代表
        for(Dgroup group : finalPartition){
            if(group.getdStateSet().contains(dfa.getStart())){
                minDFA.setStart(dfa.getStart());
                newAllStateList.add(minDFA.getStart());
            }
            else{
                for(Dstate accState : accStates){
                    if(group.getdStateSet().contains(accState)){
                        newAccStateList.add(accState);
                        newAllStateList.add(accState);
                        isAcc = true;
                        break;
                    }
                }
                if(!isAcc){
                    newAllStateList.add(group.getdStateSet().iterator().next());
                }
            }
        }
        // 将接受状态集合与所有状态的集合加入到最小化DFA中
        minDFA.setAccept(newAccStateList);
        minDFA.setD_states(newAllStateList);
        // 构建DFA转换表
        for(int i = 0; i < newAllStateList.size(); i++){
            for (Dstate dstate : newAllStateList) {
                Set<RelationshipEdge> edgeSet = transitTable.getAllEdges(newAllStateList.get(i), dstate);
                if(!edgeSet.isEmpty()){
                    for (RelationshipEdge edge : edgeSet) {
                        newTransitTable.addEdge(newAllStateList.get(i), dstate, edge);
                    }
                }
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

}
