package BasicClass.Regex;

import java.util.LinkedList;

public class RegexSet {
    public LinkedList<Regex> regexes = new LinkedList<>();

    //将输入的字符串数组形式的regexSet转为RegexSet
    public RegexSet(String[] regexSet) {
        for(String reg: regexSet) {
            Regex regex = new Regex();
            regex.setStrRegex(reg);
            this.regexes.add(regex);
        }
    }

    public LinkedList<Regex> getRegexes() {
        return regexes;
    }

}
