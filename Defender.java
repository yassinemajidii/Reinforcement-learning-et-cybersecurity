import java.util.*;

public class Defender {
    private Environment environment; // L'environnement de la simulation
    private boolean learning; // Indique si le défenseur utilise un apprentissage
    private String learningAlgorithm; // Algorithme d'apprentissage utilisé
    private String exploration; // Stratégie d'exploration (e.g., epsilon_greedy, softmax)
    private Map<String, Map<String, Double>> algorithmsParameters; // Paramètres des algorithmes
    private Deque<Experience> replayBuffer; // Buffer pour stocker les expériences
    private Random rng; // Générateur de nombres aléatoires
    private double[][] qTable; // Table Q pour l'apprentissage par renforcement
    private Map<StateAction, List<Double>> returns; // Stocke les retours pour Monte Carlo

    // Constructeur
    public Defender(Environment environment, boolean learning, String learningAlgorithm, String exploration) {
        this.environment = environment;
        this.learning = learning;
        this.learningAlgorithm = learningAlgorithm;
        this.exploration = exploration;
        this.algorithmsParameters = getDefaultParameters();
        this.rng = new Random();
        this.replayBuffer = new ArrayDeque<>();
        this.qTable = new double[environment.getNumOfNodes()][environment.getValuesPerNode() + 1];
        this.returns = new HashMap<>();
    }

    // Initialisation des paramètres par défaut
    private Map<String, Map<String, Double>> getDefaultParameters() {
        Map<String, Map<String, Double>> params = new HashMap<>();
        Map<String, Double> mcEpsilonGreedy = new HashMap<>();
        mcEpsilonGreedy.put("alpha", 0.05); // Taux d'apprentissage
        mcEpsilonGreedy.put("epsilon", 0.1); // Paramètre d'exploration
        params.put("montecarlo_epsilon_greedy", mcEpsilonGreedy);
        return params;
    }

    // Sélection d'une action en fonction de la stratégie d'exploration
    public Action selectAction(Environment env, int episode) {
        if ("epsilon_greedy".equals(exploration)) {
            return selectActionEpsilonGreedy(algorithmsParameters.get("montecarlo_epsilon_greedy").get("epsilon"));
        } else if ("softmax".equals(exploration)) {
            return selectActionSoftmax(1.0); // Exemple : tau = 1.0
        } else {
            return selectRandomAction(); // Par défaut, sélection aléatoire
        }
    }

    // Stratégie epsilon-greedy
    private Action selectActionEpsilonGreedy(double epsilon) {
        if (rng.nextDouble() < epsilon) {
            return selectRandomAction(); // Exploration
        } else {
            return selectBestAction(); // Exploitation
        }
    }

    // Stratégie softmax (simplifiée)
    private Action selectActionSoftmax(double tau) {
        // Implémentez la logique softmax ici si nécessaire
        return selectRandomAction(); // Exemple simplifié
    }

    // Sélection de la meilleure action (exploitation)
    private Action selectBestAction() {
        Action bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int node = 0; node < environment.getNumOfNodes(); node++) {
            for (int defenseType = 0; defenseType <= environment.getValuesPerNode(); defenseType++) {
                double value = qTable[node][defenseType];
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = new Action(node, defenseType);
                }
            }
        }

        return bestAction != null ? bestAction : selectRandomAction(); // Retourne une action aléatoire si aucune action
    }

    // Sélection d'une action aléatoire
    public Action selectRandomAction() {
        List<Action> actions = new ArrayList<>();
        for (int node = 0; node < environment.getNumOfNodes(); node++) {
            for (int defenseType = 0; defenseType <= environment.getValuesPerNode(); defenseType++) {
                Action action = new Action(node, defenseType);
                if (!environment.getDefenderNodesWithMaxValue().contains(action)) {
                    actions.add(action);
                }
            }
        }

        // Vérification si la liste d'actions est vide
        if (actions.isEmpty()) {
            throw new IllegalStateException("No valid actions available for the defender.");
        }

        return actions.get(rng.nextInt(actions.size()));
    }

    // Réagit à une attaque en protégeant le nœud ciblé
    public Action reactToAttack(Action attackerAction) {
        // Récupérer le nœud ciblé par l'attaquant
        int targetNode = attackerAction.getNode();

        // Choisir une défense pour ce nœud
        int defenseType = 0; // Exemple : choisir un type de défense par défaut
        Action defenseAction = new Action(targetNode, defenseType);

        System.out.println("Defender reacts to attack on node " + targetNode + " with defense type " + defenseType);
        return defenseAction;
    }

    // Ajoute une expérience au buffer de replay
    public void appendExperience(Experience exp) {
        replayBuffer.add(exp);
    }

    // Étape d'entraînement DQN (non implémentée)
    public void dqnTrainingStep() {
        System.out.println("[INFO] DQN training step not implemented yet.");
    }

    // Mise à jour des valeurs Q
    public void QValuesUpdate(List<StateAction> sas, int reward) {
        System.out.println("[INFO] Defender QValuesUpdate called with reward = " + reward);

        // Exemple de mise à jour de la Q-table
        double alpha = algorithmsParameters.get("montecarlo_epsilon_greedy").get("alpha");
        for (StateAction sa : sas) {
            int state = sa.state;
            Action action = sa.action;
            int node = action.getNode();
            int defenseType = action.getActionType();

            // Mise à jour simple de la Q-table
            qTable[state][defenseType] += alpha * (reward - qTable[state][defenseType]);
        }
    }

    // Réinitialisation (si nécessaire)
    public void reset() {
        replayBuffer.clear();
        qTable = new double[environment.getNumOfNodes()][environment.getValuesPerNode() + 1];
        returns.clear();
    }

    // Vérifie si l'apprentissage est activé
    public boolean isLearning() {
        return learning;
    }

    // Retourne l'algorithme d'apprentissage utilisé
    public String getLearningAlgorithm() {
        return learningAlgorithm;
    }
}