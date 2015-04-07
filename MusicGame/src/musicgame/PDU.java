package musicgame;

import java.util.ArrayList;

/**
 * 
 * @author Bruno Pereira
 */
public class PDU {
    
    private char ver;
    private char seg;
    private String lab;
    private String tipo;
    private char numCampos;
    private ArrayList<Campo> campos;

   
       public PDU( String lab, String tipo) {
        this.ver = '0';
        this.seg = '0';
        this.lab = lab;
        this.tipo = tipo;
        this.numCampos = '0';
        this.campos= new ArrayList<>();
    }

    public void addCampo(Campo c) {
        this.campos.add(c);
        //this.numCampos++;
    }
    
    public byte[] getBytes(){
        byte versao = (byte) ver;
        byte seguranca = (byte) seg;
       // byte[] label= new Byte()
        //label.
        
      
    }
    
    
    

}
