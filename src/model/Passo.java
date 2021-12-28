package model;

import model.interfaces.Carregavel;

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

    public Passo(String descricao, float custoEstimado, float duracaoEstimada){
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.subpassos = new ArrayList<>();
        this.realizado = false;
        this.idTecnicoRealizou = null;
    }

    public Passo() {
        this.descricao = "";
        this.custoEstimado = 0;
        this.duracaoEstimada = 0;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.subpassos = new ArrayList<>();
    }

    public Passo(String descricao, float custoEstimado, float duracaoEstimada, float custoReal, float duracaoReal, boolean realizado, String idTecnico,List<SubPasso> subpassos) {
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
        realizado = true;
    }


    public void adicionar_subpasso(String descricao, float custoEstimado, float duracaoEstimada){
        SubPasso novoPasso = new SubPasso(descricao,custoEstimado,duracaoEstimada);
        subpassos.add(novoPasso);
    }

    public void adicionar_subpasso(SubPasso sp){
        if(subpassos.size() == 0){
            this.custoEstimado = 0;
            this.duracaoEstimada = 0;
        }
        subpassos.add(sp);
        this.custoEstimado += sp.getCustoEstimado();
        this.duracaoEstimada += sp.getDuracaoEstimada();
    }


    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanoRealizado;idTecnico;numeroSP%subPassos

    //subPassos: subpasso1/subpasso2/subpass3...
    public void carregar(String string) {
        String[] split = string.split("%");
        if(split.length == 2){
            String[] infos = split[0].split(";");
            if(infos.length == 8){
                int nSP = 0;
                try {
                    this.descricao = infos[0];
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
                String[] subpassos = split[1].split("/");
                if(subpassos.length == nSP){
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
            if(idTecnicoRealizou!=null && subpassos.size()>0) valido = false;
        }
        else{
            if(idTecnicoRealizou!=null) valido = false;
        }

        return valido && descricao.length()>0 && custoEstimado >= 0 && duracaoEstimada >= 0;
    }

    public boolean concluido() {
        return realizado;
    }


    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanoRealizado;idTecnico;numeroSP%subPassos
    //subPassos: subpasso1/subpasso2/subpass3...
    public String toString(){
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



    public SubPasso getProximoSubPasso(){
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
        return new Passo(this.descricao,this.custoEstimado,this.duracaoEstimada,this.custoReal,this.duracaoReal,this.realizado,this.idTecnicoRealizou,this.subpassos);
    }

}
