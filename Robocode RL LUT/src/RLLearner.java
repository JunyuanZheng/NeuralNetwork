
public class RLLearner {
	public static final double LearningRate = 0.1;
	public static final double DiscountRate = 0.9;
	public static final double ExploitationRate = 0.3;
	private int lastState;
	private int lastAction;
	private boolean first = true;
	private RLQTable table;

	public RLLearner(RLQTable table) {
		this.table = table;
	}

	public void learn(int state, int action, double reinforcement) {
		// System.out.println("Reinforcement: " + reinforcement);
		if (first)
			first = false;
		else {
			double oldQValue = table.getQValue(lastState, lastAction);
			double newQValue = (1 - LearningRate) * oldQValue
					+ LearningRate * (reinforcement + DiscountRate * table.getMaxQValue(state));
			table.setQValue(lastState, lastAction, newQValue);
		}
		lastState = state;
		lastAction = action;
	}

	public void learnSarsa(int state, int action, double reinforcement) {
		if (first)
			first = false;
		else {
			double oldQValue = table.getQValue(lastState, lastAction);
			double newQValue = (1 - LearningRate) * oldQValue
					+ LearningRate * (reinforcement + DiscountRate * table.getQValue(state, selectAction(state)));
			table.setQValue(lastState, lastAction, newQValue);
		}
		lastState = state;
		lastAction = action;
	}

	public int selectAction(int state) {
		int selectedAction = -1;

		// Explore
		if (Math.random() < ExploitationRate) {
			selectedAction = -1;
		} else {
			selectedAction = table.getBestAction(state);
		}
		// Select random action if all qValues == 0 or exploring.
		if (selectedAction == -1) {
			selectedAction = (int) (Math.random() * Action.NumRobotActions);
		}
		return selectedAction;
	}
}
