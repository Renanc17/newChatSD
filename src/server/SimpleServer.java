package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import entities.SimpleMessage;

public class SimpleServer {

	private ServerSocket serverSocket;
	private int port;
	private String host = "192.168.1.44";
	private int timeout = 60000; // 1min
	private SimpleMessage sMessage;
	private Socket clientSocket;
	private ObjectInputStream in;
	private boolean disconnect = true;
	private Object aux;

	public SimpleServer() {
		this.port = 9000;
	}

	public SimpleServer(int port) {
		this.port = port;
	}

	public SimpleServer(String host, int port) {
		this.host = host;
		this.port = port;
	}


	/**
	 * Abre o socket e define o timeout.
	 */
	private void open() {
		try {
			serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
			serverSocket.setSoTimeout(timeout);
			System.out.println("Servidor ativo e aguardando conexao.");
		} catch (IOException e) {
			System.out.println("Porta Ocupada, criando o socket na porta:" + port);
		}
	}

	/**
	 * Abre o servidor e o coloca em espera de requisições.
	 */
	public void run() {
		this.open();
		this.handleRequest();
		this.receiverMode();
	}

	/**
	 * Metodo que determina o inicio de atendimento a uma requisicao.
	 */
	private void handleRequest() {

		if ((serverSocket != null) && (serverSocket.isBound())) {
			try {
				clientSocket = serverSocket.accept();
				if (clientSocket != null) {
					System.out.println("Cliente conectado:" + clientSocket.getInetAddress().getHostAddress() + ":"
							+ clientSocket.getPort());
					disconnect = false;
					in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
					handleClient();
				}
			} catch (SocketTimeoutException timeoutException) {
				System.out.println("Terminando por time-out");
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gerencia uma requisicao atendida.
	 * 
	 * @param clientSocket
	 *            - Socket produzido no accept.
	 */
	public void handleClient() {
		try {
			aux = in.readObject();
			sMessage = (SimpleMessage) aux;
			System.out.println(sMessage.getNickname() + ": " + sMessage.getMessage());
		} catch (IOException e) {
				disconnect = true;
			System.out.println("Disconnect client!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException NP) {

		}
		sMessage = null;
	}

	private void receiverMode() {
		while (disconnect == false)
			handleClient();
	}

	/**
	 * Fecha a conexao.
	 */
	private void close() {
		try {
			System.out.println("Solicitacao de termino. Fechando o servidor.");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
