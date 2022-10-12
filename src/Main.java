import BasicClass.FA.NFA;
import BasicClass.PatternTree.PatternTree;
import BasicClass.Regex.RegexSet;
import Tools.CombineNFA;
import Tools.ThompsonConstructor;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {  //TODO
        String[] regexes = {};
        RegexSet regexSet = new RegexSet(regexes);
        ArrayList<PatternTree> patternTrees = regexSet.parse();
        ArrayList<NFA> nfa_list = new ArrayList<>();
        for(PatternTree p : patternTrees) {
            NFA n = ThompsonConstructor.translate(p.root);
            nfa_list.add(n);
        }
        NFA final_nfa = CombineNFA.CombineNFAIntoOne(nfa_list);
        final_nfa.showNFA();
    }
}
