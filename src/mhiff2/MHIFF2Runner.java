package mhiff2;

import genetic.RLEvolutionEngine;
import genetic.RLEvolutionLogger;
import genetic.RLFitnessEvaluator;
import genetic.RLRewardTypes;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import qlearning.Agent;
import qlearning.QAgent;

public class MHIFF2Runner {
	
	public static void run(int runNumb) throws FileNotFoundException 
	{
		double learningRate = 0.75;
		double discountFactor = 0.05;
		double randActionProbability = 0.01;
		int populationSize = 100;
		int eliteCount = 5;
		
		List<String> actions = new Vector<String>();
		for (MHIFF2FitnessType action : MHIFF2FitnessType.values()) {
			actions.add(action.toString());
		}
		
		List<String> rewardTypes = new Vector<String>();
		//rewardTypes.add(RLRewardTypes.SIMPLE.toString());
		rewardTypes.add(RLRewardTypes.MEAN.toString());
		rewardTypes.add(RLRewardTypes.MS1.toString());
		//rewardTypes.add(RLRewardTypes.MIND.toString());
		
		Agent<String> agent = new QAgent(learningRate, discountFactor, actions, randActionProbability, rewardTypes.size());
		
		char[] chars = new char[2];
		chars[0] = '0';
		chars[1] = '1';
		CandidateFactory<String> candidateFactory = new StringFactory(chars, 64);
		RLFitnessEvaluator fitnessEvaluator = new MHIFF2FitnessEvaluator(MHIFF2FitnessType.HIFF.toString());
		
		
		TerminationCondition conditions = new TargetFitness(448.0, true);
		List<EvolutionaryOperator<String>> operators = new ArrayList<EvolutionaryOperator<String>>();
		operators.add(new StringMutation(chars, new Probability(0.01)));
		EvolutionPipeline<String> pipeline = new EvolutionPipeline<String>(operators);
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(new Probability(0.9));
		Random rng = new MersenneTwisterRNG();
		RLEvolutionEngine<String> engine = new RLEvolutionEngine<String>(candidateFactory, fitnessEvaluator, rng, rewardTypes,
				populationSize, eliteCount, conditions, pipeline, selectionStrategy);
		
		PrintWriter pwEA = new PrintWriter("MHIFF2_" + populationSize + "_" + eliteCount + "_" + "target_value2" + "_MEAN_MS1_" + runNumb);
		
		engine.addEvolutionObserver(new RLEvolutionLogger<String>(pwEA));
		engine.initActions();
		agent.learn(engine);
		pwEA.flush();
		pwEA.close();

	}
}
