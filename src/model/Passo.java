package model;

import model.interfaces.Loadable;

import java.util.ArrayList;
import java.util.List;

public class Passo implements Loadable {
    List<SubPasso> passos;
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
        this.passos = new ArrayList<>();
        this.realizado = false;
        this.idTecnicoRealizou = null;
    }

    public Passo() {
        this.descricao = "";
        this.custoEstimado = -1;
        this.duracaoEstimada = -1;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
    }

    public void concluirPasso(String idTecnico){
        this.idTecnicoRealizou = idTecnico;
        realizado = true;
    }


    public void adicionar_subpasso(String descricao, float custoEstimado, float duracaoEstimada){
        SubPasso novoPasso = new SubPasso(descricao,custoEstimado,duracaoEstimada);
        passos.add(novoPasso);
    }

    public void adicionar_subpasso(SubPasso sp){
        passos.add(sp);
    }


    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanoRealizado;idTecnico;numeroSP%subPassos

    //subPassos: subpasso1/subpasso2/subpass3...
    public void load(String string) {
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
                        sp.load(subpassos[i]);
                        adicionar_subpasso(sp);
                    }
                }
            }
        }
    }

    public boolean validate() {
        return true;
    }
}
