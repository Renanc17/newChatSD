package entities;

import java.io.Serializable;
/**
 * Entidade que armazena uma mensagem simples.
 *
 */
public class SimpleMessage implements Serializable {


	private static final long serialVersionUID = 1L;
	private String nickname;
	private String message = "";

	// Getters and Setters

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
