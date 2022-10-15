package BasicClass.PatternTree;


import java.util.Stack;

public class PatternTreeNode {
    private PatternTreeNode firstChild;
    private PatternTreeNode nextSibling;
    private Character value;
    private NodeType type;

    public PatternTreeNode getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(PatternTreeNode firstChild) {
        this.firstChild = firstChild;
    }

    public PatternTreeNode getNextSibling() {
        return nextSibling;
    }

    public void setNextSibling(PatternTreeNode nextSibling) {
        this.nextSibling = nextSibling;
    }

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public PatternTreeNode(PatternTreeNode firstChild, PatternTreeNode nextSibling, Character value, NodeType type) {
        this.firstChild = firstChild;
        this.nextSibling = nextSibling;
        this.value = value;
        this.type = type;
    }

    //得到一个节点的最后一个子节点
    public PatternTreeNode getLastChild(){
        PatternTreeNode node = this.getFirstChild();
        if(node != null) {
            while(node.getNextSibling() != null) {
                node = node.getNextSibling();
            }
        }
        return node;
    }

    //将一个栈中的节点设为this节点的子节点
    public void mergeStackNode(Stack<PatternTreeNode> stack) {
        PatternTreeNode tempNode = stack.pop();
        PatternTreeNode firstChild = tempNode;
        while(!stack.isEmpty()) {
            PatternTreeNode internalNode = stack.pop();
            tempNode.setNextSibling(internalNode);
            tempNode = tempNode.getNextSibling();
        }
        if(this.firstChild == null) {
            this.setFirstChild(firstChild);
        }
        else{
            this.getLastChild().setNextSibling(firstChild);
        }
    }

}
