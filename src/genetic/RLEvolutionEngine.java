package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import qlearning.Environment;

public class RLEvolutionEngine<T> extends AbstractEvolutionEngine<T> implements Environment<String> {

	private final Set<RLEvolutionLogger<String>> observers = new CopyOnWriteArraySet<RLEvolutionLogger<String>>();
	private final Random rng;
	private final CandidateFactory<T> candidateFactory;
	private final RLFitnessEvaluator fitnessEvaluator;
	private List<TerminationCondition> satisfiedTerminationConditions;
	private int currentGenerationIndex;
	private long startTime;
	private List<String> rewardTypes;
	private List<EvaluatedCandidate<T>> evaluatedTargetFitnessPopulation;
	private List<EvaluatedCandidate<T>> evaluatedPopulation;
	private int populationSize;
	private int eliteCount;
	private TerminationCondition conditions;
	private final EvolutionaryOperator<T> evolutionScheme;
    private final SelectionStrategy<? super T> selectionStrategy;
    private Double bestTargetFitness;
    private Double meanTargetFitness;
    private Double prevBestTargetFitness;
    private Double prevMeanTargetFitness;
    private Double prevMinD;
    private Double minD;
    private TerminationCondition genCondition;

	@SuppressWarnings("unchecked")
	public RLEvolutionEngine(CandidateFactory<T> candidateFactory,
			RLFitnessEvaluator fitnessEvaluator, Random rng, List<String> rewardTypes,
			int populationSize, int eliteCount, TerminationCondition conditions,
			EvolutionaryOperator<T> evolutionScheme, SelectionStrategy<? super T> selectionStrategy) {
		super(candidateFactory, (FitnessEvaluator<? super T>) fitnessEvaluator, rng);
		this.candidateFactory = candidateFactory;
		this.fitnessEvaluator = fitnessEvaluator;
		this.rng = rng;
		this.currentGenerationIndex = 0;
		this.rewardTypes = rewardTypes;
		this.populationSize = populationSize;
		this.eliteCount = eliteCount;
		this.conditions = conditions;
		this.evolutionScheme = evolutionScheme;
		this.selectionStrategy = selectionStrategy;
		this.genCondition =  new GenerationCount(300000);
	}


	@Override
	public List<Double> applyAction(String action) {
		fitnessEvaluator.setCurrentAction(action);
		evolvePopulation();
		prevBestTargetFitness = bestTargetFitness;
		bestTargetFitness = calculateBestTargetFitness();
		prevMeanTargetFitness = meanTargetFitness;
		meanTargetFitness = calculateMeanTargetFitness();
		prevMinD = minD;
		minD = calculateMinDistance();
		List<Double> rewards = new ArrayList<Double>();
		for (String rewardType : rewardTypes) {
			rewards.add(calculateReward(rewardType));
		}
		return rewards;
	}
	
	private Double calculateReward(String rewardType) {
		if (rewardType.equals("MEAN")) {
			//current mean target fitness - prev mean target fitness
			return meanTargetFitness - prevMeanTargetFitness;
		}
		if (rewardType.equals("MS1")) {
			return Math.signum(bestTargetFitness - prevBestTargetFitness);
		}
		if (rewardType.equals("MIND")) {
			return minD - prevMinD;
		}
		//current best target fitness - prev best target fitness
		return bestTargetFitness - prevBestTargetFitness;
	}
	
	private Double calculateBestTargetFitness() {
		return evaluatedTargetFitnessPopulation.get(0).getFitness();
	}
	
	private Double calculateMeanTargetFitness() {
		double meanFitness = 0;
		for (EvaluatedCandidate<T> candidate : evaluatedTargetFitnessPopulation) {
			meanFitness += candidate.getFitness();
		}
		return meanFitness / evaluatedTargetFitnessPopulation.size();
	}
	
