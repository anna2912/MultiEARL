package leadingOnes;

import java.util.List;

import genetic.RLFitnessEvaluator;

public class LOnesFitnessEvaluator extends RLFitnessEvaluator {

	public LOnesFitnessEvaluator(String targetAction) {
		super(targetAction);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFitness(String candidate,
			List<? extends String> population, String action) {
		if (action.equals(LOnesFitnessType.ONEMAX.toString()))
			return oneMax(candidate);
		return leadingOnes(candidate);
	}

	double oneMax (String candidate) {
		int ones = 0;
		for (int i = 0; i < candidate.length(); i++) {
			if (candidate.charAt(i) == '1')
				ones++;
		}
		return ones;
	}
	
	double leadingOnes (String candidate) {
		int zero = candidate.length();
		for (int i = 0; i < candidate.length(); i++) {
			if (candidate.charAt(i) == '0') {
				zero = i;
				break;
			}
		}
		return zero;
	}
}
