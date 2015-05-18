package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import centralServer.CentralServer;

/**
 * Inicia o Servidor.
 *
 */
public class RunCentralServer {

	public static void main(String[] args) {
		String host = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Digite o Ip do CentralServer: ");
		try {
			host = in.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		CentralServer cs = new CentralServer(host);
		System.out.println("Iniciando Central Server!");
		cs.startCycle();
	}

}
