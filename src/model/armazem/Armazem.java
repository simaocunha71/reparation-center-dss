package model.armazem;

import model.interfaces.IEquipamento;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Armazem {
    private Map<Integer, IEquipamento> paraOrcamento = new HashMap<>(); //0 //key -> num_reg
    private Map<Integer, IEquipamento> paraReparacao = new HashMap<>(); //1 //key -> num_reg
    private Map<Integer, IEquipamento> prontosAEntregar = new HashMap<>(); //2 //key -> num_reg
    private int ultimoNumeroDeRegisto;


    public Armazem(){
        this.ultimoNumeroDeRegisto = 0;
        paraOrcamento = new HashMap<>();
        paraReparacao = new HashMap<>();
        prontosAEntregar = new HashMap<>();
    }

    private void regista_para_orcamento(IEquipamento equipamento){
        IEquipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraOrcamento.put(clone.getNumeroRegisto(),clone);
        regista_num_ref(equipamento.getNumeroRegisto());
    }

    private void regista_para_reparacao(IEquipamento equipamento){
        IEquipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        paraReparacao.put(clone.getNumeroRegisto(),clone);
        regista_num_ref(equipamento.getNumeroRegisto());
    }

    private void regista_prontos_entregar(IEquipamento equipamento){
        IEquipamento clone = new Equipamento(equipamento.getNifCliente(), equipamento.getNumeroRegisto(), equipamento.getModelo(), equipamento.getDescricao());
        prontosAEntregar.put(clone.getNumeroRegisto(),clone);
        regista_num_ref(equipamento.getNumeroRegisto());
    }

    private void regista_num_ref(int num_ref){
        if(this.ultimoNumeroDeRegisto < num_ref){
            this.ultimoNumeroDeRegisto = num_ref;
        }
    }

    public int get_ultimo_numero_de_registo_equipamento(){
        return this.ultimoNumeroDeRegisto;
    }



    private void adicionar_equipamento_para_orcamento(IEquipamento equipamento){
        if(!paraOrcamento.containsKey(equipamento.getNumeroRegisto())){
            regista_para_orcamento(equipamento);
        }
    }

    private void adicionar_equipamento_para_reparacao(IEquipamento equipamento){
        if(!paraReparacao.containsKey(equipamento.getNumeroRegisto())){
            regista_para_reparacao(equipamento);
        }
    }

    private void adicionar_equipamento_pronto_a_entregar(IEquipamento equipamento){
        if(!prontosAEntregar.containsKey(equipamento.getNumeroRegisto())){
            regista_prontos_entregar(equipamento);
        }
    }

    public void adicionar_equipamento(IEquipamento equipamento,int local) throws IOException {
        switch (local){
            case 1 -> adicionar_equipamento_para_orcamento(equipamento);
            case 2 -> adicionar_equipamento_para_reparacao(equipamento);
            case 3 -> adicionar_equipamento_pronto_a_entregar(equipamento);

        }
    }

    public void regista_equipamento(IEquipamento e, int local) {
        switch (local){
            case 1 -> regista_para_orcamento(e);
            case 2 -> regista_para_reparacao(e);
            case 3 -> regista_prontos_entregar(e);
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        paraOrcamento.forEach((k,v)->sb.append(v.toString()).append("@1\n"));
        paraReparacao.forEach((k,v)->sb.append(v.toString()).append("@2\n"));
        prontosAEntregar.forEach((k,v)->sb.append(v.toString()).append("@3\n"));
        return sb.toString();
    }


    public void transferencia_seccao(int num_ref) {
        if(paraOrcamento.containsKey(num_ref)){
            IEquipamento e = getEquipamentoParaOrcamento(num_ref);
            remove_seccao_1(num_ref);
            paraReparacao.put(num_ref,e);
        }
        else if(paraReparacao.containsKey(num_ref)){
            IEquipamento e = getEquipamentoParaReparacao(num_ref);
            remove_seccao_2(num_ref);
            prontosAEntregar.put(num_ref,e);
        }
        else {
            remove_seccao_3(num_ref);
        }
    }

    private void remove_seccao_1(int num_ref){
        paraOrcamento.remove(num_ref);
    }

    private void remove_seccao_2(int num_ref){
        paraReparacao.remove(num_ref);
    }

    private void remove_seccao_3(int num_ref){
        prontosAEntregar.remove(num_ref);
    }


    public boolean contem_equipamento_para_orcamento(int numeroRegisto){
        return paraOrcamento.containsKey(numeroRegisto);
    }

    public boolean contem_equipamento_para_reparacao(int numeroRegisto){
        return paraReparacao.containsKey(numeroRegisto);
    }

    public boolean contem_equipamento_pronto_a_entregar(int numeroRegisto){
        return prontosAEntregar.containsKey(numeroRegisto);
    }

    public IEquipamento getEquipamento(int num_ref) {
        IEquipamento e = null;
        e = getEquipamentoParaOrcamento(num_ref);
        if(e == null) e = getEquipamentoParaReparacao(num_ref);
        if(e == null) e = getEquipamentoProntoAEntregar(num_ref);
        return e;
    }

    private IEquipamento getEquipamentoParaOrcamento(int num_ref){
        if(contem_equipamento_para_orcamento(num_ref))
            return paraOrcamento.get(num_ref).clone();
        else return null;
    }

    private IEquipamento getEquipamentoParaReparacao(int num_ref){
        if(contem_equipamento_para_reparacao(num_ref))
            return paraReparacao.get(num_ref).clone();
        else return null;
    }

    private IEquipamento getEquipamentoProntoAEntregar(int num_ref){
        if(contem_equipamento_pronto_a_entregar(num_ref))
            return prontosAEntregar.get(num_ref).clone();
        else return null;
    }
}