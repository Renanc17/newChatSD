package worker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import entities.Sala;
import entities.SimpleMessage;
import entities.User;

public class Worker {

	private ServerSocket serverSocket;
	private int port;
	private String host = "192.168.1.44";
	private int timeout = 2000;
	private User user;
	private ObjectOutputStream output;
	private SimpleMessage sMessage;
	private Socket clientSocket;
	private ObjectInputStream in;
	private Socket rsSocket;
	private Object aux;
	private Sala sala;

	public Worker() {
		this.port = 9009;
	}

	public Worker(int port) {
		this.port = port;
	}

	public Worker(String host, int port) {
		this.host = host;
		this.port = port;
	}

	// Getters and Setters

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
				port++;
			System.out.println("Porta Ocupada, criando o socket na porta:" + port);
		}
	}

	/**
	 * Abre o servidor e o coloca em espera de requisições.
	 */
	public void run() {
		this.open();
		this.handleRequest();
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
					user = new User();
					user.setHost(clientSocket.getInetAddress().getHostAddress());
					in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
					rsSocket = new Socket(user.getHost(), 9000);
					output = new ObjectOutputStream(rsSocket.getOutputStream());
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
			user.setNickname(sMessage.getNickname());
			sendMessage(sMessage);

		} catch (IOException e) {
			user = null;
			System.out.println("Disconnect client!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException NP) {

		}
		sMessage = null;
	}
	
	/**
	 * Envia uma mensagem para o user.
	 * @param ssMessage
	 */
	public void sendMessage(SimpleMessage ssMessage) {
		try {
			output.writeObject(ssMessage);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Obtem a mensagem do user.
	 * @return
	 */
	public SimpleMessage getMessage() {
		SimpleMessage sMessage = null;
		try {
			sMessage = (SimpleMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sMessage;

	}
	
	/**
	 * Armazena os dados da sala que o user est�.
	 * @param sala
	 */
	public void entraNaSala(Sala sala){
		this.sala = sala;
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
