	import jade.core.AID;
	import jade.core.Agent;
	import jade.core.behaviours.TickerBehaviour;
	import jade.lang.acl.ACLMessage;


	public class EntityAgent extends BaseAgent {

		String localName ;
		int creationTime = 0;

		UpdatePosition fastSim = new UpdatePosition(this, 1);
		UpdatePosition normalSim = new UpdatePosition(this, 100);
		UpdateInfo updateInfo = new UpdateInfo(this, 1000);
		CheckForSimModeChange checkSimMode = new CheckForSimModeChange(this, 100);
		int messageCount = 0;




		protected void setup() {

			System.out.println("Entity started");

			localName = getLocalName();

			Object[] args = getArguments();
			if (args != null) {

				 localName = args[0].toString() + "_" + args[1].toString();
				 creationTime = Integer.parseInt(args[2].toString());
				 System.out.println("Agent created successfully : " + localName + " creationTime: " +creationTime);


				 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID("Assignment02GUI", AID.ISLOCALNAME));
				msg.setLanguage("English");
				send(msg);

			}

		}

		public class CheckForSimModeChange extends TickerBehaviour {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 *

			 */

			public CheckForSimModeChange(Agent a, long interval) {
				super(a, interval);
			}

			protected void onTick() {

			}

			public void changeSimMode() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID("MainFrame", AID.ISLOCALNAME));
				// msg.addReceiver(new AID ("AirPlaneAgent",AID.ISLOCALNAME));
				msg.setLanguage("English");
				msg.setContent("simmode" + "_" + 0);
				send(msg);
			}

		}

		public class EvaluateMessages extends TickerBehaviour {

			/**
			 *

			 */

			private static final long serialVersionUID = 657002871747329933L;

			public EvaluateMessages(Agent a, long interval) {
				super(a, interval);
			}

			protected void onTick() {

				ACLMessage msg = receive();

				if (msg != null) {
					messageCount++;

					String content = msg.getContent();

				}
			}

		}

		public class UpdatePosition extends TickerBehaviour {

			/**
			 *

			 */

			private static final long serialVersionUID = 657002871747329933L;

			public UpdatePosition(Agent a, long interval) {
				super(a, interval);
			}

			protected void onTick() {

				// timeCounter++;
			}
		}

		public class UpdateInfo extends TickerBehaviour {

			private static final long serialVersionUID = 657002871747329933L;

			public UpdateInfo(Agent a, long interval) {
				super(a, interval);
			}

			protected void onTick() {

			}

		}

	}
