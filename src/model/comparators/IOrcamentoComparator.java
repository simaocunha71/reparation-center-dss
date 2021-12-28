package model.comparators;

import model.Orcamento;
import model.interfaces.IPedido;

import java.util.Comparator;

public class IOrcamentoComparator implements Comparator<Orcamento>  {

    public int compare(Orcamento o1, Orcamento o2) {
        return o1.getDataConfirmacao().compareTo(o2.getDataConfirmacao());
    }
}
