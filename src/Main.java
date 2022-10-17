import BasicClass.PatternTree.NodeType;
import BasicClass.Regex.Regex;
import BasicClass.Regex.RegexSet;
import Tools.RegexParser;

import java.util.ArrayDeque;

public class Main {

    public static final char[] symbols = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public static void main(String[] args) {
        String[] regexes = new String[]{"c(a|b|c)*"};
        RegexSet aRegexes = new RegexSet(regexes);
        for (Regex r : aRegexes.getRegexes()) {
            r.compile();
        }
    }
}
