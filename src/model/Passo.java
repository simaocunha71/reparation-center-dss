package model;

import model.interfaces.Carregavel;

import java.util.ArrayList;
import java.util.List;

public class Passo implements Carregavel {
    List<SubPasso> subpassos;
    String descricao;
    float custoEstimado;
    float custoReal;
    float duracaoEstimada; //em minutos
    float duracaoReal; //em minutos
    boolean realizado;
    String idTecnicoRealizou; //"idTecnico", caso não tenha sub-passos ou "vários", caso tenha sub-passos

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
        this.custoEstimado = -1;
        this.duracaoEstimada = -1;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.subpassos = new ArrayList<>();
    }

    //se tiver subpassos
    public void concluir(){
        boolean subpassosConcluidos = true;
        for(SubPasso sp : subpassos){
            if(!sp.concluido()) subpassosConcluidos = false;
        }
        if(subpassosConcluidos) {
            this.idTecnicoRealizou = "vários";
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
        subpassos.add(sp);
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
                    this.custoEstimado = Integer.parseInt(infos[1]);
                    this.custoReal = Integer.parseInt(infos[2]);
                    this.duracaoEstimada = Integer.parseInt(infos[3]);
                    this.duracaoReal = Integer.parseInt(infos[4]);
                    int b = Integer.parseInt(infos[5]);
                    if(b == 1) this.realizado = true;
                    this.idTecnicoRealizou = infos[6];
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

    public void setDuracaoEstimada(float duracaoEstimada) {
        this.duracaoEstimada = duracaoEstimada;
    }

    //TODO: valida
    public boolean valida() {
        return true;
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
}
