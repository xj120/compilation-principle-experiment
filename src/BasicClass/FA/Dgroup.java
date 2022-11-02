package BasicClass.FA;

import java.util.Set;

public class Dgroup {
    private int groupId;
    private Set<Dstate> dStateSet;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Set<Dstate> getdStateSet() {
        return dStateSet;
    }

    public void setdStateSet(Set<Dstate> dStateSet) {
        this.dStateSet = dStateSet;
    }

    public Dgroup(int groupId, Set<Dstate> dStateSet){
        this.groupId = groupId;
        this.dStateSet = dStateSet;
    }
}
