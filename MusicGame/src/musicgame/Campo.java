package musicgame;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 
 * @author Bruno Pereira
 */
class Campo {
    
    private int id;
    private byte size;
    private String valor;

    public Campo(int id, String valor) {
        this.id = id;
        this.valor = valor;
        this.size = (byte) valor.getBytes().length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public byte getSize(){
        return this.size;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
        this.size = (byte) this.valor.getBytes().length;
    }
    
    public byte[] getBytes(){
        ArrayList<Byte> res = new ArrayList<>();
        res.add((byte) this.id);
        res.add(this.size);
        byte[] v = this.valor.getBytes();
        
        for(byte b : v){
            res.add(b);
        }
        
        int i = 0;
        byte[] r = new byte[res.size()];
        for (byte b : res) {
            r[i] = b;
            i++;
        }
        
        return r;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.valor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Campo other = (Campo) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.valor, other.valor)) {
            return false;
        }
        return true;
    }

}
