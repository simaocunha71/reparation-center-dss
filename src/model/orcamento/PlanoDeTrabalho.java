package model.orcamento;

import model.interfaces.IPedido;
import model.interfaces.IPlanoDeTrabalho;

import java.util.ArrayList;
import java.util.List;

public class PlanoDeTrabalho implements IPlanoDeTrabalho {
    private IPedido pedido_associado;
    private List<Passo> passos;
    private boolean realizado;


    public PlanoDeTrabalho(IPedido pedido_associado){
        this.pedido_associado = pedido_associado.clone();
        this.passos = new ArrayList<>();
        this.realizado = false;
    }

    public PlanoDeTrabalho(IPedido pedido_associado, boolean realizado, List<Passo> passos) {
        this.pedido_associado = pedido_associado.clone();
        this.realizado = realizado;
        this.passos = new ArrayList<>();
        for(Passo p : passos){
            this.passos.add(p.clone());
        }
    }


    //custoEstimado;custoReal;tempoEstimado;tempoReal;booleanoRealizado;numeroPassos@Passos

    //Passos: Passo1->Passo2->Passo3...

    public void carregar(String string){
        String[] split = string.split("@");
        if(split.length == 2){
            String[] infos = split[0].split(";");
            if(infos.length == 2) {
                int nP = 0;
                try {
                    int b = Integer.parseInt(infos[0]);
                    if (b == 1) this.realizado = true;
                    nP = Integer.parseInt(infos[1]);
                } catch (NumberFormatException ignored) {
                    this.realizado = false;
                }
                String[] passos = split[1].split("->");
                if(passos.length == nP) {
                    for (int i = 0; i < nP; i++) {
                        Passo p = new Passo();
                        p.carregar(passos[i]);
                        adicionar_passo(p);
                    }
                }
            }
        }
    }

    public IPedido get_pedido(){
        return pedido_associado.clone();
    }

    public boolean valida() {
        boolean valido = true;
        for(Passo p : passos){
            if(!p.valida()) {
                valido = false;
            }
        }
        return valido && passos.size() > 0;
    }

    public void adicionar_passo(Passo p){
        int numeroSubPasso = passos.size()+1;
        p.set_numero_do_passo(numeroSubPasso);
        passos.add(p.clone());
    }

    //custoEstimado;custoReal;tempoEstimado;tempoReal;booleanoRealizado;numeroPassos@Passos

    //Passos: Passo1->Passo2->Passo3...
    public String salvar(){
        StringBuilder sb = new StringBuilder();
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(passos.size()).append("@");
        int i = 0;
        for(; i < passos.size()-1; i++){
            sb.append(passos.get(i).salvar()).append("->");
        }
        if(passos.size() != 0) sb.append(passos.get(passos.size()-1).salvar());
        return sb.toString();
    }

    public IPlanoDeTrabalho clone(){
        return new PlanoDeTrabalho(this.pedido_associado,this.realizado,this.passos);
    }

    public void recalcula_estimativas() {
        for (Passo p : passos) {
            p.recalcula_estimativas();
        }
    }

    public float calcula_custo_gasto(){
        float custo_gasto = 0;
        for(Passo p : passos){
            custo_gasto += p.calcula_custo_gasto();
        }
        return custo_gasto;
    }

    public float calcula_custo_estimado(){
        float gasto_estimado = 0;
        for(Passo p : passos){
            gasto_estimado += p.calcula_custo_estimado();
        }
        return gasto_estimado;
    }

    public float calcula_duracao_estimada(){
        float duracao_estimada = 0;
        for(Passo p : passos){
            duracao_estimada += p.calcula_duracao_estimada();
        }
        return duracao_estimada;
    }

    public float calcula_tempo_gasto(){
        float tempo_gasto = 0;
        for(Passo p : passos){
            tempo_gasto += p.calcula_tempo_gasto();
        }
        return tempo_gasto;
    }

    public boolean ultrapassou_120porcento_orcamento(){
        return calcula_custo_estimado()*1.2 < calcula_custo_gasto();
    }

    public float orcamento_gasto(){
        return calcula_custo_gasto()*100/ calcula_custo_estimado();
    }

    public Passo get_proximo_passo(){
        boolean stop = false;
        Passo prox = null;
        if(existe_proximo_passo()) {
            for (Passo p : passos) {
                if (stop) break;
                if (!p.concluido()) {
                    stop = true;
                    prox = p;
                }
            }
        }
        return prox;
    }

    public boolean existe_proximo_passo(){
        boolean existe = false;
        for(Passo p : passos){
            if(existe) break;
            if(!p.concluido()){
                existe = true;
            }
        }
        return existe;
    }


    public int get_total_passos(){
        return passos.size();
    }

    public boolean concluido() {
        if(!realizado) {
            boolean concluido = true;
            for (Passo p : passos) {
                if (!p.concluido()) concluido = false;
            }
            if (passos.size() == 0) concluido = false;
            if (concluido) {
                realizado = true;
            }
        }
        return realizado;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(pedido_associado.toString()).append("\n");
        if(passos.isEmpty())
            sb.append("Subpassos: não existem\n");
        else{
            sb.append("Passos: ").append(passos.size()).append("\n");
        }
        if(realizado)
            sb.append("Realizado: Sim");
        else
            sb.append("Realizado: Não");
        return sb.toString();
    }
}
