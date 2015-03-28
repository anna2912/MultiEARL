package qlearning;

import java.util.List;

public interface Environment<A> {
	
	public List<Double> applyAction(A action);

	public boolean inFinalState();
	
}
