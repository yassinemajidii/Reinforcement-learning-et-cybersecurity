public class Experience {
    public Object state;
    public Action action;
    public double reward;
    public Object nextState;
    public int done;

    public Experience(Object state, Action action, double reward, Object nextState, int done) {
        this.state = state;
        this.action = action;
        this.reward = reward;
        this.nextState = nextState;
        this.done = done;
    }
}