package model;

import java.util.Map;

public class Armazem {
    private Map<String, Equipamento> paraOrcamento;
    private Map<String, Equipamento> paraReparacao;
    private Map<String, Equipamento> prontosAEntregar;
}

//TODO: busca por cada map com o numRegisto do Equipamento