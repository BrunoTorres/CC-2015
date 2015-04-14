package musicgame;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 
 * @author Bruno Pereira
 */
class Campo {
    
    private int id;
    private byte[] size;
    private byte[] valor;

    public Campo(int id, byte[] valor) {
        this.id = id;
        this.valor = valor;
        this.size = PDU.intToByteArray(valor.length);
    }
    
     

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public byte[] getSize(){
        return this.size;
    }

    public byte[] getValor() {
        return valor;
    }

    public void setValor(byte[] valor) {
        this.valor = valor;
        this.size = PDU.intToByteArray(valor.length);
    }
    
    public byte[] getBytes(){
        ArrayList<Byte> res = new ArrayList<>();
        res.add((byte) this.id);
         for (byte b : this.size) {
            res.add(b);
        }
        
        //res.add(this.size);
        byte[] v = this.valor;
        
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
