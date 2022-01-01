import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.time.LocalTime;
import java.util.ArrayList;

public class SecondAssignment extends Agent {

    short GUI_WIDTH = 1800;
    short GUI_HEIGHT = 600;
    short GUI_XSTART = 0;
    short GUI_YSTART = 0;

    int partHairCutCount = 0;
    int partBeardShavingCount = 0;
    int totalCustomer = 0;
    int creationTimeHairCut = 0;
    int processHairCutTime = 0;
    int processBeardShavingTime = 0;
    int creationTimeBeardShaving = 0;
    int leftCustomerWithoutProcessCount = 0;
    final int SIMDURATION = 4 * 60; // 4 hours
    int simClock = 0;

    ArrayList<JEntity> entityList = new ArrayList<JEntity>();

    GUIFrame guiframe;
    private AgentController t1 = null;

    ContainerController container1 = null;

    public void createNextEntity(String entityType, int creationTime, int count) {

        System.out.println("CreationTime of " + entityType + " is " + creationTime);
        addBehaviour(new WakerBehaviour(this, creationTime) {
            protected void handleElapsedTimeout() {

                System.out.println("Handle time elapsed  " + creationTime + " entity: " + entityType);

                createEntity(entityType, creationTime, count);

            }
        });
    }

    //Hair Cut costumers
    OneShotBehaviour createPartHairCutEntity = new OneShotBehaviour() {

        public void action() {
            BetaDistribution betaDistribution = new BetaDistribution(0.896, 1.03);
            // values are got by previous assignment
            double randomValue = 24.5 + (11 * betaDistribution.sample());
            creationTimeHairCut += Math.round(randomValue);
            ++partHairCutCount;

            createNextEntity("HairCut", (int) creationTimeHairCut * 1000, partHairCutCount);
            addBehaviour(processHairCut);
            System.out.println(" inter arrival time for the next Hair Cut Entity : " + randomValue + " creation Time Hair Cut"
                    + creationTimeHairCut + " Hair Cut Count " + partHairCutCount);
        }
    };

    OneShotBehaviour processHairCut = new OneShotBehaviour() {
        public void action() {
            UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(31.5, 40.5);
            double randomValue = uniformRealDistribution.sample();
            processHairCutTime = (int) (creationTimeHairCut + (Math.round(randomValue)));
            totalCustomer -= 1;

            createNextEntity("HairCutLeave", (int) processHairCutTime * 1000, partHairCutCount);
            System.out.println("Hair cut process will take " + randomValue + " minutes.   ");
        }
    };

    // Creates a BeardShaving costumer
    OneShotBehaviour createPartBeardShavingEntity = new OneShotBehaviour() {
        public void action() {
            BetaDistribution betaDistribution = new BetaDistribution(0.978, 1.06);
            // values are got by previous assignment
            double randomValue = 34.5 + (11 * betaDistribution.sample());
            System.out.println(" Inter arrival time for the next Beard Shaving Entity : " + randomValue + "    ");
            creationTimeBeardShaving += Math.round(randomValue);
            ++partBeardShavingCount;
            createNextEntity("BeardShaving", creationTimeBeardShaving * 1000, partBeardShavingCount);
            addBehaviour(processBeardShaving);
        }
    };

    OneShotBehaviour processBeardShaving = new OneShotBehaviour() {

        public void action() {

            UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(10.5, 18.5);
            double randomValue = uniformRealDistribution.sample();
            processBeardShavingTime = (int) (creationTimeBeardShaving + Math.round(randomValue));
            totalCustomer -= 1;
            createNextEntity("BeardShavingLeave", (int) processBeardShavingTime * 1000, partBeardShavingCount);
            System.out.println(" Beard Shaving process will take " + randomValue + " minutes.   ");

        }
    };

    // Left Customer Without Process
    OneShotBehaviour leftCustomerWithoutProcess = new OneShotBehaviour() {
        public void action() {
            createNextEntity("LeftCustomer", (int) simClock * 1000, leftCustomerWithoutProcessCount);
            System.out.println(" Customer Left");

        }
    };

    OneShotBehaviour runSimulation = new OneShotBehaviour() {

        public void action() {
            System.out.println("creationTime for Hair Cut " + creationTimeHairCut + " creationTime for Beard Shaving " + creationTimeBeardShaving);
        }
    };


    public void createEntity(String agentType, int creationTime, int partCount) {
        Object[] dtss = new Object[3];
        dtss[0] = agentType;
        dtss[1] = partCount;
        dtss[2] = creationTime;
        String entityName = agentType + "_" + (partCount) + "_" + (creationTime);
        startAgent("EntityContainer", "localhost", entityName, "EntityAgent", dtss);
        entityList.add(new JEntity(entityName, agentType, partCount, creationTime));
        System.out.println("A new " + agentType + "is created at " + LocalTime.now());
    }


