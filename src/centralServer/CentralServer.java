package centralServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entities.Sala;
import entities.SimpleMessage;
import entities.User;
import worker.Worker;

public class CentralServer {

	private List<Worker> worker = new ArrayList<Worker>();
	private List<Sala> listSala = new ArrayList<Sala>();
	private List<User> listUser = new ArrayList<User>();
	private String host;
	private int clientPorts = 9009;
	private int salaIdGen = 1; // gerador de id de sala

	public CentralServer() {
		this.host = "localhost";
	}

	public CentralServer(String host) {
		this.host = host;
	}

	/**
	 * Recebe um usuário novo e registra em uma List.
	 */
	private void receiveUserConnection() {
		int ultimo;
		if (worker.isEmpty()) {
			worker.add(new Worker(host, clientPorts++));
			worker.get(0).run();
			ultimo = worker.size() - 1;
		} else {
			ultimo = worker.size() - 1;
			if (worker.get(ultimo).getUser() == null) {
				worker.get(ultimo).run();
			} else {
				ultimo = worker.size() - 1;
				listUser.add(worker.get(ultimo).getUser());
				worker.add(new Worker(host, clientPorts++));
				worker.get(ultimo).run();
			}
		}

	}

	/**
	 * Envia as mensagens dos usuários para seus destinos.
	 */
	private void checkMessages() {
		SimpleMessage ss = null;
		for (Worker w : worker) {

			if (w.getUser() != null) {
				ss = w.getMessage();

				if (ss != null) {
					if (isCommand(ss)) {
						try {
							commands(ss);
						} catch (ArrayIndexOutOfBoundsException e) {
							ss.setMessage("Argumento Invalido.");
							w.sendMessage(ss);
						}

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
	 * @return boolean
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
	 */
	private void commands(SimpleMessage sMessage) throws ArrayIndexOutOfBoundsException {

		String comando = sMessage.getMessage();
		String[] parts = comando.split(" -");
		User user = null;
		for (User u : listUser) {
			if (!listUser.isEmpty()) {
				if (u.getNickname().equals(sMessage.getNickname()))
					user = u;
			}
		}
		sMessage.setNickname("Server");

		if (parts[0].equalsIgnoreCase("/PM")) { // Comando para enviar mensagens
												// privadas de um usuário para
												// outro.
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (parts[1] != null && parts[2] != null) {
							for (Worker dw : worker) {
								if (dw.getUser() != null) {
									if (parts[1].equals(dw.getUser().getNickname())) {
										sMessage.setNickname(user.getNickname() + "->" + dw.getUser().getNickname());
										sMessage.setMessage("(PM) " + parts[2]);
										w.sendMessage(sMessage);
										dw.sendMessage(sMessage);
									}
								}
							}
						}
					}
				}
			}

		} else if (parts[0].equalsIgnoreCase("/ListarSalas")) { // Comando para
																// listar todas
																// as salas
																// criadas no
																// servidor.
			sMessage.setMessage("Lista de Salas");
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
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

		} else if (parts[0].equalsIgnoreCase("/ListarUsuarios")) { // Comando
																	// para
																	// listar
																	// todos os
																	// usuários
																	// na mesma
																	// sala o
			int idSala; // usuário que solicitou o comando.
			sMessage.setMessage("Lista de Users nesta sala:");
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
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

		} else if (parts[0].equalsIgnoreCase("/CriarSala")) { // Comando para
																// criar sala
																// baseado em
																// parametros.

			Sala sala = new Sala();
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
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

		} else if (parts[0].equalsIgnoreCase("/AtualizarSala")) { // Comando
																	// para
																	// atualizar
																	// informações
																	// de uma
																	// sala
																	// criada
			for (Worker w : worker) { // baseada em parametros.
				if (w.getUser() != null && user != null) {
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

		} else if (parts[0].equalsIgnoreCase("/EncerrarSala")) { // Comando para
																	// encerrar
																	// a sala em
																	// que o
																	// usuário
																	// está,
																	// apenas
			int salaId; // se o mesmo for dono da sala.
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						salaId = w.getSala().getId();
						if (salaId != 0) {
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
							for (Iterator<Sala> iter = listSala.listIterator(); iter.hasNext();) {
								Sala s = iter.next();
								if (salaId == s.getId())
									iter.remove();
							}
						} else {
							sMessage.setMessage("Voce esta na sala principal.");
							w.sendMessage(sMessage);
						}
					}
				}
			}
		} else if (parts[0].equalsIgnoreCase("/SairDaSala")) { // Comando para
																// sair da sala
																// em que o
																// usuário está
																// atualmente.
			int salaId;
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getId() != 0) {
							salaId = w.getSala().getId();
							w.entraNaSala(new Sala());
							sMessage.setMessage("Voce saiu da sala em que estava e esta na sala principal.");
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

		} else if (parts[0].equalsIgnoreCase("/EntrarNaSala")) { // Comando para
																	// o usuário
																	// entrar em
																	// uma sala
																	// criada,
																	// apenas
			for (Worker w : worker) { // se o mesmo não estiver em sala alguma.
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (w.getSala().getId() == 0) {
							if (parts[1] != null) {
								for (Sala s : listSala) {
									if (s.getNome().equalsIgnoreCase(parts[1])) {
										w.entraNaSala(s);
										sMessage.setMessage("Voce entrou na sala: " + s.getNome());
										w.sendMessage(sMessage);
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
		} else if (parts[0].equalsIgnoreCase("/AlterarNome")) { // Comando para
																// alterar o
																// nome do
																// usuário.
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						if (parts[1] != null) {
							user.setNickname(parts[1]);
							for (Sala s : listSala) {
								if (s.getOwner().getNickname().equals(w.getUser().getNickname())
										&& s.getOwner().getHost().equals(w.getUser().getHost())) {
									s.setOwner(user);
								}
							}
							for (Worker aw : worker) {
								if (aw.getUser() != null) {
									if (w.getUser().getNickname().equals(aw.getUser().getNickname())) {
										sMessage.setMessage("Esse nome ja esta sendo usado.");
										w.sendMessage(sMessage);
									} else {
										w.setUser(user);
									}
								}
							}
						} else {
							sMessage.setMessage("Argumentos Invalidos.");
							w.sendMessage(sMessage);
						}
					}
				}
			}
		} else if (parts[0].equalsIgnoreCase("/MinhasInfo")) { // Comando para o
																// usuário
																// verificar
																// suas
																// informações.
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						sMessage.setMessage("-------Suas Informacoes--------" + '\n' + "Nome: " + user.getNickname()
								+ '\n' + "IP: " + user.getHost() + '\n');
						w.sendMessage(sMessage);
						if (w.getSala().getId() == 0) {
							sMessage.setMessage("Sala Atual: Sala Principal");
						} else {
							sMessage.setMessage("Sala Atual: " + w.getSala().getNome() + '\n' + "Descricao: "
									+ w.getSala().getDescSala() + '\n' + "Dono: "
									+ w.getSala().getOwner().getNickname());
						}
						w.sendMessage(sMessage);
					}
				}
			}

		} else {
			for (Worker w : worker) {
				if (w.getUser() != null && user != null) {
					if (w.getUser().getNickname().equals(user.getNickname())
							&& w.getUser().getHost().equals(user.getHost())) {
						sMessage.setMessage("Comando invalido!");
						w.sendMessage(sMessage);
					}
				}
			}
		}

	}

	/**
	 * Começa o ciclo do Servidor.
	 */
	public void startCycle() {
		System.out.println("Iniciando Ciclo!");
		while (true) {
			receiveUserConnection();
			checkMessages();
		}

	}

}
