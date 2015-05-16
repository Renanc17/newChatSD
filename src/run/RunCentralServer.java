package run;

import centralServer.CentralServer;


public class RunCentralServer {

	public static void main(String[] args) {
		
		CentralServer cs = new CentralServer();
		cs.startCycle();
	}

}
