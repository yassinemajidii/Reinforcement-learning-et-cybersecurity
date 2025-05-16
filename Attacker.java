import java.util.*;

public class Attacker {
    private Environment environment; // L'environnement de la simulation
    private boolean learning; // Indique si l'attaquant utilise un apprentissage
    private String learningAlgorithm; // Algorithme d'apprentissage utilisé
    private String exploration; // Stratégie d'exploration (e.g., epsilon_greedy, softmax)
    private Map<String, Map<String, Double>> algorithmsParameters; // Paramètres des algorithmes
    private Deque<Experience> replayBuffer; // Buffer pour stocker les expériences
    private Random rng; // Générateur de nombres aléatoires
    private double[][] qTable; // Table Q pour l'apprentissage par renforcement
    private Map<StateAction, List<Double>> returns; // Stocke les retours pour Monte Carlo

    // Constructeur
    public Attacker(Environment environment, boolean learning, String learningAlgorithm, String exploration) {
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
            for (int attackType = 0; attackType <= environment.getValuesPerNode(); attackType++) {
                double value = qTable[node][attackType];
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = new Action(node, attackType);
                }
            }
        }

        return bestAction != null ? bestAction : selectRandomAction(); // Retourne une action aléatoire si aucune action
    }

    // Sélection d'une action aléatoire
    public Action selectRandomAction() {
        List<Action> actions = new ArrayList<>();
        for (int node = 0; node < environment.getNumOfNodes(); node++) {
            for (int attackType = 0; attackType <= environment.getValuesPerNode(); attackType++) {
                Action action = new Action(node, attackType);
                actions.add(action);
            }
        }

        // Vérification si la liste d'actions est vide
        if (actions.isEmpty()) {
            throw new IllegalStateException("No valid actions available for the attacker.");
        }

        return actions.get(rng.nextInt(actions.size()));
    }

    // Mise à jour des retours pour Monte Carlo
    public void updateReturns(StateAction sa, double reward) {
        // Si la paire état-action n'existe pas encore, initialisez une liste
        returns.putIfAbsent(sa, new ArrayList<>());

        // Ajoutez la récompense à la liste
        returns.get(sa).add(reward);
    }

    // Calcul de la moyenne des retours pour une paire état-action
    public double getAverageReturn(StateAction sa) {
        if (!returns.containsKey(sa)) {
            return 0.0; // Retournez 0 si aucune récompense n'est enregistrée
        }

        List<Double> rewards = returns.get(sa);
        return rewards.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // Mise à jour Monte Carlo
    public void monteCarloUpdate(List<StateAction> sas, double alpha, double R) {
        Collections.reverse(sas); // Inverser la liste pour traiter les états dans l'ordre inverse
        for (StateAction sa : sas) {
            // Mettre à jour les retours pour la paire état-action
            updateReturns(sa, R);

            // Calculer la moyenne des retours
            double averageReturn = getAverageReturn(sa);

            // Mettre à jour la Q-table
            int state = sa.state;
            Action action = sa.action;
            int node = action.getNode();
            int attackType = action.getActionType();

            qTable[state][attackType] += alpha * (averageReturn - qTable[state][attackType]);
        }
    }

    // Ajoute une expérience au buffer de replay
    public void appendExperience(Experience exp) {
        replayBuffer.add(exp);
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