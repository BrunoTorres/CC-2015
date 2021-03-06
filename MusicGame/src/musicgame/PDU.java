package musicgame;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Bruno Pereira
 */
public class PDU implements Serializable{

    private byte ver;
    private byte seg;
    private int lab;
    private byte tipo;
    private byte numCampos;
    private int tamLista;
    private ArrayList<Campo> campos;

    public String getMusicPath() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().toString().concat("/musicgame/musica/");
    }

    public String getImagesPath() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().toString().concat("/musicgame/imagens/");
    }

    public String getDesafiosPath() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().toString().concat("/musicgame/desafios/");
    }

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
        this.tipo = bytes[4];
        this.numCampos = bytes[5];
        byte[] tl = {bytes[6], bytes[7]};
        this.tamLista = PDU.byteArrayToInt(tl);
        this.campos = new ArrayList<>();
        Campo c;
        if (numCampos > 0) {
            for (int i = 8; i < tamLista + 8;) {
                int k = 0;
                int id = bytes[i];
                byte[] sizeL = new byte[]{bytes[i + 1], bytes[i + 2]};
                int sizeNew = byteArrayToInt(sizeL);
                byte[] valor = new byte[sizeNew];
                int j;
                for (j = i + 3; j < sizeNew + i + 3; j++) {
                    valor[k] = bytes[j];
                    k++;
                }

                c = new Campo(id, valor);
                this.campos.add(c);
                i += sizeNew + 3;
            }
        }
    }

    public byte getVer() {
        return ver;
    }

    public void setVer(byte ver) {
        this.ver = ver;
    }

    public byte getSeg() {
        return seg;
    }

    public void setSeg(byte seg) {
        this.seg = seg;
    }

    public byte getTipo() {
        return tipo;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public byte getNumCampos() {
        return numCampos;
    }

    public void setNumCampos(byte numCampos) {
        this.numCampos = numCampos;
    }

    public int getTamLista() {
        return tamLista;
    }

    public void setTamLista(int tamLista) {
        this.tamLista = tamLista;
    }

    public static int byteArrayToInt(byte[] b) {
        int res;
        if (b.length > 1) {
            res = b[1] & 0xFF | (b[0] & 0xFF) << 8;
        } else {
            res = b[0];
        }
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
        this.tamLista += c.getSize()[0] + c.getSize()[1];
    }
    
     public void addCampoTcp(Campo c) {
        this.campos.add(c);
        this.numCampos++;
        //this.tamLista += c.getSize()[0] + c.getSize()[1];
    }

    public Campo getCampo(int ind) {
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
            soma += PDU.byteArrayToInt(c.getSize()) + 3;
            //c.getSize()[0]+c.getSize()[1] + 3;
        }

        byte[] s;
        s = PDU.intToByteArray(soma);
        for (byte b : s) {
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

    public static void printBytes(byte[] b) {
        // byte[] b = this.getBytes();
        for (byte a : b) {
            System.out.print(a + "|");
        }
        System.out.println("");

    }

    @Override
    public String toString() {
        return "PDU{" + "ver=" + ver + ", seg=" + seg + ", lab=" + lab + ", tipo=" + tipo + ", numCampos=" + numCampos + ", tamLista=" + tamLista + ", campos=" + campos + '}';
    }

}
