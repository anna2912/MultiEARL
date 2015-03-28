package genetic;

import java.util.ArrayList;
import java.util.List;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;


public final class RLPopulationData<T>
{
    private final T bestCandidate;
    private final double bestCandidateFitness;
    private final double meanFitness;
    private final double fitnessStandardDeviation;
    private final T bestCandidateTarget;
    private final double bestCandidateFitnessTarget;
    private final double meanFitnessTarget;
    private final double fitnessStandardDeviationTarget;
    private final String targetFitnessType;
    private final String fitnessType;
    private final boolean naturalFitness;
    private final int populationSize;
    private final int eliteCount;
    private final int generationNumber;
    private final long elapsedTime;
    private final List<EvaluatedCandidate<T>> population;

    /**
     * @param bestCandidate The fittest candidate present in the population.
     * @param bestCandidateFitness The fitness score for the fittest candidate
     * in the population.
     * @param meanFitness The arithmetic mean of fitness scores for each member
     * of the population.
     * @param fitnessStandardDeviation A measure of the variation in fitness
     * scores.
     * @param naturalFitness True if higher fitness scores are better, false
     * otherwise. 
     * @param populationSize The number of individuals in the population.
     * @param eliteCount The number of candidates preserved via elitism.
     * @param generationNumber The (zero-based) number of the last generation
     * that was processed.
     * @param elapsedTime The number of milliseconds since the start of the
     */
    public RLPopulationData(T bestCandidate,
                          double bestCandidateFitness,
                          double meanFitness,
                          double fitnessStandardDeviation,
                          T bestCandidateTarget,
                          double bestCandidateFitnessTarget,
                          double meanFitnessTarget,
                          double fitnessStandardDeviationTarget,
                          String targetFitnessType,
                          String fitnessType,
                          boolean naturalFitness,
                          int populationSize,
                          int eliteCount,
                          int generationNumber,
                          long elapsedTime,
                          List<EvaluatedCandidate<T>> population)
    {
        this.bestCandidate = bestCandidate;
        this.bestCandidateFitness = bestCandidateFitness;
        this.meanFitness = meanFitness;
        this.fitnessStandardDeviation = fitnessStandardDeviation;
        this.bestCandidateTarget = bestCandidateTarget;
        this.bestCandidateFitnessTarget = bestCandidateFitnessTarget;
        this.meanFitnessTarget = meanFitnessTarget;
        this.fitnessStandardDeviationTarget = fitnessStandardDeviationTarget;
        this.targetFitnessType = targetFitnessType;
        this.fitnessType = fitnessType;
        this.naturalFitness = naturalFitness;
        this.populationSize = populationSize;
        this.eliteCount = eliteCount;
        this.generationNumber = generationNumber;
        this.elapsedTime = elapsedTime;
        this.population = population;
    }


    /**
     * @return The fittest candidate present in the population.
     * @see #getBestCandidateFitness()
     */
    public T getBestCandidate()
    {
        return bestCandidate;
    }


    /**
     * @return The fitness score of the fittest candidate.
     * @see #getBestCandidateFitness()
     */
    public double getBestCandidateFitness()
    {
        return bestCandidateFitness;
    }


    /**
     * Returns the average fitness score of population members.
     * @return The arithmetic mean fitness of individual candidates.
     */
    public double getMeanFitness()
    {
        return meanFitness;
    }


    /**
     * Returns a statistical measure of variation in fitness scores within
     * the population. 
     * @return Population standard deviation for fitness scores.
     */
    public double getFitnessStandardDeviation()
    {
        return fitnessStandardDeviation;
    }


    public T getBestCandidateTarget() {
		return bestCandidateTarget;
	}


	public double getBestCandidateFitnessTarget() {
		return bestCandidateFitnessTarget;
	}


	public double getMeanFitnessTarget() {
		return meanFitnessTarget;
	}


	public double getFitnessStandardDeviationTarget() {
		return fitnessStandardDeviationTarget;
	}


	public String getTargetFitnessType() {
		return targetFitnessType;
	}


	public String getFitnessType() {
		return fitnessType;
	}


	/**
     * Indicates whether the fitness scores are natural or non-natural.
     * @return True if higher fitness scores indicate fitter individuals, false
     * otherwise.
     */
    public boolean isNaturalFitness()
    {
        return naturalFitness;
    }

    
    /**
     * @return The number of individuals in the current population.
     */
    public int getPopulationSize()
    {
        return populationSize;
    }


    /**
     * @return The number of candidates preserved via elitism.
     */
    public int getEliteCount()
    {
        return eliteCount;
    }


    /**
     * @return The number of this generation (zero-based).
     */
    public int getGenerationNumber()
    {
        return generationNumber;
    }


    /**
     * Returns the amount of time (in milliseconds) since the
     * start of the evolutionary algorithm's execution.
     * @return How long (in milliseconds) the algorithm has been running.
     */
    public long getElapsedTime()
    {
        return elapsedTime;
    }
    
    public List<T> getPopulation () {
    	List<T> candidates = new ArrayList<T>();
    	
    	for (EvaluatedCandidate<T> candidate : population) 
    		candidates.add(candidate.getCandidate());
    	
    	return candidates;
    }
}
