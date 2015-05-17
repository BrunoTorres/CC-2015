package musicgame;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Bruno Pereira
 */
class CompareUsersByPoints implements Comparator<Utilizador>, Serializable {

    public CompareUsersByPoints() {
    }

    @Override
    public int compare(Utilizador o1, Utilizador o2) {
        if (o1.getPontuacao() > o2.getPontuacao()) {
            return 1;
        }
        if (o1.getPontuacao() < o2.getPontuacao()) {
            return -1;
        }
        if (o1.getTempoResposta() > o2.getTempoResposta()) {
            return -1;
        } else {
            return 1;
        }
    }

}
