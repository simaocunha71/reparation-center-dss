package model.orcamento;

import model.interfaces.Carregavel;
import model.interfaces.Validavel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Passo implements Carregavel, Validavel {
    private List<SubPasso> subpassos;
    private String descricao;
    private float custo_estimado;
    private float custo_real;
    private float duracao_estimada; //em minutos
    private float duracao_real; //em minutos
    private boolean realizado;
    private String id_tecnico; //"idTecnico", caso não tenha sub-passos ou "vários", caso tenha sub-passos
    private int numero_do_passo;


    public Passo() {
        this.descricao = "";
        this.custo_estimado = 0;
        this.duracao_estimada = 0;
        this.custo_real = 0;
        this.duracao_real = 0;
        this.subpassos = new ArrayList<>();
        this.numero_do_passo = -1;
    }

    public Passo(String descricao, float custo_estimado, float duracao_estimada, float custo_real, float duracao_real, boolean realizado, String idTecnico, List<SubPasso> subpassos, int numero_do_passo) {
        this.descricao = descricao;
        this.custo_estimado = custo_estimado;
        this.duracao_estimada = duracao_estimada;
        this.custo_real = custo_real;
        this.duracao_real = duracao_real;
        this.realizado = realizado;
        this.id_tecnico = idTecnico;
        this.subpassos = new ArrayList<>();
        for(SubPasso sp : subpassos){
            this.subpassos.add(sp.clone());
        }
        this.numero_do_passo = numero_do_passo;
    }

    public void carrega(Passo passo) {
        this.descricao = passo.descricao;
        this.custo_estimado = passo.custo_estimado;
        this.duracao_estimada = passo.duracao_estimada;
        this.custo_real = passo.custo_real;
        this.duracao_real = passo.duracao_real;
        this.realizado = passo.realizado;
        this.id_tecnico = passo.id_tecnico;
        this.subpassos = new ArrayList<>();
        for(SubPasso sp : passo.subpassos){
            this.subpassos.add(sp.clone());
        }
        this.numero_do_passo = passo.numero_do_passo;
    }

    //se tiver subpassos
    public void concluir(){
        boolean subpassosConcluidos = true;
        Set<String> tecnicos =new HashSet<>();
        for(SubPasso sp : subpassos){
            if(!sp.concluido()) subpassosConcluidos = false;
            tecnicos.add(sp.get_id_tecnico());
        }
        if(subpassosConcluidos) {
            if(tecnicos.size() > 1) this.id_tecnico = "vários";
            else if (tecnicos.stream().findFirst().isPresent()) this.id_tecnico = tecnicos.stream().findFirst().get();
            this.custo_real = 0;
            this.duracao_real = 0;
            for (SubPasso sp : subpassos) {
                this.custo_real += sp.get_custo_real();
                this.duracao_real += sp.get_duracao_real();
            }
            realizado = true;
        }
    }

    public void set_numero_do_passo(int numero_do_passo) {
        this.numero_do_passo = numero_do_passo;
    }

    public float get_custo_real() {
        return custo_real;
    }

    public float get_duracao_real() {
        return duracao_real;
    }

    public String get_descricao() {
        return descricao;
    }



    //se nao tem subpassos
    public void concluir(String id_tecnico, float custo_real, float duracao_real){
        this.custo_real = custo_real;
        this.duracao_real = duracao_real;
        this.id_tecnico = id_tecnico;
        this.realizado = true;
    }

    public void adicionar_subpasso(SubPasso sp){
        if(subpassos.size() == 0){
            this.custo_estimado = 0;
            this.duracao_estimada = 0;
        }
        int numeroSubPasso = subpassos.size()+1;
        sp.set_numero_do_passo(numeroSubPasso);
        subpassos.add(sp.clone());
        this.custo_estimado += sp.get_custo_estimado();
        this.duracao_estimada += sp.get_duracao_estimada();
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
                    this.custo_estimado = Float.parseFloat(infos[1]);
                    this.custo_real = Float.parseFloat(infos[2]);
                    this.duracao_estimada = Float.parseFloat(infos[3]);
                    this.duracao_real = Float.parseFloat(infos[4]);
                    int b = Integer.parseInt(infos[5]);
                    if(b == 1) this.realizado = true;
                    this.id_tecnico = infos[6];
                    if(id_tecnico.equals("null")) id_tecnico = null;
                    nSP = Integer.parseInt(infos[7]);
                }
                catch(NumberFormatException ignored){
                    this.descricao = "";
                    this.custo_estimado = -1;
                    this.duracao_estimada = -1;
                    this.custo_real = 0;
                    this.duracao_real = 0;
                    this.realizado = false;
                    this.id_tecnico = null;
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

    public void set_descricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean tem_subpassos(){
        return subpassos.size()>0;
    }

    public void set_custo_estimado(float custo_estimado) {
        this.custo_estimado = custo_estimado;
    }

    public void set_custo_real(float custo_real) {
        this.custo_real = custo_real;
    }

    public void set_duracao_real(float duracao_real) {
        this.duracao_real = duracao_real;
    }

    public void set_duracao_estimada(float duracao_estimada) {
        this.duracao_estimada = duracao_estimada;
    }

    public boolean valida() {
        boolean valido = true;
        for(SubPasso sp : subpassos){
            if(!sp.valida()) valido = false;
        }
        if(realizado){
            if(id_tecnico ==null && subpassos.size()==0) valido = false;
        }
        else{
            if(id_tecnico !=null) {
                valido = false;
            }
        }
        return valido && descricao.length()>0 && custo_estimado >= 0 && duracao_estimada >= 0;
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
        sb.append(descricao).append(";").append(custo_estimado).append(";").append(custo_real).append(";");
        sb.append(duracao_estimada).append(";").append(duracao_real).append(";");
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(id_tecnico).append(";").append(subpassos.size()).append("%");
        int i = 0;
        for(; i < subpassos.size()-1; i++){
            sb.append(subpassos.get(i).salvar()).append("/");
        }
        if(subpassos.size() != 0)sb.append(subpassos.get(subpassos.size()-1).salvar());
        return sb.toString();
    }

    public void recalcula_estimativas() {
        if(subpassos.size() > 0) {
            this.custo_estimado = 0;
            this.duracao_estimada = 0;
            for (SubPasso sp : subpassos) {
                sp.recalcula_estimativas();

                this.custo_estimado += sp.get_custo_estimado();
                this.duracao_estimada += sp.get_duracao_estimada();
            }
        }
        else if (realizado){
            this.custo_estimado = custo_real;
            this.duracao_estimada = duracao_real;
        }
    }

    public float get_custo_estimado() {
        return custo_estimado;
    }

    public float get_duracao_estimada() {
        return duracao_estimada;
    }

    public int get_numero_do_passo() {
        return numero_do_passo;
    }

    public float calcula_custo_gasto(){
        float custo_gasto = 0;
        if(realizado){
            custo_gasto = this.custo_real;
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
            tempo_gasto = this.duracao_real;
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
        return new Passo(this.descricao,this.custo_estimado,this.duracao_estimada,this.custo_real,this.duracao_real,this.realizado,this.id_tecnico,this.subpassos,this.numero_do_passo);
    }

    public int get_total_subpassos(){
        return subpassos.size();
    }

    public float calcula_custo_estimado() {
        float gasto_estimado = 0;
        if(tem_subpassos()) {
            for (SubPasso sp : subpassos) {
                gasto_estimado += sp.get_custo_estimado();
            }
        }else gasto_estimado = custo_estimado;
        return gasto_estimado;
    }

    public float calcula_duracao_estimada() {
        float duracao_estimada = 0;
        if(tem_subpassos()) {
            for (SubPasso sp : subpassos) {
                duracao_estimada += sp.get_duracao_estimada();
            }
        }else duracao_estimada = this.duracao_estimada;
        return duracao_estimada;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ID do técnico associado: ").append(id_tecnico).append("\n");
        sb.append("Descrição do passo: ").append(descricao).append("\n");
        if(subpassos.isEmpty())
            sb.append("Subpassos: não existem\n");
        else{
            sb.append("SubPassos: ").append(subpassos.size()).append("\n");
        }

        sb.append("Estimativas: ").append("\n");
        sb.append(" > Custo: ").append(custo_estimado).append(" €\n");
        sb.append(" > Duração: ").append(duracao_estimada).append(" min\n");
        sb.append("Realidade: ").append("\n");
        sb.append(" > Custo: ").append(custo_real).append(" €\n");
        sb.append(" > Duração: ").append(duracao_real).append(" min\n");
        sb.append("Passo #").append(numero_do_passo).append("\n");
        if(realizado)
            sb.append("Realizado: Sim\n\n");
        else
            sb.append("Realizado: Não\n\n");

        return sb.toString();

    }

}
