package qlearning;

import java.io.PrintWriter;

public interface Agent<A> {

	public int learn(Environment<A> environment);

	public void refresh();
	
	public void addLogger(PrintWriter pw);

}
