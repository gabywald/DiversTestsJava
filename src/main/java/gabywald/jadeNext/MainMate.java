package gabywald.jadeNext;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MainMate {

	public static void main(String[] args) {
		Runtime runtime = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.GUI, "true");
		ContainerController containerController = runtime.createMainContainer(profile);

		AgentController agentController01,agentController02;

		try {
			agentController01 = containerController.createNewAgent("Agent01", "gabywald.jade.MyAgent", null);
			agentController01.start();
			agentController02 = containerController.createNewAgent("Agent02", "gabywald.jade.MyFirstJadeAgent", null);
			agentController02.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}


	}

}
