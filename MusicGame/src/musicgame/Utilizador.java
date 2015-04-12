package musicgame;

import java.io.Serializable;
import java.net.InetAddress;

public class Utilizador implements Serializable {

    private String userName;
    private String alcunha;
    private byte[] pass;
    private InetAddress ip;
    private int port;

    public Utilizador() {
        this.userName = "";
        this.alcunha = "";
        this.pass = null;
        this.port = -1;
        this.ip = null;

    }

    public Utilizador(String nick, String al, byte[] pass, InetAddress ip, int port) {
        this.userName = nick;
        this.alcunha = al;
        this.pass = pass;
        this.port = port;
        this.ip = ip;

    }

    public Utilizador(Utilizador c) {
        this.userName = c.getUserName();
        this.alcunha = c.getAlcunha();
        this.pass = c.getPassword();
        this.port = c.getPort();
        this.ip = c.getIp();

    }

    public byte[] getPass() {
        return pass;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAlcunha(String alcunha) {
        this.alcunha = alcunha;
    }

    public void setPass(byte[] pass) {
        this.pass = pass;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
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
        if (this == o) {
            return true;
        }
        if ((o == null) || (o.getClass() != this.getClass())) {
            return false;
        } else {
            Utilizador c = (Utilizador) o;
            return this.getUserName().equals(c.getUserName());
        }
    }

    private byte[] getPassword() {
        return this.pass;
    }

}
