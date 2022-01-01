
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class BaseAgent extends GuiAgent {

	void register(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	AID getService(String service) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			if (result.length > 0)
				return result[0].getName();
		} catch (Exception fe) {
		}
		return null;
	}

	public AID[] searchDF(String service)
	// ---------------------------------
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);

		SearchConstraints ALL = new SearchConstraints();
		ALL.setMaxResults(new Long(-1));

		try {
			DFAgentDescription[] result = DFService.search(this, dfd, ALL);
			AID[] agents = new AID[result.length];
			for (int i = 0; i < result.length; i++)
				agents[i] = result[i].getName();
			return agents;

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		return null;
	}

	public ArrayList<String> SearchForATypeOfAgent(String agentType) {

		ArrayList<String> agentList = new ArrayList<>();
		System.out.println("Search For : " + agentType);
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			DFService.search(this, dfd);

			AID[] agents = searchDF(agentType);

			System.out.println("Found " + agents.length + " agents.");
			if (agents != null) {
				for (int i = 0; i < agents.length; i++) {
					agentList.add(agents[i].getLocalName());
				}

			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentList;
	}

	public void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub

	}

}
