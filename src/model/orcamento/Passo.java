package model.orcamento;

import model.interfaces.Carregavel;
import model.interfaces.Planeavel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Passo implements Carregavel {
    private List<SubPasso> subpassos;
    private String descricao;
    private float custoEstimado;
    private float custoReal;
    private float duracaoEstimada; //em minutos
    private float duracaoReal; //em minutos
    private boolean realizado;
    private String idTecnicoRealizou; //"idTecnico", caso não tenha sub-passos ou "vários", caso tenha sub-passos
    private int numero_passo;

    public Passo(String descricao, float custoEstimado, float duracaoEstimada){
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.subpassos = new ArrayList<>();
        this.realizado = false;
        this.idTecnicoRealizou = null;
        this.numero_passo = -1;
    }

    public Passo() {
        this.descricao = "";
        this.custoEstimado = 0;
        this.duracaoEstimada = 0;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.subpassos = new ArrayList<>();
        this.numero_passo = -1;
    }

    public Passo(String descricao, float custoEstimado, float duracaoEstimada, float custoReal, float duracaoReal, boolean realizado, String idTecnico,List<SubPasso> subpassos,int numero_passo) {
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = custoReal;
        this.duracaoReal = duracaoReal;
        this.realizado = realizado;
        this.idTecnicoRealizou = idTecnico;
        this.subpassos = new ArrayList<>();
        for(SubPasso sp : subpassos){
            this.subpassos.add(sp.clone());
        }
        this.numero_passo = numero_passo;
    }

    public void carrega(Passo passo) {
        this.descricao = passo.descricao;
        this.custoEstimado = passo.custoEstimado;
        this.duracaoEstimada = passo.duracaoEstimada;
        this.custoReal = passo.custoReal;
        this.duracaoReal = passo.duracaoReal;
        this.realizado = passo.realizado;
        this.idTecnicoRealizou = passo.idTecnicoRealizou;
        this.subpassos = new ArrayList<>();
        for(SubPasso sp : passo.subpassos){
            this.subpassos.add(sp.clone());
        }
        this.numero_passo = passo.numero_passo;
    }

    //se tiver subpassos
    public void concluir(){
        boolean subpassosConcluidos = true;
        Set<String> tecnicos =new HashSet<>();
        for(SubPasso sp : subpassos){
            if(!sp.concluido()) subpassosConcluidos = false;
            tecnicos.add(sp.getIdTecnico());
        }
        if(subpassosConcluidos) {
            if(tecnicos.size() > 1) this.idTecnicoRealizou = "vários";
            else this.idTecnicoRealizou = tecnicos.stream().findFirst().get();
            this.custoReal = 0;
            this.duracaoReal = 0;
            for (SubPasso sp : subpassos) {
                this.custoReal += sp.getCustoReal();
                this.duracaoReal += sp.getDuracaoReal();
            }
            realizado = true;
        }
    }

    public void setNumero_passo(int numero_passo) {
        this.numero_passo = numero_passo;
    }

    public float getCustoReal() {
        return custoReal;
    }

    public float getDuracaoReal() {
        return duracaoReal;
    }

    public String getDescricao() {
        return descricao;
    }



    //se nao tem subpassos
    public void concluir(String idTecnico, float custoReal, float duracaoReal){
        this.custoReal = custoReal;
        this.duracaoReal = duracaoReal;
        this.idTecnicoRealizou = idTecnico;
        this.realizado = true;
    }


    public void adicionar_subpasso(String descricao, float custoEstimado, float duracaoEstimada){
        if(subpassos.size() == 0){
            this.custoEstimado = 0;
            this.duracaoEstimada = 0;
        }
        SubPasso sp = new SubPasso(descricao,custoEstimado,duracaoEstimada);
        int numeroSubPasso = subpassos.size()+1;
        sp.setNumero_subpasso(numeroSubPasso);
        subpassos.add(sp.clone());
        this.custoEstimado += sp.getCustoEstimado();
        this.duracaoEstimada += sp.getDuracaoEstimada();
    }

    public void adicionar_subpasso(SubPasso sp){
        if(subpassos.size() == 0){
            this.custoEstimado = 0;
            this.duracaoEstimada = 0;
        }
        int numeroSubPasso = subpassos.size()+1;
        sp.setNumero_subpasso(numeroSubPasso);
        subpassos.add(sp.clone());
        this.custoEstimado += sp.getCustoEstimado();
        this.duracaoEstimada += sp.getDuracaoEstimada();
    }


    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanoRealizado;idTecnico;numeroSP%subPassos

    //subPassos: subpasso1/subpasso2/subpass3...
    public void carregar(String string) {
        String[] split = string.split("%");
        if(split.length <= 2){
            String[] infos = split[0].split(";");
            if(infos.length == 8){
                int nSP = 0;
                try {
                    this.descricao = infos[0];
                    System.out.println("DEBUG: "+descricao);
                    this.custoEstimado = Float.parseFloat(infos[1]);
                    this.custoReal = Float.parseFloat(infos[2]);
                    this.duracaoEstimada = Float.parseFloat(infos[3]);
                    this.duracaoReal = Float.parseFloat(infos[4]);
                    int b = Integer.parseInt(infos[5]);
                    if(b == 1) this.realizado = true;
                    this.idTecnicoRealizou = infos[6];
                    if(idTecnicoRealizou.equals("null")) idTecnicoRealizou = null;
                    nSP = Integer.parseInt(infos[7]);
                }
                catch(NumberFormatException ignored){
                    this.descricao = "";
                    this.custoEstimado = -1;
                    this.duracaoEstimada = -1;
                    this.custoReal = 0;
                    this.duracaoReal = 0;
                    this.realizado = false;
                    this.idTecnicoRealizou = null;
                }
                String[] subpassos = null;
                if(nSP > 0)
                    subpassos = split[1].split("/");
                if(subpassos != null && subpassos.length == nSP){
                    for(int i = 0; i < nSP; i++){
                        SubPasso sp = new SubPasso();
                        sp.carregar(subpassos[i]);
                        adicionar_subpasso(sp);
                    }
                }
            }
        }
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean temSubPassos(){
        return subpassos.size()>0;
    }

    public void setCustoEstimado(float custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public void setCustoReal(float custoReal) {
        this.custoReal = custoReal;
    }

    public void setDuracaoReal(float duracaoReal) {
        this.duracaoReal = duracaoReal;
    }

    public void setDuracaoEstimada(float duracaoEstimada) {
        this.duracaoEstimada = duracaoEstimada;
    }

    public boolean valida() {
        boolean valido = true;
        for(SubPasso sp : subpassos){
            if(!sp.valida()) valido = false;
        }
        if(realizado){
            if(idTecnicoRealizou==null && subpassos.size()==0) valido = false;
        }
        else{
            if(idTecnicoRealizou!=null) {
                valido = false;
            }
        }
        return valido && descricao.length()>0 && custoEstimado >= 0 && duracaoEstimada >= 0;
    }

    public boolean concluido() {
        if(!realizado) {
            boolean concluido = true;
            for (SubPasso sp : subpassos) {
                if (!sp.concluido()) concluido = false;
            }
            if (subpassos.size() == 0) concluido = false;
            if (concluido) {
                concluir();
            }
        }
        return realizado;
    }


    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanoRealizado;idTecnico;numeroSP%subPassos
    //subPassos: subpasso1/subpasso2/subpass3...
    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(descricao).append(";").append(custoEstimado).append(";").append(custoReal).append(";");
        sb.append(duracaoEstimada).append(";").append(duracaoReal).append(";");
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(idTecnicoRealizou).append(";").append(subpassos.size()).append("%");
        int i = 0;
        for(; i < subpassos.size()-1; i++){
            sb.append(subpassos.get(i).toString()).append("/");
        }
        if(subpassos.size() != 0)sb.append(subpassos.get(subpassos.size()-1).toString());
        return sb.toString();
    }

    public void recalcula_estimativas() {
        if(subpassos.size() > 0) {
            this.custoEstimado = 0;
            this.duracaoEstimada = 0;
            for (SubPasso sp : subpassos) {
                sp.recalcula_estimativas();

                this.custoEstimado += sp.getCustoEstimado();
                this.duracaoEstimada += sp.getDuracaoEstimada();
            }
        }
        else if (realizado){
            this.custoEstimado = custoReal;
            this.duracaoEstimada = duracaoReal;
        }
    }

    public float getCustoEstimado() {
        return custoEstimado;
    }

    public float getDuracaoEstimada() {
        return duracaoEstimada;
    }

    public int getNumero_passo() {
        return numero_passo;
    }

    public float calcula_custo_gasto(){
        float custo_gasto = 0;
        if(realizado){
            custo_gasto = this.custoReal;
        }
        else {
            for(SubPasso sp : subpassos){
                custo_gasto += sp.calcula_custo_gasto();
            }
        }
        return custo_gasto;
    }


    public float calcula_tempo_gasto() {
        float tempo_gasto = 0;
        if(realizado){
            tempo_gasto = this.duracaoReal;
        }
        else {
            for(SubPasso sp : subpassos){
                tempo_gasto += sp.calcula_tempo_gasto();
            }
        }
        return tempo_gasto;
    }



    public SubPasso get_proximo_subpasso(){
        boolean stop = false;
        SubPasso prox = null;
        if(existe_proximo_subpasso()){
            for(SubPasso sp : subpassos){
                if(stop) break;
                if(!sp.concluido()){
                    stop = true;
                    prox = sp;
                }
            }
        }
        return prox;
    }

    public boolean existe_proximo_subpasso(){
        boolean existe = false;
        for(SubPasso sp : subpassos){
            if(existe) break;
            if(!sp.concluido()){
                existe = true;
            }
        }
        return existe;
    }

    public Passo clone(){
        return new Passo(this.descricao,this.custoEstimado,this.duracaoEstimada,this.custoReal,this.duracaoReal,this.realizado,this.idTecnicoRealizou,this.subpassos,this.numero_passo);
    }

    public int get_total_subpassos(){
        return subpassos.size();
    }

    public float calcula_gasto_estimado() {
        float gasto_estimado = 0;
        if(temSubPassos()) {
            for (SubPasso sp : subpassos) {
                gasto_estimado += sp.getCustoEstimado();
            }
        }else gasto_estimado = custoEstimado;
        return gasto_estimado;
    }

    public float calcula_duracao_estimada() {
        float duracao_estimada = 0;
        if(temSubPassos()) {
            for (SubPasso sp : subpassos) {
                duracao_estimada += sp.getDuracaoEstimada();
            }
        }else duracao_estimada = duracaoEstimada;
        return duracao_estimada;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ID do técnico associado: ").append(idTecnicoRealizou).append("\n");
        sb.append("Descrição do passo: ").append(descricao).append("\n");
        if(subpassos.isEmpty())
            sb.append("Subpassos: não existem\n");
        else{
            sb.append("SubPassos: "+ subpassos.size() +"\n");
        }

        sb.append("Estimativas: ").append("\n");
        sb.append(" > Custo: ").append(custoEstimado).append(" €\n");
        sb.append(" > Duração: ").append(duracaoEstimada).append(" min\n");
        sb.append("Realidade: ").append("\n");
        sb.append(" > Custo: ").append(custoReal).append(" €\n");
        sb.append(" > Duração: ").append(duracaoReal).append(" min\n");
        sb.append("Passo #").append(numero_passo).append("\n");
        if(realizado)
            sb.append("Realizado: Sim\n\n");
        else
            sb.append("Realizado: Não\n\n");

        return sb.toString();

    }

}
