package model;

import model.interfaces.Carregavel;
import model.interfaces.IPedido;

import java.time.LocalDateTime;

public class Orcamento implements Carregavel {
    private int num_ref;
    private boolean confirmado = false;
    private PlanoDeTrabalho planoDeTrabalho;
    private LocalDateTime dataConfirmacao;



    public Orcamento(PlanoDeTrabalho planoDeTrabalho){
        this.num_ref = planoDeTrabalho.get_num_referencia();
        this.planoDeTrabalho = planoDeTrabalho.clone();
        this.dataConfirmacao = null;
    }

    //TODO: ver clone do pedido
    public Orcamento(int num_ref, IPedido pedido, boolean confirmado, LocalDateTime dataConfirmacao){
        this.num_ref = num_ref;
        this.planoDeTrabalho = new PlanoDeTrabalho(pedido);
        this.confirmado = confirmado;
        this.dataConfirmacao = dataConfirmacao;
    }

    public int get_num_ref(){
        return num_ref;
    }

    public IPedido get_pedido_plano(){
        return planoDeTrabalho.get_pedido();
    }


    public void confirma(){
        this.confirmado = true;
        this.dataConfirmacao = LocalDateTime.now();
        recalcula_orcamento();
    }

    public void desconfirma(){
        this.confirmado = false;
        this.dataConfirmacao = null;
    }

    private void recalcula_orcamento(){
        planoDeTrabalho.recalcula_estimativas();
    }

    public boolean getConfirmado(){return this.confirmado;}

    public float getCustoEstimado() {
        return planoDeTrabalho.getCustoEstimado();
    }

    public float getDuracaoEstimada() {
        return planoDeTrabalho.getDuracaoEstimada();
    }


    public void carregar(String string) {
        planoDeTrabalho.carregar(string);
    }


    public boolean valida() {
        return planoDeTrabalho.valida();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(num_ref).append(";");
        if(confirmado) sb.append(1 + ";");
        else sb.append(0+";");
        if(dataConfirmacao != null) sb.append(dataConfirmacao);
        sb.append("#").append(planoDeTrabalho.toString());
        return sb.toString();
    }

    public float get_custo_gasto(){
        return planoDeTrabalho.calcula_custo_gasto();
    }

    public float get_tempo_gasto(){
        return planoDeTrabalho.calcula_tempo_gasto();
    }

    public float orcamento_gasto(){
        return planoDeTrabalho.orcamento_gasto();
    }

    public boolean ultrapassou120PorCentoOrcamento(){
        return planoDeTrabalho.ultrapassou120PorCentoOrcamento();
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    //TODO: clone orcamento
    public Orcamento clone(){
        return this;
    }
}
