package model;

import java.io.IOException;
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

    private void regista_para_orcamento(Equipamento equipamento){
        Equipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraOrcamento.put(clone.getNumeroRegisto(),clone);
        this.ultimoNumeroDeRegisto++;
    }

    private void regista_para_reparacao(Equipamento equipamento){
        Equipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraReparacao.put(clone.getNumeroRegisto(),clone);
    }

    private void regista_prontos_entregar(Equipamento equipamento){
        Equipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        prontosAEntregar.put(clone.getNumeroRegisto(),clone);
    }

    public int get_ultimo_numero_de_registo_equipamento(){
        return this.ultimoNumeroDeRegisto;
    }



    private void adicionar_equipamento_para_orcamento(Equipamento equipamento){
        if(!paraOrcamento.containsKey(equipamento.getNumeroRegisto())){
            regista_para_orcamento(equipamento);
        }
    }

    private void adicionar_equipamento_para_reparacao(Equipamento equipamento){
        if(!paraReparacao.containsKey(equipamento.getNumeroRegisto())){
            regista_para_reparacao(equipamento);
        }
    }

    private void adicionar_equipamento_pronto_a_entregar(Equipamento equipamento){
        if(!prontosAEntregar.containsKey(equipamento.getNumeroRegisto())){
            regista_prontos_entregar(equipamento);
        }
    }

    public void adicionar_equipamento(Equipamento equipamento,int local) throws IOException {
        switch (local){
            case 1 -> adicionar_equipamento_para_orcamento(equipamento);
            case 2 -> adicionar_equipamento_para_reparacao(equipamento);
            case 3 -> adicionar_equipamento_pronto_a_entregar(equipamento);

        }
    }


    public void regista_equipamento(Equipamento e, int local) {
        switch (local){
            case 1 -> regista_para_orcamento(e);
            case 2 -> regista_para_reparacao(e);
            case 3 -> regista_prontos_entregar(e);
        }
    }

    public boolean contem_equipamento_para_orcamento(int numeroRegisto){
        return paraOrcamento.containsKey(numeroRegisto);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        paraOrcamento.forEach((k,v)->sb.append(v.toString()).append("@1\n"));
        paraReparacao.forEach((k,v)->sb.append(v.toString()).append("@2\n"));
        prontosAEntregar.forEach((k,v)->sb.append(v.toString()).append("@3\n"));
        return sb.toString();
    }

    public void transferencia_seccao(int num_referencia) {
        if(paraOrcamento.containsKey(num_referencia)){
            Equipamento e = paraOrcamento.get(num_referencia);
            paraOrcamento.remove(num_referencia);
            paraReparacao.put(num_referencia,e);
            System.out.println("DEBUG: TRANSFERIU "+num_referencia+" DA SECÇÃO 1 PARA A SECÇÃO 2");
        }
        else if(paraReparacao.containsKey(num_referencia)){
            Equipamento e = paraReparacao.get(num_referencia);
            paraReparacao.remove(num_referencia);
            prontosAEntregar.put(num_referencia,e);
            System.out.println("DEBUG: TRANSFERIU "+num_referencia+" DA SECÇÃO 2 PARA A SECÇÃO 3");
        }
        else {
            prontosAEntregar.remove(num_referencia);
            System.out.println("DEBUG: TIROU "+num_referencia+" DA SECÇÃO 3");
        }
    }
}

//TODO: busca por cada map com o numRegisto do Equipamento