	private Double calculateMinDistance() {
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < evaluatedTargetFitnessPopulation.size() - 1; i++) {
			double curDist = evaluatedTargetFitnessPopulation.get(i).getFitness() - evaluatedTargetFitnessPopulation.get(i+1).getFitness();
			if (curDist < minDist)
				minDist = curDist;
		}
		return minDist;
	}


	@Override
	public boolean inFinalState() {
		if (satisfiedTerminationConditions == null || satisfiedTerminationConditions.isEmpty())
			return false;
		return true;
	}


	@Override
	protected List<EvaluatedCandidate<T>> nextEvolutionStep(
			List<EvaluatedCandidate<T>> evaluatedPopulation, int eliteCount, Random rng) {


		List<T> population = new ArrayList<T>(evaluatedPopulation.size());
		// First perform any elitist selection.
		List<T> elite = new ArrayList<T>(eliteCount);
		Iterator<EvaluatedCandidate<T>> iterator = evaluatedTargetFitnessPopulation.iterator();
		while (elite.size() < eliteCount)
		{
			elite.add(iterator.next().getCandidate());
		}
		// Then select candidates that will be operated on to create the evolved
		// portion of the next generation.
		population.addAll(selectionStrategy.select(evaluatedPopulation,
					fitnessEvaluator.isNatural(),
					evaluatedPopulation.size() - eliteCount,
					rng));
		// Then evolve the population.
		population = evolutionScheme.apply(population, rng);
		// When the evolution is finished, add the elite to the population.
		population.addAll(elite);
		this.evaluatedPopulation = evaluatePopulation(population);
		String curAction = fitnessEvaluator.getCurrentAction();
		fitnessEvaluator.setCurrentAction(fitnessEvaluator.getTargetAction());
		evaluatedTargetFitnessPopulation = evaluatePopulation(population);
		fitnessEvaluator.setCurrentAction(curAction);
		return this.evaluatedPopulation;
	}
	
	public void initActions()
    {

		satisfiedTerminationConditions = null;
		currentGenerationIndex = 0;
		startTime = System.currentTimeMillis();
		List<T> population = candidateFactory.generateInitialPopulation(populationSize,
				Collections.<T>emptySet(),
                rng);
		evaluatedPopulation = evaluatePopulation(population);
		evaluatedTargetFitnessPopulation = evaluatePopulation(population);
		bestTargetFitness = calculateBestTargetFitness();
		meanTargetFitness = calculateMeanTargetFitness();
		minD = calculateMinDistance();
		
		EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
		EvolutionUtils.sortEvaluatedPopulation(evaluatedTargetFitnessPopulation, fitnessEvaluator.isNatural());
		RLPopulationData<T> data = RLEvolutionUtils.getPopulationData(evaluatedPopulation, 
								  evaluatedTargetFitnessPopulation,
								  fitnessEvaluator.getTargetAction(),
								  fitnessEvaluator.getCurrentAction(),
			                      fitnessEvaluator.isNatural(),
			                      eliteCount,
			                      currentGenerationIndex,
			                      startTime);
		satisfiedTerminationConditions = EvolutionUtils.shouldContinue(
												EvolutionUtils.getPopulationData(evaluatedTargetFitnessPopulation,
								                fitnessEvaluator.isNatural(),
								                eliteCount,
								                currentGenerationIndex,
								                startTime), 
							                conditions, genCondition);
		notifyPopulationChange(data);

	}
	

	public void evolvePopulation()
	{
	
		if (satisfiedTerminationConditions == null) {
			currentGenerationIndex++;
			evaluatedPopulation = nextEvolutionStep(evaluatedPopulation, eliteCount, rng);
			
			EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
			EvolutionUtils.sortEvaluatedPopulation(evaluatedTargetFitnessPopulation, fitnessEvaluator.isNatural());
			RLPopulationData<T> data = RLEvolutionUtils.getPopulationData(evaluatedPopulation, 
					  evaluatedTargetFitnessPopulation,
					  fitnessEvaluator.getTargetAction(),
					  fitnessEvaluator.getCurrentAction(),
					  fitnessEvaluator.isNatural(),
					  eliteCount,
					  currentGenerationIndex,
					  startTime);
			satisfiedTerminationConditions = EvolutionUtils.shouldContinue(
					EvolutionUtils.getPopulationData(evaluatedTargetFitnessPopulation,
	                fitnessEvaluator.isNatural(),
	                eliteCount,
	                currentGenerationIndex,
	                startTime), 
                conditions, genCondition);
			notifyPopulationChange(data);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void notifyPopulationChange(RLPopulationData<T> data)
    {
        for (RLEvolutionLogger<String> observer : observers)
        {
            observer.populationUpdate((RLPopulationData<String>) data);
        }
    }
	
	public void addEvolutionObserver(RLEvolutionLogger<String> observer)
	{
		observers.add(observer);
	}
	
	public void removeEvolutionObserver(RLEvolutionLogger<String> observer)
	{
		observers.remove(observer);
	}


	public List<String> getRewardTypes() {
		return rewardTypes;
	}

}
