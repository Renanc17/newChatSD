package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import entities.SimpleMessage;

/**
 * Classe responsável pela função Sender do Usuário.
 *
 */
public class SimpleClient {
	private Socket clientSocket;
	private String host;
	private int port;
	private ObjectOutputStream output;
	

	public SimpleClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Cria uma conexão com o Servidor.
	 */
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
	
	/**
	 * Verifica se o usuário está connectado.
	 * @return boolean
	 */
	public boolean isConnected() {
		boolean result = false;
		if (clientSocket!=null) result = clientSocket.isConnected();
		return result;
	}
	
	/**
	 * Envia a mensagem do usuário para o servidor.
	 * @param sMessage
	 */
	public void SendMessage(SimpleMessage sMessage) {
		try {
			output.writeObject(sMessage);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fecha a conexão com o servidor.
	 */
	public void close() {
		try {
			clientSocket.shutdownOutput();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Erro no fechamento do socket");
		}
	}
}
