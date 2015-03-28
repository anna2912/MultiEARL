package genetic;

import java.io.PrintWriter;

public class RLEvolutionLogger<T> {

	private PrintWriter pw;
	
	public RLEvolutionLogger (PrintWriter pw) {
		this.pw = pw;
		pw.println("GENNUMBER, TARGETFITNESSTYPE, BESTCANDTARGETFITNESS, FITNESSTYPE, BESTCANDFITNESS, BESTCANDTARGET, BESTCAND");
	}
	
	public void populationUpdate(RLPopulationData<T> data) {
		String separator = ",";
		pw.println(data.getGenerationNumber() + separator  
				+ data.getTargetFitnessType() + separator + data.getBestCandidateFitnessTarget()
				 + separator + data.getFitnessType() + separator + data.getBestCandidateFitness() + separator
				 + data.getBestCandidateTarget() + separator + data.getBestCandidate() + separator + 
				 data.getPopulation().toString());
	}

}
