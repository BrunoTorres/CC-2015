package musicgame;

import java.io.Serializable;


public class Utilizador implements Serializable{
    
	private String userName;	
	private String alcunha;
        private byte[] pass;
	

	public Utilizador() {
		this.userName = "";
		this.alcunha = "";
                this.pass =null;

	}

	public Utilizador(String nick, String pw, byte[] pass) {
		this.userName = nick;
		this.alcunha = pw;
                this.pass=pass;
	
	}

	public Utilizador(Utilizador c) {
		this.userName = c.getUserName();
		this.alcunha = c.getAlcunha();
                this.pass=c.getPassword();
		
	}
	
	
	
	
	public String getUserName() {
		return this.userName;
	}

	public String getAlcunha() {
		return this.alcunha;
	}

	public Utilizador clone() {
		return new Utilizador(this);
	}

	public String toString() {
		StringBuilder s = new StringBuilder("### Utilizador ###\n");
		s.append("Utilizador: ").append(this.getUserName());
		s.append("Alcunha ").append(this.getAlcunha());
		return s.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		else {
			Utilizador c = (Utilizador) o;
			return this.getUserName().equals(c.getUserName());
		}
	}

    private byte[] getPassword() {
        return this.pass;
    }

}
