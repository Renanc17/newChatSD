package run;

import client.SimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import entities.SimpleMessage;


public class RunClient {

	public static void main(String[] args) {
		int port = 9009; // Porta para conexao com centralServer
		String host = "localhost"; // Endereço do centralServer
		boolean firstConnect = true;
		SimpleMessage sMessage = new SimpleMessage();

		// A linha a seguir define um leitor de teclado
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("Digite seu nickname: ");
		try {
			sMessage.setNickname(in.readLine());
			sMessage.setMessage("Bem vindo " + sMessage.getNickname());
			System.out.print("Endereco do centralServer: ");
			host = in.readLine();
			System.out.print("Porta de acesso ao centralServer: ");
			port = Integer.parseInt(in.readLine());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Crio o SimpleClient que conversa com o nosso servidor
		SimpleClient client = new SimpleClient(host, port);
		client.Connect();
		
		if (client.isConnected()) {
			System.out.println("Cliente conectado.");
			while (!sMessage.getMessage().trim().equalsIgnoreCase("CLOSE")) {
				try {
					
					if(firstConnect){
					client.SendMessage(sMessage);	
					firstConnect = false;
					}
					
					System.out.print("Entre com um comando:");
					sMessage.setMessage(in.readLine());
					client.SendMessage(sMessage);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			client.close();
		} else
			System.out.println("Falha - Cliente nao conectado.");
	}
}
