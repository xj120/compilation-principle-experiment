package BasicClass.FA;

import java.util.ArrayList;

public class Dstate extends State {
    private boolean mark;
    private ArrayList<State> nfa_state;
    public static int D_STATE_ID = 0;

    public Dstate() {
        this.nfa_state = new ArrayList<>();
        this.mark = false;
        this.setId(D_STATE_ID);
        D_STATE_ID += 1;
    }

    public Dstate(StateType type) {
        this.nfa_state = new ArrayList<>();
        this.mark = false;
        this.setType(type);
        this.setId(D_STATE_ID);
        D_STATE_ID += 1;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public ArrayList<State> getNfa_state() {
        return nfa_state;
    }

    public void setNfa_state(ArrayList<State> nfa_state) {
        this.nfa_state = nfa_state;
    }

    public boolean equals(Dstate d) {
        if (nfa_state.size() != d.getNfa_state().size()) {
            return false;
        } else {
            int count = 0;
            for (State s : nfa_state) {
                for (State sd : d.getNfa_state()) {
                    if (s.getId() == sd.getId()) {
                        count++;
                        break;
                    }
                }
            }
            return count == nfa_state.size();
        }
    }

    public boolean contains(State s) {
        for (State ns : nfa_state) {
            if (ns.getId() == s.getId()) {
                return true;
            }
        }
        return false;
    }
}
