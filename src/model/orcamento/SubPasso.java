package model.orcamento;

import model.interfaces.Carregavel;

public class SubPasso implements Carregavel {
    private String descricao;
    private float custo_estimado;
    private float custo_real;
    private float duracao_estimada; //em minutos
    private float duracao_real; //em minutos
    private boolean realizado;
    private String id_tecnico;
    private int numero_do_subpasso;

    public SubPasso() {
        this.descricao = "";
        this.custo_estimado = 0;
        this.duracao_estimada = 0;
        this.custo_real = 0;
        this.duracao_real = 0;
        this.realizado = false;
        this.id_tecnico = null;
        this.numero_do_subpasso = -1;
    }

    public SubPasso(String descricao, float custo_estimado, float duracao_estimada, float custoReal, float duracaoReal, boolean realizado, String id_tecnico, int numero_do_subpasso) {
        this.descricao = descricao;
        this.custo_estimado = custo_estimado;
        this.duracao_estimada = duracao_estimada;
        this.custo_real = custoReal;
        this.duracao_real = duracaoReal;
        this.realizado = realizado;
        this.id_tecnico = id_tecnico;
        this.numero_do_subpasso = numero_do_subpasso;
    }

    public void carrega(SubPasso passo) {
        this.descricao = passo.descricao;
        this.custo_estimado = passo.custo_estimado;
        this.duracao_estimada = passo.duracao_estimada;
        this.custo_real = passo.custo_real;
        this.duracao_real = passo.duracao_real;
        this.realizado = passo.realizado;
        this.id_tecnico = passo.id_tecnico;
        this.numero_do_subpasso = passo.numero_do_subpasso;
    }

    public void concluir(String id_tecnico, float custo_real, float duracao_real){
        this.custo_real = custo_real;
        this.duracao_real = duracao_real;
        this.id_tecnico = id_tecnico;
        this.realizado = true;
    }

    public boolean concluido(){return this.realizado;}

    public float get_custo_real() {
        return custo_real;
    }

    public float get_duracao_real() {
        return duracao_real;
    }

    public String get_descricao() {
        return descricao;
    }

    public String get_id_tecnico() {
        return id_tecnico;
    }

    public void set_custo_real(float custo_real) {
        this.custo_real = custo_real;
    }

    public void set_duracao_real(float duracao_real) {
        this.duracao_real = duracao_real;
    }

    public void set_numero_do_passo(int numero_do_subpasso) {
        this.numero_do_subpasso = numero_do_subpasso;
    }

    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanRealizado;idTecnico
    public void carregar(String string) {
       String[] infos = string.split(";");
       if (infos.length == 7){
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
       }
    }

    public void set_duracao_estimada(float duracao_estimada) {
        this.duracao_estimada = duracao_estimada;
    }

    public void set_custo_estimado(float custo_estimado) {
        this.custo_estimado = custo_estimado;
    }

    public void set_descricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean valida() {
        return descricao.length()>0 && custo_estimado >= 0 && duracao_estimada >= 0 && ((!realizado && id_tecnico == null)||(realizado && id_tecnico != null));
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(descricao).append(";").append(custo_estimado).append(";").append(custo_real).append(";");
        sb.append(duracao_estimada).append(";").append(duracao_real).append(";");
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(id_tecnico);
        return sb.toString();
    }


    public float get_custo_estimado() {
        return custo_estimado;
    }

    public float get_duracao_estimada() {
        return duracao_estimada;
    }

    public int get_numero_do_passo() {
        return numero_do_subpasso;
    }

    public void recalcula_estimativas() {
        if(realizado){
            this.custo_estimado = custo_real;
            this.duracao_estimada = duracao_real;
        }
    }

    public float calcula_custo_gasto() {
        float custo_gasto = 0;
        if (realizado) custo_gasto = this.custo_real;
        return  custo_gasto;
    }

    public float calcula_tempo_gasto() {
        float tempo_gasto = 0;
        if (realizado) tempo_gasto = this.duracao_real;
        return  tempo_gasto;
    }

    public SubPasso clone(){
        return new SubPasso(this.descricao,this.custo_estimado,this.duracao_estimada,this.custo_real,this.duracao_real,this.realizado,this.id_tecnico, this.numero_do_subpasso);
    }

    //Subpasso
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("  ID do técnico associado: ").append(id_tecnico).append("\n");
        sb.append("  Descrição do passo: ").append(descricao).append("\n");
        sb.append("  Estimativas: ").append("\n");
        sb.append("   > Custo: ").append(custo_estimado).append(" €\n");
        sb.append("   > Duração: ").append(duracao_estimada).append(" min\n");
        sb.append("  Realidade: ").append("\n");
        sb.append("   > Custo: ").append(custo_real).append(" €\n");
        sb.append("   > Duração: ").append(duracao_real).append(" min\n");
        sb.append("  SubPasso #").append(numero_do_subpasso).append("\n\n");
        if(realizado)
            sb.append("  Realizado: Sim");
        else
            sb.append("  Realizado: Não");
        return sb.toString();
    }

}

