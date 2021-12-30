package model.comparators;

import model.interfaces.IPedido;
import model.pedidos.PedidoExpresso;

import java.util.Comparator;

public class IPedidoComparator implements Comparator<IPedido> {

    public int compare(IPedido o1, IPedido o2) {
        if(o1.getClass().equals(o2.getClass()))
            return o1.get_tempo_registo().compareTo(o2.get_tempo_registo());
        else if(o1.getClass().equals(PedidoExpresso.class))
            return -1;
        else
            return 1;
    }
}
