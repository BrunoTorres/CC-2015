package musicgame;


public class Resposta {
    private int nQuestao;
    private int resposta;
    private int pontos;
    private boolean proxima;

    public Resposta(int nQuestao, int resposta, int pontos, boolean proxima) {
        this.nQuestao = nQuestao;
        this.resposta = resposta;
        this.pontos = pontos;
        this.proxima = proxima;
    }

    public int getnQuestao() {
        return nQuestao;
    }

    public void setnQuestao(int nQuestao) {
        this.nQuestao = nQuestao;
    }

    public int getResposta() {
        return resposta;
    }

    public void setResposta(int resposta) {
        this.resposta = resposta;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public boolean isProxima() {
        return proxima;
    }

    public void setProxima(boolean proxima) {
        this.proxima = proxima;
    }

}
