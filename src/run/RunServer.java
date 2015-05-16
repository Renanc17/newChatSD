package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import server.SimpleServer;

public class RunServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String host = "localhost"; //Endereço do centralServer
		int port = 9000;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Digite seu Ip: ");
		try {
			host = in.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (args.length>0) {
			String sPort = args[0];
			try {
				port = Integer.parseInt(sPort);
				if (port<1024) {
					System.out.println("Porta informada < 1024. Assumindo porta 9009.");
					port = 9009;
				}
			} catch (NumberFormatException e) {
				System.out.println("Erro na porta informada: "+sPort);
				System.out.println("Assumindo porta 9009.");
				port = 9009;
			}
		} else {
			System.out.println("Assumindo porta "+port+".");
		}

		SimpleServer server = new SimpleServer(host,port);
		server.run();
	}

}
