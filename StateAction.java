public class StateAction {
    public int state;
    public Action action;

    public StateAction(int state, Action action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StateAction)) return false;
        StateAction other = (StateAction) obj;
        return this.state == other.state && this.action.equals(other.action);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(state, action);
    }
}