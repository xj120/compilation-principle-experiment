package BasicClass.FA;

import org.jgrapht.graph.*;

public class RelationshipEdge extends DefaultEdge {
    private char label; //String label;

    public RelationshipEdge(char label) {
        super();
        this.label = label;
    }

    public char getLabel() {//String getLabel()
        return label;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + label + ")";
    }
}
