package gabywald.jade;

import jade.core.behaviours.Behaviour;

public class MyFirstJadeBehaviour extends Behaviour {
	private Boolean finished = false;

	public void action() {
		while (true) {
			// faire quelque chose qui modifie finished
		}
	}

	public boolean done() {
		return finished;
	}
}
