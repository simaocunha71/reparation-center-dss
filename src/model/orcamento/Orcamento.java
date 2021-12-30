package model.orcamento;

import model.interfaces.IOrcamento;
import model.interfaces.IPedido;
import model.interfaces.IPlanoDeTrabalho;

import java.time.LocalDateTime;

public class Orcamento implements IOrcamento {
    private int num_ref;
    private boolean confirmado = false;
    private IPlanoDeTrabalho planoDeTrabalho;
    private LocalDateTime dataConfirmacao;



    public Orcamento(IPlanoDeTrabalho planoDeTrabalho){
        this.num_ref = planoDeTrabalho.get_num_ref();
        this.planoDeTrabalho = planoDeTrabalho.clone();
        this.dataConfirmacao = null;
    }

    public Orcamento(int num_ref, IPedido pedido, boolean confirmado, LocalDateTime dataConfirmacao){
        this.num_ref = num_ref;
        this.planoDeTrabalho = new PlanoDeTrabalho(pedido.clone());
        this.confirmado = confirmado;
        this.dataConfirmacao = dataConfirmacao;
    }

    public Orcamento(int num_ref, IPlanoDeTrabalho plano, boolean confirmado, LocalDateTime dataConfirmacao){
        this.num_ref = num_ref;
        this.planoDeTrabalho = plano.clone();
        this.confirmado = confirmado;
        this.dataConfirmacao = dataConfirmacao;
    }

    public void carregar(IOrcamento orcamento){
        this.num_ref = orcamento.get_num_ref();
        this.planoDeTrabalho = orcamento.getPlanoDeTrabalho();
        this.confirmado = orcamento.getConfirmado();
        this.dataConfirmacao = orcamento.getDataConfirmacao();
    }

    public IPlanoDeTrabalho getPlanoDeTrabalho() {
        return planoDeTrabalho.clone();
    }

    public int get_num_ref(){
        return num_ref;
    }

    public IPedido get_pedido(){
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
        recalcula_orcamento();
    }

    private void recalcula_orcamento(){
        planoDeTrabalho.recalcula_estimativas();
    }

    public boolean getConfirmado(){return this.confirmado;}

    public float calcula_gasto_estimado() {
        return planoDeTrabalho.calcula_gasto_estimado();
    }

    public float calcula_duracao_estimada() {
        return planoDeTrabalho.calcula_duracao_estimada();
    }


    public void carregar(String string) {
        planoDeTrabalho.carregar(string);

    }


    public boolean valida() {
        return planoDeTrabalho.valida();
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(num_ref).append(";");
        if(confirmado) sb.append(1 + ";");
        else sb.append(0+";");
        if(dataConfirmacao != null) sb.append(dataConfirmacao);
        sb.append("#").append(planoDeTrabalho.salvar());
        return sb.toString();
    }


    public float calcula_custo_gasto(){
        return planoDeTrabalho.calcula_custo_gasto();
    }

    public float calcula_tempo_gasto(){
        return planoDeTrabalho.calcula_tempo_gasto();
    }

    public float orcamento_gasto(){
        return planoDeTrabalho.orcamento_gasto();
    }

    public boolean ultrapassou_120porcento_orcamento(){
        return planoDeTrabalho.ultrapassou_120porcento_orcamento();
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public IOrcamento clone(){
        return new Orcamento(this.num_ref,this.planoDeTrabalho,this.confirmado,this.dataConfirmacao);
    }

    public Passo get_proximo_passo(){
        return planoDeTrabalho.get_proximo_passo();
    }

    public boolean concluido(){
        return this.planoDeTrabalho.concluido();
    }

    public int get_total_passos() {
        return this.planoDeTrabalho.get_total_passos();
    }
}
