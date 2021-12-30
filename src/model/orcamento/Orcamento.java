package model.orcamento;

import model.interfaces.IOrcamento;
import model.interfaces.IPedido;
import model.interfaces.IPlanoDeTrabalho;

import java.time.LocalDateTime;

public class Orcamento implements IOrcamento {
    private int num_registo;
    private boolean confirmado = false;
    private IPlanoDeTrabalho plano_de_trabalho;
    private LocalDateTime data_confirmacao;



    public Orcamento(IPlanoDeTrabalho plano_de_trabalho){
        this.num_registo = plano_de_trabalho.get_pedido().get_num_registo();
        this.plano_de_trabalho = plano_de_trabalho.clone();
        this.data_confirmacao = null;
    }

    public Orcamento(int num_registo, IPedido pedido, boolean confirmado, LocalDateTime data_confirmacao){
        this.num_registo = num_registo;
        this.plano_de_trabalho = new PlanoDeTrabalho(pedido.clone());
        this.confirmado = confirmado;
        this.data_confirmacao = data_confirmacao;
    }

    public Orcamento(int num_registo, IPlanoDeTrabalho plano, boolean confirmado, LocalDateTime data_confirmacao){
        this.num_registo = num_registo;
        this.plano_de_trabalho = plano.clone();
        this.confirmado = confirmado;
        this.data_confirmacao = data_confirmacao;
    }

    public void carregar(IOrcamento orcamento){
        this.num_registo = orcamento.get_plano_de_trabalho().get_pedido().get_num_registo();
        this.plano_de_trabalho = orcamento.get_plano_de_trabalho();
        this.confirmado = orcamento.get_confirmado();
        this.data_confirmacao = orcamento.get_data_confirmacao();
    }

    public IPlanoDeTrabalho get_plano_de_trabalho() {
        return plano_de_trabalho.clone();
    }

    public int get_num_registo() {
        return num_registo;
    }

    public void confirma(){
        this.confirmado = true;
        this.data_confirmacao = LocalDateTime.now();
        recalcula_orcamento();
    }

    public void desconfirma(){
        this.confirmado = false;
        this.data_confirmacao = null;
        recalcula_orcamento();
    }

    private void recalcula_orcamento(){
        plano_de_trabalho.recalcula_estimativas();
    }

    public boolean get_confirmado(){return this.confirmado;}



    public void carregar(String string) {
        plano_de_trabalho.carregar(string);

    }


    public boolean valida() {
        return plano_de_trabalho.valida();
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(num_registo).append(";");
        if(confirmado) sb.append(1 + ";");
        else sb.append(0+";");
        if(data_confirmacao != null) sb.append(data_confirmacao);
        sb.append("#").append(plano_de_trabalho.salvar());
        return sb.toString();
    }

    public LocalDateTime get_data_confirmacao() {
        return data_confirmacao;
    }

    public IOrcamento clone(){
        return new Orcamento(this.num_registo,this.plano_de_trabalho,this.confirmado,this.data_confirmacao);
    }

}
