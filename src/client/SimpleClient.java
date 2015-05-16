package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import entities.SimpleMessage;

public class SimpleClient {
	private Socket clientSocket;
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 9009;
	private String host;
	private int port;
	private ObjectOutputStream output;
	
	public SimpleClient() {
		this.host = SimpleClient.DEFAULT_HOST;
		this.port = SimpleClient.DEFAULT_PORT;
	}

	public SimpleClient(String host) {
		this.host = host;
		this.port = SimpleClient.DEFAULT_PORT;
	}

	public SimpleClient(int port) {
		this.host = SimpleClient.DEFAULT_HOST;
		this.port = port;
	}

	public SimpleClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void Connect() {
		try {
			clientSocket = new Socket(host,port);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			
		} catch (UnknownHostException e) {
			System.out.println("Servidor nao encontrado.");
		} catch (IOException e) {
			System.out.println("Erro na preparacao para ler o socket. O servidor deve estar desconectado.");
		}
	}
	
	public boolean isConnected() {
		boolean result = false;
		if (clientSocket!=null) result = clientSocket.isConnected();
		return result;
	}

	public void SendMessage(SimpleMessage sMessage) {
		try {
			output.writeObject(sMessage);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readMessage() {
		
	}

	public void close() {
		try {
			clientSocket.shutdownOutput();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Erro no fechamento do socket");
		}
	}
}
