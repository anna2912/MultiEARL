package qlearning;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class QAgent implements Agent<String> {
	
	private double learningRate;
	private double discountFactor;
	private int rewardDim;
	private Vector<String> actions;
	private Vector<Vector<Double>> q;
	private double randActionProbability;
	private List<PrintWriter> loggers;
	
	public QAgent (double learningRate, double discountFactor, List<String> actions, double randActionProbability, int rewardDim) {
		this.learningRate = learningRate;
		this.discountFactor = discountFactor;
		this.actions = new Vector<String>();		
		for (int i = 0; i < actions.size(); i++) {
			this.actions.add(actions.get(i));
		}
		this.randActionProbability = randActionProbability;
		this.rewardDim = rewardDim;
		refresh();
		this.loggers = new ArrayList<PrintWriter>();
	}
	
	public void updateQValue (int i, List<Double> reward) {
		Vector<Double> newQ = new Vector<Double>();
		int maxQIndex = maxQIndex();
		if (maxQIndex == -1) {
			Random rand = new Random();
			maxQIndex = rand.nextInt(q.size());
		}
		for (int j = 0; j < rewardDim; j++) {
			newQ.add(q.get(i).get(j) + learningRate * (reward.get(j) + discountFactor * q.get(maxQIndex).get(j)  - q.get(i).get(j)));
		}
		q.set(i, newQ);
	}
	
	public int maxQIndex () {
		boolean comparable = false;
		Vector<Double> maxQ = q.get(0);
		int maxIndex = 0;

		for (int i = 1; i < q.size(); i++) {
			int compRes = compareReward(q.get(i), maxQ);
			if (compRes != 0) {
				comparable = true;
				if (compRes == 1) {
					maxQ = q.get(i);
					maxIndex = i;
					comparable = true;
				}
			}
		}
		
		if (!comparable)
			return -1;
		
		Vector<Integer> maxSet = new Vector<Integer>();
		maxSet.add(maxIndex);
		
		for (int i = 0; i < q.size(); i++) {
			if (i != maxIndex) {
				int compRes = compareReward(q.get(i), maxQ);
				if (compRes == 0) {
					maxSet.add(i);
				}
			}
		}
		
		Random rand = new Random();
		
		return maxSet.get(rand.nextInt(maxSet.size()));
		
	}
	
	public int compareReward (List<Double> r1, List<Double> r2) {
		if (r1.size() != r2.size())
			return 0;
		
		int gr = 0;
		int ls = 0;
		
		for (int j = 0; j < rewardDim; j++) {
			if (r1.get(j) > r2.get(j))
				gr++;
			else if (r1.get(j) < r2.get(j))
				ls++;
		}
		
		if (gr == 0) {
			if (ls == 0)
				return 0;
			return -1;
		}
		
		return 1;
	}

	@Override
	public int learn(Environment<String> environment) {
		int stepCount = 0;
		while (!environment.inFinalState()) {
			Random rand = new Random();
			int curAction = maxQIndex();			
			
			if (curAction == -1 || rand.nextDouble() < this.randActionProbability) {
				curAction = rand.nextInt(actions.size());
			}

			List<Double> curReward = environment.applyAction(actions.get(curAction));
			
			updateQValue(curAction, curReward);
			
			stepCount++;
			
			logStep (stepCount, curAction, curReward);
		}
		return stepCount;
	}

	@Override
	public void refresh() {
		q = new Vector<Vector<Double>>();
		for (int i = 0; i < actions.size(); i++) {
			q.add(new Vector<Double>());
			for (int j = 0; j < rewardDim; j++) {
				q.get(i).add(new Double(0));
			}
		}		
	}

	@Override
	public void addLogger(PrintWriter pw) {
		loggers.add(pw);
		pw.print("STEPNUM, CURACTION, ");
		
		for (int i = 1; i <= rewardDim; i++) {
			pw.print("CURREWARD" + i + ", ");
		}
		
		for (int i = 0; i < actions.size(); i++) {
			pw.print(actions.get(i) + ", ");
			for (int j = 0; j < rewardDim; j++) {
				pw.print("REWARD" + i + "_" + j + ", ");
			}
		}
		pw.println();
	}
	
	private void logStep (int stepNumber, int curAction, List<Double> curReward) {
		
		for (PrintWriter pw : loggers) {
			pw.print(stepNumber + ", " + actions.get(curAction) + ", ");
			
			for (Double reward : curReward) {
				pw.print(reward + ", ");
			}
			
		
			for (int i = 0; i < actions.size(); i++) {
				pw.print(actions.get(i) + ", ");
				for (int j = 0; j < rewardDim; j++) {
					pw.print(q.get(i).get(j) + ", ");
				}
			}
			pw.println();
		}
		
		/*for (PrintWriter pw : loggers) {
		pw.print("Step " + stepNumber + ": action " + actions.get(curAction) + " reward ");
		
		for (Double reward : curReward) {
			pw.print(reward + " ");
		}
		
		pw.println();
		
		for (int i = 0; i < actions.size(); i++) {
			pw.print(actions.get(i) + " ");
			for (int j = 0; j < rewardDim; j++) {
				pw.print(q.get(i).get(j) + " ");
			}
			pw.println();
		}
		pw.println();
		}*/
		
	}

}
