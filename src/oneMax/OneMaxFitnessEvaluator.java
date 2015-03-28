package oneMax;

import java.util.List;

import genetic.RLFitnessEvaluator;

public class OneMaxFitnessEvaluator extends RLFitnessEvaluator {

	public OneMaxFitnessEvaluator(String targetAction) {
		super(targetAction);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFitness(String candidate,
			List<? extends String> population, String action) {
		if (action.equals(OneMaxFitnessType.ONEMAX.toString()))
			return charMax(candidate, 1);
		return charMax(candidate, 0);
	}

	double charMax (String candidate, int n) {
		int chars = 0;
		for (int i = 0; i < candidate.length(); i++) {
			if (candidate.charAt(i) == ('0' + n))
				chars++;
		}
		return chars;
	}

}
