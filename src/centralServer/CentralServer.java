package centralServer;

import java.util.ArrayList;
import java.util.List;

import entities.Sala;
import entities.SimpleMessage;
import entities.User;
import worker.Worker;

public class CentralServer {

	private List<Worker> worker = new ArrayList<Worker>();
	private List<Sala> listSala = new ArrayList<Sala>();
	private List<User> listUser = new ArrayList<User>();
	private int clientPorts = 9009;

	private void receiveUserConnection() {
		int ultimo;
		if (worker.isEmpty()) {
			worker.add(new Worker(clientPorts++));
			worker.get(0).run();
			ultimo = worker.size() - 1;
		} else {
			ultimo = worker.size() - 1;
			if (worker.get(ultimo).getUser() == null) {
				worker.get(ultimo).run();
			} else {
				listUser.add(worker.get(ultimo).getUser());
				worker.add(new Worker(clientPorts++));
				worker.get(ultimo).run();
			}
		}

	}

	private void checkMessages() {
		SimpleMessage ss = null;
		for (Worker w : worker) {
			if (w.getUser() != null) {
				ss = w.getMessage();
				if (isCommand(ss)) {
					for (Worker dw : worker) {
						if (dw.getUser() != null) {
							dw.sendMessage(ss);
						}
					}
				} else {
					w.sendMessage(commands(w.getMessage()));
				}
			}
		}
	}

	/**
	 * Verifica se a mensagem é um comando.
	 * 
	 * @param message
	 * @return
	 */
	private boolean isCommand(SimpleMessage sMessage) {
		if (sMessage.getMessage().trim().equals(""))
			return false;
		char verf[] = sMessage.getMessage().toCharArray();
		if (verf[0] != '/') {
			return true;
		} else {
			return false;
		}
	}

	private SimpleMessage commands(SimpleMessage sMessage) {

		if (sMessage.getMessage().equalsIgnoreCase("/PM")) {

		} else if (sMessage.getMessage().equalsIgnoreCase("/ListarSalas")) {

		} else if (sMessage.getMessage().equalsIgnoreCase("/ListarUsuarios")) {

		} else if (sMessage.getMessage().equalsIgnoreCase("/CriarSala")) {
//			Sala sala = new Sala();
			sMessage.setNickname("Server");
			sMessage.setMessage("Voce criou uma sala");
//			cria a sala

		} else if (sMessage.getMessage().equalsIgnoreCase("/AtualizarSala")) {

		} else if (sMessage.getMessage().equalsIgnoreCase("/EncerrarSala")) {

		} else if (sMessage.getMessage().equalsIgnoreCase("/Sair")) {

		} else {
			sMessage.setNickname("Server");
			sMessage.setMessage("Comando invalido!");
		}

		return sMessage;
	}

	public void startCycle() {
		System.out.println("Iniciando Ciclo!");
		while (true) {
			receiveUserConnection();
			checkMessages();
		}

	}

	public static void main(String[] args) {

		CentralServer cs = new CentralServer();
		System.out.println("Iniciando Central Server!");
		cs.startCycle();

	}

}
