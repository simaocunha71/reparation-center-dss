package model.armazem;

import model.interfaces.IEquipamento;
import model.interfaces.IGestEquipamentos;

import java.util.HashMap;
import java.util.Map;

public class SSEquipamentos implements IGestEquipamentos {
    private Map<Integer, IEquipamento> para_orcamento = new HashMap<>(); //0 //key -> num_reg
    private Map<Integer, IEquipamento> para_reparacao= new HashMap<>(); //1 //key -> num_reg
    private Map<Integer, IEquipamento> prontos_a_entregar= new HashMap<>(); //2 //key -> num_reg
    private int ultimo_numero_registado;


    public SSEquipamentos(){
        this.ultimo_numero_registado = 0;
    }

    private void regista_para_orcamento(IEquipamento equipamento){
        para_orcamento.put(equipamento.get_numero_registo(),equipamento.clone());
        regista_num_reg(equipamento.get_numero_registo());
    }

    private void regista_para_reparacao(IEquipamento equipamento){
        para_reparacao.put(equipamento.get_numero_registo(),equipamento.clone());
        regista_num_reg(equipamento.get_numero_registo());
    }

    private void regista_prontos_entregar(IEquipamento equipamento){
        prontos_a_entregar.put(equipamento.get_numero_registo(),equipamento.clone());
        regista_num_reg(equipamento.get_numero_registo());
    }

    private void regista_num_reg(int num_registo){
        if(this.ultimo_numero_registado < num_registo){
            this.ultimo_numero_registado = num_registo;
        }
    }

    public int get_ultimo_numero_de_registo_equipamento(){
        return this.ultimo_numero_registado;
    }


    public void regista_equipamento(IEquipamento e, int local) {
        switch (local){
            case 1 -> regista_para_orcamento(e);
            case 2 -> regista_para_reparacao(e);
            case 3 -> regista_prontos_entregar(e);
        }
    }

    public void carregar(String linha) {
        String [] split = linha.split("@");
            if(split.length == 2){
                IEquipamento equipamento = new Equipamento();
                equipamento.carregar(split[0]);
                try {
                    int local = Integer.parseInt(split[1]);
                    if (equipamento.valida()) regista_equipamento(equipamento,local);
                }catch (NumberFormatException ignored){}
            }
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        para_orcamento.forEach((k, v)->sb.append(v.salvar()).append("@1\n"));
        para_reparacao.forEach((k, v)->sb.append(v.salvar()).append("@2\n"));
        prontos_a_entregar.forEach((k, v)->sb.append(v.salvar()).append("@3\n"));
        return sb.toString();
    }


    public void transferencia_seccao(int num_registo) {
        if(para_orcamento.containsKey(num_registo)){
            IEquipamento e = get_equipamento_para_orcamento(num_registo);
            remove_seccao_1(num_registo);
            para_reparacao.put(num_registo,e);
        }
        else if(para_reparacao.containsKey(num_registo)){
            IEquipamento e = get_equipamento_para_reparacao(num_registo);
            remove_seccao_2(num_registo);
            prontos_a_entregar.put(num_registo,e);
        }
        else {
            remove_seccao_3(num_registo);
        }
    }

    private void remove_seccao_1(int num_registo){
        para_orcamento.remove(num_registo);
    }

    private void remove_seccao_2(int num_registo){
        para_reparacao.remove(num_registo);
    }

    private void remove_seccao_3(int num_registo){
        prontos_a_entregar.remove(num_registo);
    }


    private boolean contem_equipamento_para_orcamento(int num_registo){
        return para_orcamento.containsKey(num_registo);
    }

    private boolean contem_equipamento_para_reparacao(int num_registo){
        return para_reparacao.containsKey(num_registo);
    }

    private boolean contem_equipamento_pronto_a_entregar(int num_registo){
        return prontos_a_entregar.containsKey(num_registo);
    }

    public boolean contem_equipamento(int num_registo, int seccao){
        switch (seccao){
            case 1 -> {
                return contem_equipamento_para_orcamento(num_registo);
            }
            case 2 -> {
                return contem_equipamento_para_reparacao(num_registo);
            }
            case 3 -> {
                return contem_equipamento_pronto_a_entregar(num_registo);
            }
            default -> {
                return false;
            }
        }
    }

    public IEquipamento get_equipamento(int num_registo) {
        IEquipamento e;
        e = get_equipamento_para_orcamento(num_registo);
        if(e == null) e = get_equipamento_para_reparacao(num_registo);
        if(e == null) e = get_equipamento_pronto_a_entregar(num_registo);
        return e;
    }

    private IEquipamento get_equipamento_para_orcamento(int num_registo){
        if(contem_equipamento_para_orcamento(num_registo))
            return para_orcamento.get(num_registo).clone();
        else return null;
    }

    private IEquipamento get_equipamento_para_reparacao(int num_registo){
        if(contem_equipamento_para_reparacao(num_registo))
            return para_reparacao.get(num_registo).clone();
        else return null;
    }

    private IEquipamento get_equipamento_pronto_a_entregar(int num_registo){
        if(contem_equipamento_pronto_a_entregar(num_registo))
            return prontos_a_entregar.get(num_registo).clone();
        else return null;
    }
}