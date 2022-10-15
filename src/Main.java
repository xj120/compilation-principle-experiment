import BasicClass.FA.NFA;
import BasicClass.Regex.Regex;
import BasicClass.Regex.RegexSet;
import Tools.CombineNFA;
import Tools.ThompsonConstructor;

import java.util.ArrayList;

public class Main {

    public static final char[] symbols = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args) {
        // String[] regexes = new String[]{"c(a|b|c)*"};
        String[] regexes = new String[]{"c*(a|b)"};
        RegexSet aRegexes = new RegexSet(regexes);
        for (Regex r : aRegexes.getRegexes()) {
            r.compile();
        }
        ArrayList<NFA> nfa_list = new ArrayList<>();
        for (Regex r : aRegexes.getRegexes()) {
            nfa_list.add(ThompsonConstructor.translate(r.getTree().getRoot()));
        }
        NFA final_nfa = CombineNFA.CombineNFAIntoOne(nfa_list);
        final_nfa.showNFA();
    }
}
