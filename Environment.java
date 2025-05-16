import java.util.*;

public class Environment {
    private int numOfNodes;
    private int valuesPerNode;
    private int dataNode;
    private int startNode;
    private double detectionProbability;
    private int initialSecurity;

    private boolean attackerDetected;
    private boolean dataNodeCracked;
    private boolean attackerAttackSuccessful;
    private boolean attackerMaxedAllValues;
    private boolean defenderMaxedAllValues;

    private List<Action> attackerNodesWithMaxValue;
    private List<Action> defenderNodesWithMaxValue;

    private double[][] attackValues;
    private double[][] defenceValues;
    private int[][] neighboursMatrix;
    private Random rng;

    public Environment(int numOfNodes, int valuesPerNode, int[][] neighboursMatrix,
            int dataNode, int startNode, double detectionProbability, int initialSecurity) {
        this.numOfNodes = numOfNodes;
        this.valuesPerNode = valuesPerNode;
        this.neighboursMatrix = neighboursMatrix;
        this.dataNode = dataNode;
        this.startNode = startNode;
        this.detectionProbability = detectionProbability;
        this.initialSecurity = initialSecurity;
        this.rng = new Random(42);
        reset();
    }

    public void reset() {
        attackerDetected = false;
        dataNodeCracked = false;
        attackerAttackSuccessful = false;
        attackerMaxedAllValues = false;
        defenderMaxedAllValues = false;

        attackerNodesWithMaxValue = new ArrayList<>();
        defenderNodesWithMaxValue = new ArrayList<>();

        attackValues = new double[numOfNodes][valuesPerNode];
        defenceValues = new double[numOfNodes][valuesPerNode + 1];

        for (int i = 0; i < numOfNodes; i++) {
            Arrays.fill(defenceValues[i], initialSecurity);
        }
        for (int i = 1; i < numOfNodes; i++) {
            int rnd = rng.nextInt(valuesPerNode);
            defenceValues[i][rnd] = 0;
            defenceValues[i][valuesPerNode] = detectionProbability;
        }
        for (int i = 0; i < valuesPerNode; i++) {
            defenceValues[startNode][i] -= initialSecurity;
        }
        defenceValues[startNode][valuesPerNode] = 0;
    }

    public void doDefenderAction(int node, int defenceType) {
        defenceValues[node][defenceType] += 1;
        if (defenceValues[node][defenceType] == 10) {
            defenderNodesWithMaxValue.add(new Action(node, defenceType));
        }
        if (defenderNodesWithMaxValue.size() == numOfNodes * (valuesPerNode + 1)) {
            defenderMaxedAllValues = true;
        }
    }

    public int[] doAttackerAction(int node, int attackType) {
        attackerAttackSuccessful = false;
        attackValues[node][attackType] += 1;
        if (attackValues[node][attackType] == 10) {
            attackerNodesWithMaxValue.add(new Action(node, attackType));
        }
        if (attackerNodesWithMaxValue.size() == numOfNodes * valuesPerNode) {
            attackerMaxedAllValues = true;
        }
        if (attackValues[node][attackType] > defenceValues[node][attackType]) {
            if (node == dataNode) {
                dataNodeCracked = true;
                return new int[] { 100, -100 };
            } else {
                attackerAttackSuccessful = true;
                return new int[] { 0, 0 };
            }
        } else {
            double chance = new Random().nextDouble() * 10;
            if (chance < defenceValues[node][valuesPerNode]) {
                attackerDetected = true;
                return new int[] { -100, 100 };
            }
            return new int[] { 0, 0 };
        }
    }

    public boolean terminationCondition() {
        return dataNodeCracked || attackerDetected || attackerMaxedAllValues || defenderMaxedAllValues;
    }

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public int getValuesPerNode() {
        return valuesPerNode;
    }

    public List<Action> getDefenderNodesWithMaxValue() {
        return defenderNodesWithMaxValue;
    }

    public double[][] getDefenceValues() {
        return defenceValues;
    }

    public boolean isAttackerAttackSuccessful() {
        return attackerAttackSuccessful;
    }

    public boolean isAttackerMaxedAllValues() {
        return attackerMaxedAllValues;
    }

    public boolean isDefenderMaxedAllValues() {
        return defenderMaxedAllValues;
    }

    public boolean isDataNodeCracked() {
        return dataNodeCracked;
    }

    public boolean isAttackerDetected() {
        return attackerDetected;
    }

    public int getStartNode() {
        return startNode;
    }

    public List<Integer> getNeighbours(int node) {
        List<Integer> neighbours = new ArrayList<>();
        for (int n : neighboursMatrix[node]) {
            neighbours.add(n);
        }
        return neighbours;
    }

    public boolean isMaxValueNode(Action action) {
        return attackerNodesWithMaxValue.contains(action);
    }
}