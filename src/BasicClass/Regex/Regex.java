package BasicClass.Regex;

import BasicClass.FA.NFA;
import BasicClass.PatternTree.PatternTree;
import BasicClass.PatternTree.PatternTreeNode;
import Tools.RegexParser;

import java.util.ArrayDeque;

public class Regex {
    String strRegex;
    PatternTree tree;
    NFA nfa;

    public String getStrRegex() {
        return strRegex;
    }

    public void setStrRegex(String regex) {
        this.strRegex = regex;
    }

    public PatternTree getTree() {
        return tree;
    }

    public void setTree(PatternTree tree) {
        this.tree = tree;
    }

    public NFA getNfa() {
        return nfa;
    }

    public void setNfa(NFA nfa) {
        this.nfa = nfa;
    }

    public void compile(){
        RegexParser parser = new RegexParser(this);
        //解析RE
        this.tree = parser.Parse();
        printTree();
    }

    //打印生成的AST
    private void printTree() {
        PatternTreeNode root = tree.getRoot();
        if(root != null){
            ArrayDeque<PatternTreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            PatternTreeNode node = queue.poll();

            while(node != null){
                System.out.println("(" + node.getValue() + ")" + "->" + node.getType() + "\r");
                PatternTreeNode childnode = node.getFirstChild();

                if(childnode != null) {
                    System.out.println("\tfirstChild:(" + childnode.getValue() + ")" + "->" + childnode.getType() + "\r");
                    queue.add(childnode);
                    childnode = childnode.getNextSibling();

                    while (childnode != null) {
                        System.out.println("\t(" + childnode.getValue() + ")" + "->" + childnode.getType() + "\r");
                        queue.add(childnode);
                        childnode = childnode.getNextSibling();
                    }
                }
                node = queue.poll();
            }
        }
        System.out.println("-------------------------------------------");
    }
}
