import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;


public class MainContainer {

	int agentNumber = 0;
	int centralNumber = 0;
	int incendiarioNumber = 0;

	Runtime rt;
	ContainerController container;

	public MainContainer(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.initMainContainerInPlatform("localhost", "9888", "MainContainer");

	}
	
	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "false");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath, Object[] args) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, args);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startAgenteCentral(Mapa mapa){
		this.startAgentInPlatform("Central " + centralNumber++, "AgenteCentral", new Object[] {mapa});
	}

	public void startAgenteDrone(int i, Mapa mapa, Posicao posicao){
		this.startAgentInPlatform("Agente "+ agentNumber++, "Drone", new Object[] {mapa, posicao});
	}

	public void startAgenteCamiao(int i, Mapa mapa, Posicao posicao){
		this.startAgentInPlatform("Agente "+ agentNumber++, "Camiao", new Object[] {mapa, posicao});
	}

	public void startAgenteAeronave(int i, Mapa mapa, Posicao posicao){
		this.startAgentInPlatform("Agente "+ agentNumber++, "Aeronave", new Object[] {mapa, posicao});
	}

	public void startIncendiario(Mapa mapa) {
		this.startAgentInPlatform("Incendiario " + incendiarioNumber ++, "AgenteIncendiario",new Object[] {mapa});
	}

	public void startInterface(Mapa mapa) {
		this.startAgentInPlatform("Interface", "AgenteInterface",new Object[] {mapa});
	}
}