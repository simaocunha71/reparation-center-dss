package model;

import java.util.HashMap;
import java.util.Map;

public class Armazem {
    private Map<Integer, Equipamento> paraOrcamento = new HashMap<>(); //0
    private Map<Integer, Equipamento> paraReparacao = new HashMap<>(); //1
    private Map<Integer, Equipamento> prontosAEntregar = new HashMap<>(); //2
    private int ultimoNumeroDeRegisto;


    public Armazem(){
        this.ultimoNumeroDeRegisto = 0;
        paraOrcamento = new HashMap<>();
        paraReparacao = new HashMap<>();
        prontosAEntregar = new HashMap<>();
    }

    public void regista_para_orcamento(Equipamento equipamento){
        Equipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraOrcamento.put(clone.getNumeroRegisto(),clone);
        this.ultimoNumeroDeRegisto++;
    }

    public int get_ultimo_numero_de_registo_equipamento(){
        return this.ultimoNumeroDeRegisto;
    }



}

//TODO: busca por cada map com o numRegisto do Equipamento