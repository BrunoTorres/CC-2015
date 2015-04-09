package musicgame;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author Bruno Pereira
 */
public class PDU {

    private byte ver;
    private byte seg;
    private short lab;
    private byte tipo;
    private byte numCampos;
    private int tamLista;
    private ArrayList<Campo> campos;

    public PDU(short lab, int tipo) {
        this.ver = 0;
        this.seg = 0;
        this.lab = lab;
        this.tipo = (byte) tipo;
        this.numCampos = 0;
        this.tamLista = 0;
        this.campos = new ArrayList<>();
    }

    public PDU(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(b);
        }
        System.out.println();
        this.ver = bytes[0];
        this.seg = bytes[1];
        byte[] l = {bytes[2], bytes[3]};
        this.lab = this.byteArrayToInt(l);
        this.tipo = bytes[4];
        this.numCampos = bytes[5];
        byte[] tl = {bytes[6], bytes[7]};
        this.tamLista = this.byteArrayToInt(tl);
        this.campos = new ArrayList<>();
        Campo c;
        if (numCampos > 0) {
            for (int i = 8; i < tamLista + 8;) {
                System.out.println("i: " + i);
                int k = 0;
                int id = bytes[i];
                byte sizeL = bytes[i + 1];
                
                System.out.println("Size: " + sizeL);
                byte[] valor = new byte[sizeL];
                int j;
                for (j = i + 2; j < sizeL + i + 2; j++) {
                    valor[k] = bytes[j];    
                    k++;
                }
                String v = new String(valor);
                //System.out.println("Valor: " + v);
                c = new Campo(id, v);
                this.campos.add(c);
                i += sizeL+2;
            }
        }
    }

    public void addCampo(Campo c) {
        this.campos.add(c);
        this.numCampos++;
        System.out.println("B4: " + this.tamLista);
        this.tamLista += c.getSize();
        System.out.println("AFT: " + this.tamLista);
    }

    private short byteArrayToInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getShort();
    }

    private byte[] intToByteArray(short n) {
        byte[] res = new byte[2];
        
        res[0] = (byte) ((n & 0xFF00) >> 8);
        res[1] = (byte) (n & 0x00FF);
        
        for(byte b : res){
            System.out.print(b + "|");
        }
        System.out.println();
        return res;
    }

    public byte[] getBytes() {
        ArrayList<Byte> res = new ArrayList<>();
        byte versao = (byte) this.ver;
        byte seguranca = (byte) this.seg;
        byte[] l = this.intToByteArray(this.lab);
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
            System.out.println("Vai somar, a soma estava a: " + soma);
            soma += c.getSize() + 2;
            System.out.println("O valor do tamanho é: " + c.getSize());
            System.out.println("Já somou ficou a: " + soma);
        }
        System.out.println("SOMA: " + soma);
        byte[] s;
        s = this.intToByteArray((short) soma);
        
        for(byte b : s){
            res.add(b);
            System.out.print(b + "|");
        }
        System.out.println();
        
        campos.stream().forEach((c) -> {
            byte[] cm = c.getBytes();
            for (byte b : cm) {
                res.add(b);
            }
        }); // byte[] label= new Byte()
        //label.
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
