package genetic;

import java.util.List;

import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

public final class RLEvolutionUtils {
	private RLEvolutionUtils()
	{
	}

	public static <T> RLPopulationData<T> getPopulationData(
			List<EvaluatedCandidate<T>> evaluatedPopulation,
			List<EvaluatedCandidate<T>> evaluatedTargetFitnessPopulation,
			String tagetFitnessType,
			String fitnessType,
			boolean naturalFitness,
			int eliteCount,
			int iterationNumber,
			long startTime)
	{
		DataSet stats = new DataSet(evaluatedPopulation.size());
		DataSet statsTarget = new DataSet(evaluatedTargetFitnessPopulation.size());
		for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
		{
			stats.addValue(candidate.getFitness());
		}
		for (EvaluatedCandidate<T> candidate : evaluatedTargetFitnessPopulation)
		{
			statsTarget.addValue(candidate.getFitness());
		}
		return new RLPopulationData<T>(
			evaluatedPopulation.get(0).getCandidate(),
			evaluatedPopulation.get(0).getFitness(),
			stats.getArithmeticMean(),
			stats.getStandardDeviation(),
			evaluatedTargetFitnessPopulation.get(0).getCandidate(),
			evaluatedTargetFitnessPopulation.get(0).getFitness(),
			statsTarget.getArithmeticMean(),
			statsTarget.getStandardDeviation(),
			tagetFitnessType,
			fitnessType,
			naturalFitness,
			stats.getSize(),
			eliteCount,
			iterationNumber,
			System.currentTimeMillis() - startTime,
			evaluatedTargetFitnessPopulation);
	}
}
