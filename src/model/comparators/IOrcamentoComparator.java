package model.comparators;

import model.interfaces.IOrcamento;

import java.util.Comparator;

public class IOrcamentoComparator implements Comparator<IOrcamento>  {

    public int compare(IOrcamento o1, IOrcamento o2) {
        return o1.getDataConfirmacao().compareTo(o2.getDataConfirmacao());
    }
}
