package genetic;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public abstract class RLFitnessEvaluator implements FitnessEvaluator<String>{
	
	protected String targetAction;
	protected String currentAction;

	public RLFitnessEvaluator (String targetAction) {
		this.targetAction = targetAction;
		this.currentAction = targetAction;
	}
	
	public String getTargetAction() {
		return targetAction;
	}

	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}

	public String getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(String currentAction) {
		this.currentAction = currentAction;
	}

	@Override
	public double getFitness(String candidate, List<? extends String> population) {
		return getFitness(candidate, population, currentAction);
	};
	
	public abstract double getFitness(String candidate, List<? extends String> population, String action);

	@Override
	public boolean isNatural() {
		return true;
	}

}
