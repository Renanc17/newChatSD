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
	private int salaIdGen = 1; // gerador de id de sala

	/**
	 * Recebe um user novo.
	 */
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
				ultimo = worker.size() - 1;
				listUser.add(worker.get(ultimo).getUser());
				worker.add(new Worker(clientPorts++));
				worker.get(ultimo).run();
			}
		}

	}

	/**
	 * Envia as mensagens dos users pra sala central.
	 */
	private void checkMessages() {
		SimpleMessage ss = null;
		for (Worker w : worker) {

			if (w.getUser() != null) {
				ss = w.getMessage();

				if (ss != null) {
					if (isCommand(ss)) {
						commands(ss);

					} else {
						for (Worker dw : worker) {
							if (dw.getUser() != null && w.getSala().getId() == dw.getSala().getId()) {
								dw.sendMessage(ss);
							}
						}
					}
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
		char verf[] = sMessage.getMessage().trim().toCharArray();
		if (verf[0] != '/') {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Executa o comando.
	 * 
	 * @param sMessage
	 * @return
	 */
	private SimpleMessage commands(SimpleMessage sMessage) {

		String comando = sMessage.getMessage();
		String[] parts = comando.split(" -");
		User user = null;
		for (User u : listUser) {
			if (u.getNickname().equals(sMessage.getNickname()))
				user = u;
		}
		sMessage.setNickname("Server");

		if (parts[0].equalsIgnoreCase("/PM")) {
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (parts[1] != null && parts[2] != null) {
							for (Worker dw : worker) {
								if (dw.getUser() != null) {
									if (parts[1].equals(dw.getUser().getNickname())) {
										sMessage.setNickname(user.getNickname() + "->" + dw.getUser().getNickname());
										sMessage.setMessage(parts[2]);
										w.sendMessage(sMessage);
										dw.sendMessage(sMessage);
									}
								}
							}
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/ListarSalas")) {
			sMessage.setMessage("Lista de Salas");
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						w.sendMessage(sMessage);
						for (Sala s : listSala) {
							sMessage.setMessage("Nome: " + s.getNome() + '\n' + "Descricao: " + s.getDescSala() + '\n'
									+ "Dono: " + s.getOwner().getNickname() + '\n');
							w.sendMessage(sMessage);
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/ListarUsuarios")) {
			int idSala;
			sMessage.setMessage("Lista de Users nesta sala:");
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						idSala = w.getSala().getId();
						w.sendMessage(sMessage);
						for (Worker lw : worker) {
							if (lw.getUser() != null && lw.getSala().getId() == idSala) {
								sMessage.setMessage(lw.getUser().getNickname());
								w.sendMessage(sMessage);
							}
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/CriarSala")) {

			Sala sala = new Sala();
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (parts[1] != null && parts[2] != null) {

							sala.setNome(parts[1]);
							sala.setDescSala(parts[2]);
							sala.setOwner(user);
							sala.setId(salaIdGen++);
							listSala.add(sala);

							w.entraNaSala(sala);
							sMessage.setMessage("Voce criou uma sala");
							w.sendMessage(sMessage);
						} else {
							sMessage.setMessage("Argumentos Invalidos.");
							w.sendMessage(sMessage);
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/AtualizarSala")) {
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getOwner().getNickname().equals(user.getNickname())) {
							if (parts[1] != null && parts[2] != null) {
								for (Sala s : listSala) {
									if (s.getId() == w.getSala().getId()) {
										s.setNome(parts[1]);
										s.setDescSala(parts[2]);
									}
								}
							} else {
								sMessage.setMessage("Argumentos Invalidos.");
								w.sendMessage(sMessage);
							}
						} else {
							sMessage.setMessage("Esta sala nao pertence a voce.");
							w.sendMessage(sMessage);
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/EncerrarSala")) {
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getOwner().getNickname().equals(user.getNickname())) {
							for (Worker aw : worker) {
								if (aw.getUser() != null) {
									if (aw.getSala().getId() == w.getSala().getId()) {
										sMessage.setMessage("A sala em que voce estava foi encerrada pelo dono e voce voltou para a sala principal.");
										aw.sendMessage(sMessage);
										aw.entraNaSala(new Sala());
									}
								}
							}
						} else {
							sMessage.setMessage("Esta sala nao pertence a voce.");
							w.sendMessage(sMessage);
						}
						for (Sala s : listSala) {
							if (w.getSala().getId() == s.getId())
								listSala.remove(s);
						}
					}

				}
			}
		} else if (parts[0].equalsIgnoreCase("/SairDaSala")) {
			int salaId;
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getId() != 0) {
							salaId = w.getSala().getId();
							w.entraNaSala(new Sala());
							sMessage.setMessage("Voce saiu da sala e esta na sala principal.");
							w.sendMessage(sMessage);
							for (Worker aw : worker) {
								if (aw.getUser() != null) {
									if (aw.getSala().getId() == salaId) {
										sMessage.setMessage(user.getNickname() + " saiu desta sala.");
										aw.sendMessage(sMessage);
									}
								}
							}
						} else {
							sMessage.setMessage("Voce nao esta em nenhuma sala.");
							w.sendMessage(sMessage);
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/EntraNaSala")) {
			for (Worker w : worker) {
				if (w.getUser() != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getId() == 0) {
							if (parts[1] != null) {
								for (Sala s : listSala) {
									if (s.getNome().equalsIgnoreCase(parts[1])) {
										w.entraNaSala(s);
									}
								}
							} else {
								sMessage.setMessage("Argumentos Invalidos.");
								w.sendMessage(sMessage);
							}
						} else {
							sMessage.setMessage("Voce precisa sair da sala em que esta antes de entrar em uma nova sala.");
							w.sendMessage(sMessage);
						}
					}
				}
			}

		} else {
			sMessage.setMessage("Comando invalido!");
		}

		return sMessage;
	}

	/**
	 * Começa o ciclo do CentralServer.
	 */
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
