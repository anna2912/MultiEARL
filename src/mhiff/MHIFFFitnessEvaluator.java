package mhiff;

import java.util.List;

import genetic.RLFitnessEvaluator;

public class MHIFFFitnessEvaluator extends RLFitnessEvaluator{

	public MHIFFFitnessEvaluator(String targetAction) {
		super(targetAction);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFitness(String candidate,
			List<? extends String> population, String action) {
		if (action.equals(MHIFFFitnessType.ZEROHIFF.toString()))
			return fn(candidate, 0);
		if (action.equals(MHIFFFitnessType.ONEHIFF.toString()))
			return fn(candidate, 1);
		return f(candidate);
	}
	
	int fn (String candidate, int n) {
		if (candidate.length() == 1) {
			if (candidate.charAt(0) == ('0' + n))
				return 1;
			return 0;
		}
		String left = candidate.substring(0, candidate.length() / 2);
		String right = candidate.substring(candidate.length() / 2, candidate.length());
		
		int b = candidate.length();
		
		for (int i = 0; i < candidate.length(); i++){
			if (candidate.charAt(i) != ('0' + n)){
				b = 0;
				break;
			}
		}
		
		return b + fn(left, n) + fn(right, n);
	}
	
	int f (String candidate) {
		if (candidate.length() == 1) {
			return 1;
		}
		String left = candidate.substring(0, candidate.length() / 2);
		String right = candidate.substring(candidate.length() / 2, candidate.length());
		
		int b = candidate.length();
		
		for (int i = 1; i < candidate.length(); i++){
			if (candidate.charAt(i) != candidate.charAt(0)){
				b = 0;
				break;
			}
		}
		
		return b + f(left) + f(right);
	}

}
