package musicgame;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author Bruno Pereira
 */
public class PDU {

    private byte ver;
    private byte seg;
    private int lab;
    private byte tipo;
    private byte numCampos;
    private int tamLista;
    private ArrayList<Campo> campos;
    
    

    public PDU(int lab, int tipo) {
        this.ver = 0;
        this.seg = 0;
        this.lab = lab;
        this.tipo = (byte) tipo;
        this.numCampos = 0;
        this.tamLista = 0;
        this.campos = new ArrayList<>();
    }

    public PDU(byte[] bytes) {
        
        this.ver = bytes[0];
        this.seg = bytes[1];
        byte[] l = {bytes[2], bytes[3]};
        this.lab = PDU.byteArrayToInt(l);
        System.out.println("label "+ lab);
        this.tipo = bytes[4];
        this.numCampos = bytes[5];
        byte[] tl = {bytes[6], bytes[7]};
        this.tamLista = PDU.byteArrayToInt(tl);
        this.campos = new ArrayList<>();
        Campo c;
        if (numCampos > 0) {
            for (int i = 8; i < tamLista + 8;) {
                //System.out.println("i: " + i);
                int k = 0;
                int id = bytes[i];
                byte[] sizeL = new byte[]{bytes[i + 1], bytes[i+2]};
                int sizeNew= byteArrayToInt(sizeL);

                //System.out.println("Size: " + sizeL);
                byte[] valor = new byte[sizeNew];
                int j;
                for (j = i + 3; j < sizeNew + i + 3; j++) {
                    valor[k] = bytes[j];
                    k++;
                }
                String v = new String(valor);
                //System.out.println("Valor: " + v);
                c = new Campo(id, v.getBytes());
                this.campos.add(c);
                i += sizeNew + 3;
            }
        }
    }
    
    public static int byteArrayToInt(byte[] b) {
        int res =  b[1] & 0xFF | (b[0] & 0xFF) << 8 ;
        return res;
        
    }

    public static byte[] intToByteArray(int n) {
        byte[] res = new byte[2];

        res[0] = (byte) ((n & 0xFF00) >> 8);
        res[1] = (byte) (n & 0x00FF);
        return res;
    }


    public void addCampo(Campo c) {
        this.campos.add(c);
        this.numCampos++;
        this.tamLista += c.getSize()[0]+c.getSize()[1];
    }
    public Campo getCampo(int ind){
        return campos.get(ind);
    }

 
    public byte[] getBytes() {
        ArrayList<Byte> res = new ArrayList<>();
        byte versao = (byte) this.ver;
        byte seguranca = (byte) this.seg;
        byte[] l = PDU.intToByteArray(this.lab);
        byte t = this.tipo;
        byte nC = this.numCampos;
        res.add(versao);
        res.add(seguranca);
        for (byte b : l) {
            res.add(b);
        }
        res.add(t);
        res.add(nC);
        int soma = 0;

        for (Campo c : campos) {
            soma += c.getSize()[0]+c.getSize()[1] + 3;
        }
     
        byte[] s;
        s = PDU.intToByteArray( soma);
        for(byte b: s){
            res.add(b);
        }
        
        campos.stream().forEach((c) -> {
            byte[] cm = c.getBytes();
            for (byte b : cm) {
                res.add(b);
            }
        });
        byte[] r = new byte[soma + 8];
        int i = 0;
        for (byte b : res) {
            r[i] = b;
            i++;
        }
        
        return r;
         
    }

    public void printBytes() {
        byte[] b = this.getBytes();
        for (byte a : b) {
            System.out.println(a);
        }
    }

    @Override
    public String toString() {
        return "PDU{" + "ver=" + ver + ", seg=" + seg + ", lab=" + lab + ", tipo=" + tipo + ", numCampos=" + numCampos + ", tamLista=" + tamLista + ", campos=" + campos + '}';
    }

}
