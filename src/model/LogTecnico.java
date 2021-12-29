package model;

import model.interfaces.Carregavel;

import java.sql.SQLOutput;
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

    @Override
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

    public void addIntervencao(String intervencao){
        if(intervencao_valida(intervencao)) intervencoes.add(intervencao);
    }

    public String getUserId() {
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

    public List<String> get_lista_intervencoes(){
        return intervencoes;
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

    private boolean intervencao_completa_normal(String i) {
        return i.split(";")[0].equals("3");
    }


    @Override
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
        System.out.println("DEBUG LOG1");
        if(info.length == 5){
            try {
                int numReg = Integer.parseInt(info[1]);
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
                int numReg = Integer.parseInt(info[1]);
                String modelo = info[2];
                String descricao = info[3];
                LocalDateTime data = LocalDateTime.parse(info[4]);
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
                float tempo_esperado = Float.parseFloat(info[5]);
                float tempo_real = Float.parseFloat(info[6]);
                if(modelo.length() > 0 && descricao.length() > 0 && !data.isBefore(thirtyDaysAgo))
                    valido = true;
            }catch (NumberFormatException | DateTimeParseException ignored){}
        }
        return valido;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("3").append("@").append(userId).append("%");
        intervencoes.forEach(k->sb.append(k).append("->"));
        return sb.toString();
    }


}