    public void startAgent(String containerName, String host, String agentName, String agentClass, Object[] dtss) {
        // Get the JADE runtime interface (singleton)
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        // create a non-main agent container
        ContainerController container = null;
        if (container1 == null) {
            container = runtime.createAgentContainer(profile);
            container1 = container;
        } else {
            container = container1;
        }

        try {
            AgentController ag = container.createNewAgent(agentName, agentClass, dtss);
            ag.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    public void setup() {

        initiateGUIFrame();
        // Ticker for simulation timing
        addBehaviour(simTimer);
        addBehaviour(runSimulation);

        addBehaviour(new EvaluateMessages(this, 100));


    }

    public void initiateGUIFrame() {
        guiframe = new GUIFrame();
        guiframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiframe.setLocationRelativeTo(null);
        guiframe.setBounds(GUI_XSTART, GUI_YSTART, GUI_WIDTH, GUI_HEIGHT);

        guiframe.setVisible(true);

        guiframe.setLayout(new BorderLayout());
        guiframe.setName("Hair Designer Simulation");
        guiframe.setTitle("Hair Designer Simulation");
        guiframe.setVisible(true);

        guiframe.createBufferStrategy(2);
        guiframe.addMouseMotionListener(guiframe);
        guiframe.addMouseListener(guiframe);

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
        }

    }

    public class CheckAgents extends TickerBehaviour {

        /**
         *
         */

        private static final long serialVersionUID = 657002871747329933L;

        public CheckAgents(Agent a, long interval) {
            super(a, interval);
        }

        public void infoBox(String infoMessage, String titleBar) {
            JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
        }

        protected void onTick() {

        }
    }


    TickerBehaviour simTimer = new TickerBehaviour(this, 1000) {
        protected void onTick() {
            if (simClock % 20 == 0)
                System.out.println("sim Time:" + simClock + " simDuration ");
            if (simClock < SIMDURATION) {
                if(totalCustomer >2){
                    addBehaviour(leftCustomerWithoutProcess);
                    leftCustomerWithoutProcessCount+=1;
                }else {
                    if (simClock == creationTimeHairCut) {
                        addBehaviour(createPartHairCutEntity);
                        totalCustomer += 1;
                    }
                    if (simClock == creationTimeBeardShaving) {
                        addBehaviour(createPartBeardShavingEntity);
                        totalCustomer += 1;
                    }
                }
                simClock++;
                guiframe.updateScenery();
                }

            }

    };

    WakerBehaviour info = new WakerBehaviour(this, 10) {
        protected void handleElapsedTimeout() {

        }
    };

    class RunSimulation implements ActionListener {

        public void actionPerformed(ActionEvent e) {

        }
    }

    public class GUIFrame extends JFrame implements MouseMotionListener, MouseListener {

        Graphics g;
        private BufferStrategy strategy;
        int mX, mY;
        int HOR_OFFSET = 0;
        int VER_OFFSET = 0;


        public void mouseMoved(MouseEvent me) {

        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseClicked(MouseEvent e) {

            mX = (int) e.getPoint().getX() - HOR_OFFSET; // +HOR_OFFSET;
            mY = (int) e.getPoint().getY() - VER_OFFSET; // +VER_OFFSET;

            // if (drawMode == 0)
        }

        public void mouseDragged(MouseEvent me) {
            mouseMoved(me);
        }
        // build poup menu

        /**
         * returns the (by the canvas accelerated) graphics context to which all
         * other classes should draw to
         */
        public Graphics2D getGraphicsContext() {
            return (Graphics2D) (g = (Graphics2D) strategy.getDrawGraphics());
        }

        public void render() {

            BufferStrategy bs = this.getBufferStrategy();
            if (bs == null) {
                createBufferStrategy(3);
                return;
            }

            Graphics g = bs.getDrawGraphics();

            // g.setColor(Color.black);
            // g.drawRect(0, 0, getWidth(), getHeight());

            updateScenery();
            g.dispose();
            bs.show();
        }

        public void updateScenery() {
            // System.out.println("Update scenery");
            BufferStrategy bs = this.getBufferStrategy();
            if (bs == null) {
                this.createBufferStrategy(3);
                return;
            }
            Graphics g = bs.getDrawGraphics();

            g.clearRect(0, 0, guiframe.getWidth(), guiframe.getHeight());
            g.setColor(Color.BLACK);

            g.drawString("Simulation Time: " + simClock, 50, 60);

            int HOROFF = 200;
            int VEROFF = 150;
            int stepWidth = 10;
            int lineLength = 20;

            for (int i = 0; i < simClock; i++)
                g.drawLine(HOROFF + (i * stepWidth), VEROFF, HOROFF + (i * stepWidth), VEROFF + lineLength);

            int i = 1;
            for (JEntity entity : entityList) {
                g.drawString(entity.entityName, 50, 80 + i++ * 15);
                if (entity.entityType.compareTo("HairCut") == 0)
                    g.drawString(("HC" + entity.entityOrder + " E"), HOROFF + entity.creationTime * stepWidth / 1000, VEROFF - 30);

                if (entity.entityType.compareTo("HairCutLeave") == 0)
                    g.drawString(("HC" + entity.entityOrder + " L"), HOROFF + entity.creationTime * stepWidth / 1000, VEROFF - 30);

                if (entity.entityType.compareTo("BeardShaving") == 0)
                    g.drawString(("BC" + entity.entityOrder + " E"), HOROFF + entity.creationTime * stepWidth / 1000,
                            VEROFF + 30 + lineLength + (entity.entityOrder % 4) * 15);

                if (entity.entityType.compareTo("BeardShavingLeave") == 0)
                    g.drawString(("BC" + entity.entityOrder + " L"), HOROFF + entity.creationTime * stepWidth / 1000,
                            VEROFF + 30 + lineLength + (entity.entityOrder % 4) * 15);

                if (entity.entityType.compareTo("LeftCustomer") == 0)
                    g.drawString(("Customer Left"), HOROFF + entity.creationTime * stepWidth / 1000,
                            VEROFF + 100 + lineLength + (entity.entityOrder % 4) * 15);
                //LeftCustomer

            }

            g.dispose();
            bs.show();

        }

    }
}
