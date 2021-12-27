package model.comparators;

import model.interfaces.IPedido;

import java.util.Comparator;

public class IPedidoComparator implements Comparator<IPedido> {

    public int compare(IPedido o1, IPedido o2) {
        return o1.getTempoRegisto().compareTo(o2.getTempoRegisto());
    }
}
