public class Simulation {
    public static void main(String[] args) {
        // Configuration de l'environnement
        int numOfNodes = 5;
        int valuesPerNode = 3;
        int[][] neighboursMatrix = {
                { 1, 2 }, { 0, 2, 3 }, { 0, 1, 4 }, { 1, 4 }, { 2, 3 }
        };
        int dataNode = 4; // Nœud contenant les données critiques
        int startNode = 0; // Nœud de départ de l'attaquant
        double detectionProbability = 0.2; // Probabilité de détection
        int initialSecurity = 5; // Niveau de sécurité initial

        // Création de l'environnement, de l'attaquant et du défenseur
        Environment env = new Environment(numOfNodes, valuesPerNode, neighboursMatrix, dataNode, startNode,
                detectionProbability, initialSecurity);
        Attacker attacker = new Attacker(env, true, "montecarlo", "epsilon_greedy");
        Defender defender = new Defender(env, true, "montecarlo", "epsilon_greedy");

        // Nombre d'épisodes à simuler
        int numEpisodes = 10;

        // Boucle de simulation
        for (int episode = 1; episode <= numEpisodes; episode++) {
            System.out.println("=== Episode " + episode + " ===");

            // L'attaquant choisit une action
            Action attackerAction = attacker.selectAction(env, episode);
            System.out.println("Attacker action: " + attackerAction);

            // Le défenseur réagit à l'action de l'attaquant
            Action defenderAction = defender.reactToAttack(attackerAction);
            System.out.println("Defender action: " + defenderAction);

            // Vérification du résultat de l'attaque
            boolean attackSuccess = evaluateAttack(attackerAction, defenderAction, env);
            if (attackSuccess) {
                System.out.println("Result: Attack succeeded! Node " + attackerAction.getNode() + " was compromised.");
            } else {
                System.out.println("Result: Attack failed! Node " + attackerAction.getNode() + " was defended.");
            }

            System.out.println();
        }
    }

    /**
     * Évalue si l'attaque a réussi ou échoué.
     *
     * @param attackerAction L'action de l'attaquant.
     * @param defenderAction L'action du défenseur.
     * @param env            L'environnement de la simulation.
     * @return true si l'attaque a réussi, false sinon.
     */
    private static boolean evaluateAttack(Action attackerAction, Action defenderAction, Environment env) {
        // Si le défenseur protège le même nœud que celui attaqué, l'attaque échoue
        if (attackerAction.getNode() == defenderAction.getNode()) {
            return false; // Défense réussie
        }

        // Sinon, l'attaque réussit
        return true;
    }
}