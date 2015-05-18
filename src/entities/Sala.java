package entities;

/**
 * Entidade que armazena informações sobre salas criadas. 
 *
 */
public class Sala {

	private int id;
	private String nome;
	private String descSala;
	private User owner;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescSala() {
		return descSala;
	}

	public void setDescSala(String descSala) {
		this.descSala = descSala;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
