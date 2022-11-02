import BasicClass.FA.DFA;
import BasicClass.FA.NFA;
import BasicClass.Regex.Regex;
import BasicClass.Regex.RegexSet;
import Tools.CombineNFA;
import Tools.SubsetConstructor;
import Tools.ThompsonConstructor;

import java.util.ArrayList;

public class Main {

    public static final char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args) {
        //String[] regexes = new String[]{"*ca"};  //错误输入
        String[] regexes = new String[]{"(a|b)*a"}; //正确输入  "c(a|b|c)*"
        RegexSet aRegexes = new RegexSet(regexes);
        for (Regex r : aRegexes.getRegexes()) {  //将RE转为语法树
            r.compile();
        }
        ArrayList<NFA> nfa_list = new ArrayList<>();
        for (Regex r : aRegexes.getRegexes()) {  //将语法树转为NFA
            r.setNfa(ThompsonConstructor.translate(r.getTree().getRoot()));
            nfa_list.add(r.getNfa());
        }
        NFA final_nfa = CombineNFA.CombineNFAIntoOne(nfa_list);  //将多个NFA组合在一起
        final_nfa.showNFA();   //打印NFA

        DFA dfa = SubsetConstructor.toDFA(final_nfa);
        dfa.showDFA();
        dfa.showDFATable();
    }
}
