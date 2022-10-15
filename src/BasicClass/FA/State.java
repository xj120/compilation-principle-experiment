package BasicClass.FA;

public class State {
    private int id;
    private StateType type;

    public static int STATE_ID = 0;

    public State() {
        this.id = State.STATE_ID;
        STATE_ID += 1;
    }

    public State(StateType type) {
        this.id = State.STATE_ID;
        this.type = type;
        STATE_ID += 1;
    }

    public int getId() {
        return id;
    }

    public StateType getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(StateType type) {
        this.type = type;
    }
}
