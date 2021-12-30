package model.logs;

import model.interfaces.Carregavel;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LogTecnico implements Carregavel {
    private String userId;
    private List<String> intervencoes;


    public LogTecnico(){
        this.userId = null;
        this.intervencoes = new ArrayList<>();
    }

    public LogTecnico(String userId){
        this.userId = userId;
        this.intervencoes = new ArrayList<>();
    }

    public void carregar(String string) {
        String []split = string.split("%");
        if(split.length == 2){
            String[] info = split[0].split(";");
            if(info.length == 1){
                userId = info[0];
                String []listaIntervencoes = split[1].split("->");
                for(String i: listaIntervencoes){
                    if(intervencao_valida(i))
                        intervencoes.add(i);
                }
            }
        }
    }

    public void add_intervencao(String intervencao){
        if(intervencao_valida(intervencao)) intervencoes.add(intervencao);
    }

    public String get_user_id() {
        return userId;
    }

    public int get_numero_passos_completos(){
        int conta = 0;
        for(String i: intervencoes){
            if(tipo_intervencao(i) == 1 || tipo_intervencao(i) == 2) conta++;
        }
        return conta;
    }

    public int get_numero_reparacoes_expresso(){
        int conta = 0;
        for(String i: intervencoes){
            if(tipo_intervencao(i) == 0) conta++;
        }
        return conta;
    }

    public float get_media_duracao_real(){
        int conta = 0;
        float real = 0;
        for(String i: intervencoes){
            if(tipo_intervencao(i) == 1 || tipo_intervencao(i) == 2){
                real += tempo_real_passo(i);
                conta++;
            }
        }
        return real/conta;
    }

    public float get_media_duracao_esperada(){
        int conta = 0;
        float real = 0;
        for(String i: intervencoes){
            if(tipo_intervencao(i) == 1 || tipo_intervencao(i) == 2){
                real += tempo_esperado_passo(i);
                conta++;
            }
        }
        return real/conta;
    }

    private float tempo_real_passo(String i) {
        try{
            return  Float.parseFloat(i.split(";")[6]);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    private float tempo_esperado_passo(String i) {

        try{
            return  Float.parseFloat(i.split(";")[5]);
        }catch (NumberFormatException e){
            return 0;
        }
    }


    private int tipo_intervencao(String i) {
        try {
            return Integer.parseInt(i.split(";")[0]);
        }catch (NumberFormatException e){
            return -1;
        }
    }

    public boolean valida() {
        return userId != null;
    }

    private boolean intervencao_valida(String intervencao) {
        boolean valido = false;
        String []info = intervencao.split(";");

        try{
            int tipo = Integer.parseInt(info[0]);
            switch(tipo){
                case 0,3 -> valido = expresso_ou_completo_valido(info);
                case 1,2 -> valido = passo_valido(info);

            }
        }catch (NumberFormatException ignored){}
        return valido;
    }

    private boolean expresso_ou_completo_valido(String[] info) {
        boolean valido = false;
        if(info.length == 5){
            try {
                Integer.parseInt(info[1]);
                String modelo = info[2];
                String descricao = info[3];
                LocalDateTime data = LocalDateTime.parse(info[4]);
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
                if(modelo.length() > 0 && descricao.length() > 0 && !data.isBefore(thirtyDaysAgo)) {
                    valido = true;
                }
            }catch (NumberFormatException | DateTimeParseException ignored){}
        }
        return valido;
    }

    private boolean passo_valido(String[] info) {
        boolean valido = false;
        if(info.length == 7){
            try {
                Integer.parseInt(info[1]);
                String modelo = info[2];
                String descricao = info[3];
                LocalDateTime data = LocalDateTime.parse(info[4]);
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
                Float.parseFloat(info[5]);
                Float.parseFloat(info[6]);
                if(modelo.length() > 0 && descricao.length() > 0 && !data.isBefore(thirtyDaysAgo))
                    valido = true;
            }catch (NumberFormatException | DateTimeParseException ignored){}
        }
        return valido;
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append("3").append("@").append(userId).append("%");
        intervencoes.forEach(k->sb.append(k).append("->"));
        return sb.toString();
    }

    public String estatisticas_extensivas(){
        StringBuilder sb = new StringBuilder();
        intervencoes.forEach(k-> sb.append(intervencaoToString(k)));
        return sb.toString();
    }

    public String intervencaoToString(String string) {
        String[] info = string.split(";");
        StringBuilder intervencao = new StringBuilder();
        int tipo = tipo_intervencao(string);
        if(intervencao_valida(string)) {
            switch (tipo) {
                case 0, 3 -> {
                    if (tipo == 0) intervencao.append("Servico expresso : ");
                    else intervencao.append("Reparacao Normal Completa : ");
                    int numReg = Integer.parseInt(info[1]);
                    String modelo = info[2];
                    String descricao = info[3];
                    LocalDateTime data = LocalDateTime.parse(info[4]);
                    intervencao.append("[# ").append(numReg).append("]").
                            append(" Modelo [").append(modelo).append("]").
                            append(" Descricao [").append(descricao).append("]").
                            append(" Data [").append(data).append("]");
                }

                case 1, 2 -> {
                    if (tipo == 1) intervencao.append("Passo : ");
                    else intervencao.append("SubPasso : ");
                    int numReg = Integer.parseInt(info[1]);
                    String modelo = info[2];
                    String descricao = info[3];
                    LocalDateTime data = LocalDateTime.parse(info[4]);
                    float tempo_esperado = Float.parseFloat(info[5]);
                    float tempo_real = Float.parseFloat(info[6]);
                    intervencao.append("[# ").append(numReg).append("]").
                            append(" Modelo [").append(modelo).append("]").
                            append(" Descricao [").append(descricao).append("]").
                            append(" Tempo_Esperado [").append(tempo_esperado).append("]").
                            append(" Tempo_Real [").append(tempo_real).append("]").
                            append(" Data [").append(data).append("]");
                }
            }
        }
        intervencao.append("\n");
        return intervencao.toString();
    }

}
