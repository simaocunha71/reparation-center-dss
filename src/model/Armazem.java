package model;

import java.util.HashMap;
import java.util.Map;

public class Armazem {
    private Map<String, Equipamento> paraOrcamento = new HashMap<>();
    private Map<String, Equipamento> paraReparacao = new HashMap<>();
    private Map<String, Equipamento> prontosAEntregar = new HashMap<>();


    public Armazem(){

    }

    public void adiciona_para_orcamento(Equipamento equipamento){
        Equipamento clone = new Equipamento(equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraOrcamento.put(clone.getNumeroRegisto(),clone);
    }



}

//TODO: busca por cada map com o numRegisto do Equipamento