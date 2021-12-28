package model;

import model.excecoes.JaExistenteExcecao;
import model.interfaces.Carregavel;
import model.interfaces.IPedido;
import model.pedidos.PedidoExpresso;

import java.util.List;

public class Orcamento implements Carregavel {
    private int num_ref;
    private boolean confirmado = false;
    private PlanoDeTrabalho planoDeTrabalho;



    public Orcamento(PlanoDeTrabalho planoDeTrabalho){
        this.num_ref = planoDeTrabalho.get_num_referencia();
        this.planoDeTrabalho = planoDeTrabalho.clone();
    }

    //TODO: ver clone do pedido
    public Orcamento(int num_ref, IPedido pedido, boolean confirmado){
        this.num_ref = num_ref;
        this.planoDeTrabalho = new PlanoDeTrabalho(pedido);
        this.confirmado = confirmado;
    }

    public int get_num_ref(){
        return num_ref;
    }

    public IPedido get_pedido_plano(){
        return planoDeTrabalho.get_pedido();
    }


    public void confirma(){
        this.confirmado = true;
    }

    public void desconfirma(){
        this.confirmado = false;
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
        if(confirmado) sb.append(1 + "#");
        else sb.append(0+"#");
        sb.append(planoDeTrabalho.toString());
        return sb.toString();
    }
}
