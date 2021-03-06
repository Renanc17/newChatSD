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
/**
 * Classe respons�vel por receber as mensagens 
 * dos usu�rios, fornece-las para o servidor
 * e enviar mensagens do servidor para o usu�rio.
 *
 */
public class Worker {

	private ServerSocket serverSocket;
	private int port;
	private String host;
	private int timeout = 2000;
	private User user;
	private ObjectOutputStream output;
	private SimpleMessage sMessage;
	private Socket clientSocket;
	private ObjectInputStream in;
	private Socket rsSocket;
	private Object aux;
	private Sala sala = new Sala();

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

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
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
	 * Abre o servidor e o coloca em espera de requisi��o.
	 */
	public void run() {
		this.open();
		this.handleRequest();
	}

	/**
	 * Metodo que define as configura��es das fun��es do Worker.
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
	 * M�todo inicia o primeiro contato com o Receiver do usu�rio.
	 * 
	 */
	public void handleClient() {
		try {
			aux = in.readObject();
			sMessage = (SimpleMessage) aux;
			user.setNickname(sMessage.getNickname());
			sMessage.setNickname("Server");
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
	 * Envia uma mensagem para o usu�rio.
	 * 
	 * @param ssMessage
	 */
	public void sendMessage(SimpleMessage ssMessage) {
		if (ssMessage != null) {
			try {
				output.writeObject(ssMessage);
				output.flush();
				output.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Fornece as mensagens para o servidor.
	 * 
	 * @return SimpleMessage
	 */
	public SimpleMessage getMessage() {
		SimpleMessage sMessage = null;
		try {
			sMessage = (SimpleMessage) in.readObject();
			sMessage.setNickname(user.getNickname());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			user = null;
			try {
				rsSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("Disconnect client!");
		}
		return sMessage;

	}

	/**
	 * Armazena os dados da sala que o user est�.
	 * 
	 * @param sala
	 */
	public void entraNaSala(Sala sala) {
		this.setSala(sala);
	}

	/**
	 * Fecha a conexao.
	 */
	private void close() {
		try {
			System.out.println("Solicitacao de termino. Fechando conexao.");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
