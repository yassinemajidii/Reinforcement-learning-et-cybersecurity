public class Action {
    private int node;
    private int actionType;

    public Action(int node, int actionType) {
        this.node = node;
        this.actionType = actionType;
    }

    public int getNode() {
        return node;
    }

    public int getActionType() {
        return actionType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Action)) return false;
        Action other = (Action) obj;
        return this.node == other.node && this.actionType == other.actionType;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(node, actionType);
    }

    @Override
    public String toString() {
        return "(" + node + ", " + actionType + ")";
    }
}