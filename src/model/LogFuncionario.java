package model;

import model.interfaces.Carregavel;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LogFuncionario implements Carregavel {
    private String userId;
    private List<String> operacoes;

    public LogFuncionario(){
        this.userId = null;
        this.operacoes = new ArrayList<>();
    }

    public LogFuncionario(String userId){
        this.userId = userId;
        this.operacoes = new ArrayList<>();
    }

    public void carregar(String string) {
        String []split = string.split("%");
        if(split.length == 2){
            String[] info = split[0].split(";");
            if(info.length == 1){
                userId = info[0];
                String []listaOperacoes = split[1].split("->");
                for(String o: listaOperacoes){
                    if(operacao_valida(o))
                        operacoes.add(o);
                }
            }
        }
    }

    public void addOperacao(String operacao){
        if(operacao_valida(operacao)) operacoes.add(operacao);
        System.out.println(operacao + " "+operacao_valida(operacao));
    }

    public String getUserId() {
        return userId;
    }

    public int get_numero_rececoes(){
        int conta = 0;
        for(String i: operacoes){
            if(tipo_operacao(i) == 0) conta++;
        }
        return conta;
    }

    public int get_numero_entreagas(){
        int conta = 0;
        for(String i: operacoes){
            if(tipo_operacao(i)  == 1) conta++;
        }
        return conta;
    }

    private int tipo_operacao(String i) {
        try {
            return Integer.parseInt(i.split(";")[0]);
        }catch (NumberFormatException e){
            return -1;
        }
    }




    @Override
    public boolean valida() {
        return userId != null;
    }

    private boolean operacao_valida(String intervencao) {
        boolean valido = false;
        String []info = intervencao.split(";");
        try{
            int tipo = Integer.parseInt(info[0]);
            if(info.length == 2 && tipo >=0 && tipo <=1){
                LocalDateTime data = LocalDateTime.parse(info[1]);
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
                if(!data.isBefore(thirtyDaysAgo)) {
                    valido = true;
                }
            }
        }catch (NumberFormatException | DateTimeParseException ignored){}
        return valido;
    }


    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append("2").append("@").append(userId).append("%");
        operacoes.forEach(k->sb.append(k).append("->"));
        return sb.toString();
    }

}
