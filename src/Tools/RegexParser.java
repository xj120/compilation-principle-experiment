package Tools;

import BasicClass.PatternTree.NodeType;
import BasicClass.PatternTree.PatternTree;
import BasicClass.PatternTree.PatternTreeNode;
import BasicClass.Regex.Regex;

import javax.swing.plaf.synth.SynthLookAndFeel;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Function;

public class RegexParser {
    private ArrayDeque<Character> arrayDeque = new ArrayDeque<>();

    public RegexParser(Regex regex){
        char[] ch = regex.getStrRegex().toCharArray();
        for(char c: ch){
            this.arrayDeque.add(c);
        }
        this.arrayDeque.add('%');
    }

    //检查RE中的括号是否对称匹配
    public boolean checkThesis(){
        ArrayDeque<Character> tempDeque = new ArrayDeque<>(this.arrayDeque);
        Stack<Character> checkStack = new Stack<>();
        while(!tempDeque.isEmpty()) {
            char ch = tempDeque.remove();
            if(ch == ')'){
                if(!checkStack.isEmpty()) {
                    checkStack.pop();
                }
                else {
                    return false;
                }
            }
            else if (ch == '('){
                checkStack.push(ch);
            }
        }
        return checkStack.isEmpty();
    }

    //将RE解析成一个正则表达式树
    public PatternTree Parse(){
        //若arrayDeque为空
        if(this.arrayDeque.isEmpty()) {
            return null;
        }

        //检查括号是否对称匹配
        if(!checkThesis()) {
            System.out.println("ILLEGAL REGEX! Asymmetry of parentheses");
            return null;
        }

        PatternTree tree = new PatternTree();

        Stack<PatternTreeNode> nodeStack = new Stack<>();

        char lookahead = this.arrayDeque.remove();

        //若RE只有%
        if(lookahead == '%') {
            System.out.println("A NULL REGEX");
            return null;
        }
        else if(lookahead != 'ε' && lookahead != '(' && !Character.isLetter(lookahead)) {
            System.out.println("ILLEGAL REGEX! Wrong beginning");
            return null;
        }

        NodeType nodeType;

        //区分类型
        if(Character.isLetter(lookahead) || lookahead == 'ε') {
            nodeType = NodeType.BASIC;
        }
        else {
            nodeType = NodeType.LEFT_PARENTHESIS;
        }

        //压入栈中
        nodeStack.push(new PatternTreeNode(null, null, lookahead, nodeType));

        //获取下一个节点
        lookahead = this.arrayDeque.remove();

        //当字符不是结束字符，分情况讨论
        while(lookahead != '%') {
            PatternTreeNode node;
            //下一个字符是*
            if(lookahead == '*') {
                nodeType = nodeStack.peek().getType();
                if(nodeType == NodeType.BASIC) {
                    node = new PatternTreeNode(nodeStack.pop(), null, lookahead, NodeType.KLNEENE_CLOSURE);
                }
                else if(nodeType == NodeType.RIGHT_PARENTHESIS) {
                    nodeStack.pop();
                    node = new PatternTreeNode(nodeStack.pop(), null, lookahead, NodeType.KLNEENE_CLOSURE);
                    nodeStack.pop();
                }
                else {
                    System.out.println("ILLEGAL REGEX! Wrong kleene closure");
                    return null;
                }
                //将*节点压入栈
                nodeStack.push(node);
            }
            //下一个字符是（
            else if(lookahead == '(') {
                node = new PatternTreeNode(null, null, lookahead, NodeType.LEFT_PARENTHESIS);
                //将（节点压入栈
                nodeStack.push(node);
            }
            //下一个字符是）
            else if(lookahead == ')') {
                nodeType = nodeStack.peek().getType();
                //栈顶节点是（，即（）的情况
                if(nodeType == NodeType.LEFT_PARENTHESIS) {
                    nodeStack.pop();
                }
                else {
                    //用一个栈把两个括号之间的节点存起来
                    Stack<PatternTreeNode> thesisStack = new Stack<>();
                    while(!nodeStack.isEmpty() && nodeStack.peek().getType() != NodeType.LEFT_PARENTHESIS) {
                        //得到栈顶节点的字符类别
                        NodeType type = nodeStack.peek().getType();
                        //普通字符，连接和闭包的情况
                        if(type == NodeType.BASIC || type == NodeType.CONCATENATION || type == NodeType.KLNEENE_CLOSURE) {
                            thesisStack.push(nodeStack.pop());
                        }
                        //并的情况
                        else if(type == NodeType.UNION) {
                            // |）的非法情况
                            if(thesisStack.size() == 0) {
                                System.out.println("ILLEGAL REGEX! Wrong |)");
                                return null;
                            }

                            // |..)的情况
                            //将|节点出栈
                            PatternTreeNode uNode = nodeStack.pop();
                            if(thesisStack.size()>1) {
                                //创建一个表示连接的节点
                                PatternTreeNode cNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
                                //将存放两个括号之间的字符节点的栈的全部节点转为连接节点的子节点
                                cNode.mergeStackNode(thesisStack);
                                uNode.getLastChild().setNextSibling(cNode);
                            }
                            // |.)
                            // 不需要‘-’节点来连接，直接将此唯一节点出栈，并设为‘|’节点的子节点
                            else{
                                uNode.getLastChild().setNextSibling(thesisStack.pop());
                            }
                            //将‘|’节点入thesis栈（括号内）
                            thesisStack.push(uNode);
                        }
                        //)的情况
                        else if(nodeStack.peek().getType() == NodeType.RIGHT_PARENTHESIS) {
                            nodeStack.pop();
                            thesisStack.push(nodeStack.pop());
                            nodeStack.pop();
                        }
                    }

                    //nodeStack栈顶为左括号
                    if(nodeStack.peek().getType() == NodeType.LEFT_PARENTHESIS) {
                        //分情况将一个节点存入栈
                        if(!thesisStack.isEmpty()) {
                            //(..)的情况
                            if(thesisStack.size()>1) {
                                PatternTreeNode cNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
                                cNode.mergeStackNode(thesisStack);
                                nodeStack.push(cNode);
                            }
                            //(.)
                            else {
                                nodeStack.push(thesisStack.pop());
                            }
                            PatternTreeNode rNode = new PatternTreeNode(null, null, lookahead, NodeType.RIGHT_PARENTHESIS);
                            nodeStack.push(rNode);
                        }
                        //()情况
                        else {
                            nodeStack.pop();
                        }
                    }
                }
            }
            else if(lookahead == '|') {
                //获取栈顶元素字符类别
                nodeType = nodeStack.peek().getType();
                // || 或者（|的情况
                if(nodeType == NodeType.UNION || nodeType == NodeType.LEFT_PARENTHESIS) {
                    System.out.println("ILLEGAL REGEX! Wrong (| or ||");
                    return null;
                }

                PatternTreeNode uNode;
                // 创建一个栈将|..|和(..|之间的节点存入栈
                Stack<PatternTreeNode> unionStack = new Stack<>();
                while(!nodeStack.isEmpty() && nodeStack.peek().getType() != NodeType.UNION && nodeStack.peek().getType() != NodeType.LEFT_PARENTHESIS) {
                    unionStack.push(nodeStack.pop());
                }

                // (..|的情况
                if(nodeStack.isEmpty() || nodeStack.peek().getType() == NodeType.LEFT_PARENTHESIS){
                    uNode = new PatternTreeNode(null, null, lookahead, NodeType.UNION);
                    PatternTreeNode fcNode;
                    // (.| 直接pop
                    if(unionStack.size() == 1) {
                        fcNode = unionStack.pop();
                    }
                    // (..| 需要先合并
                    else if(unionStack.size() > 1) {
                        PatternTreeNode cNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
                        cNode.mergeStackNode(unionStack);
                        fcNode = cNode;
                    }
                    else {
                        System.out.println("ILLEGAL REGEX!");
                        return null;
                    }
                    uNode.setFirstChild(fcNode);
                }
                // |..|的情况
                else {
                    // 将节点设为nodeStack栈顶的'|'节点
                    uNode = nodeStack.pop();
                    // 获取其最后一个子节点
                    PatternTreeNode lNode = uNode.getLastChild();
                    // 最后一个子节点设置兄弟节点
                    // |.| 直接pop
                    if(unionStack.size() == 1) {
                        lNode.setNextSibling(unionStack.pop());
                    }
                    // |..| 需要合并
                    else {
                        PatternTreeNode cNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
                        cNode.mergeStackNode(unionStack);
                        lNode.setFirstChild(cNode);
                    }
                }

                //将unionNode存入nodeStack栈
                nodeStack.push(uNode);

            }
            //直接存进nodeStack中
            else if(lookahead == 'ε' || Character.isLetter(lookahead)) {
                PatternTreeNode bNode = new PatternTreeNode(null, null, lookahead, NodeType.BASIC);
                nodeStack.push(bNode);
            }

            //处理完毕，获取RE下一个字符
            lookahead = this.arrayDeque.remove();
        }

        // nodeStack为空
        if(nodeStack.isEmpty()){
            return null;
        }

        //新建一个tempStack
        Stack<PatternTreeNode> tempStack = new Stack<>();
        while(!nodeStack.isEmpty() && nodeStack.peek().getType() != NodeType.UNION) {
            tempStack.push(nodeStack.pop());
        }

        //若nodeStack为空,则还原
        if(nodeStack.isEmpty()) {
            while(!tempStack.isEmpty()){
                nodeStack.push(tempStack.pop());
            }
        }
        //若栈顶元素类型为并,
        else if(nodeStack.peek().getType() == NodeType.UNION) {
            //若tempStack栈顶为），则还原
            if(tempStack.peek().getType() == NodeType.RIGHT_PARENTHESIS) {
                while(!tempStack.isEmpty()) {
                    nodeStack.push(tempStack.pop());
                }
            }
            //否则，将tempStack中的节点设成cNode的子节点，cNode再作为uNode的子节点
            else {
                PatternTreeNode uNode;
                uNode = nodeStack.pop();
                if(nodeStack.size() == 1) {
                    uNode.getLastChild().setNextSibling(tempStack.pop());
                }
                else{
                    PatternTreeNode cNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
                    cNode.mergeStackNode(tempStack);
                    uNode.getLastChild().setNextSibling(cNode);
                }
                nodeStack.push(uNode);
            }
        }


        //建立treeStack
        Stack<PatternTreeNode> treeStack = new Stack<>();
        while(!nodeStack.isEmpty()) {
            //将左括号和右括号节点除去
            if(nodeStack.peek().getType() == NodeType.LEFT_PARENTHESIS || nodeStack.peek().getType() == NodeType.RIGHT_PARENTHESIS) {
                nodeStack.pop();
            }
            else {
                treeStack.push(nodeStack.pop());
            }
        }

        if(treeStack.isEmpty()) {
            return null;
        }

        //若treeStack的节点数量大于1
        if(treeStack.size()>1) {
            PatternTreeNode treeNode = new PatternTreeNode(null, null, '-', NodeType.CONCATENATION);
            treeNode.mergeStackNode(treeStack);
            tree.setRoot(treeNode);
        }
        else {
            tree.setRoot(treeStack.pop());
        }

        return tree;
    }

}
