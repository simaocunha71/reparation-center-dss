package model.interfaces;

import model.LogFuncionario;
import model.LogTecnico;
import model.utilizadores.Funcionario;
import model.utilizadores.Tecnico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSLogs implements  IGestLogs{
    private Map<String, LogTecnico> logs_tecnicos;
    private Map<String, LogFuncionario> logs_funcionarios;

    public SSLogs(){
        this.logs_funcionarios = new HashMap<>();
        this.logs_tecnicos = new HashMap<>();
    }


    public void adicionar_log(String log, IUtilizador u) {
        String user_id = u.get_id();
        if(u.getClass().equals(Tecnico.class)){
            if(logs_tecnicos.containsKey(user_id)){
                logs_tecnicos.get(user_id).add_intervencao(log);
            }else{
                LogTecnico l = new LogTecnico(user_id);
                l.add_intervencao(log);
                logs_tecnicos.put(user_id,l);
            }
        }else if(u.getClass().equals(Funcionario.class)){
            if(logs_funcionarios.containsKey(user_id)){
                logs_funcionarios.get(user_id).addOperacao(log);
            }else{
                LogFuncionario l = new LogFuncionario(user_id);
                l.addOperacao(log);
                logs_funcionarios.put(user_id,l);
            }
        }
    }


    public void carregar(String string) {
        String [] split = string.split("@");
        if(split.length == 2) {
            try {
                int permissao = Integer.parseInt(split[0]);
                if (permissao == 2) {
                    LogFuncionario log = new LogFuncionario();
                    log.carregar(split[1]);
                    if(log.valida()) {
                        logs_funcionarios.put(log.getUserId(),log);
                    }
                } else if (permissao == 3) {
                    LogTecnico log = new LogTecnico();
                    log.carregar(split[1]);
                    if(log.valida()) {
                        logs_tecnicos.put(log.get_user_id(),log);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    public String salvar() {
        StringBuilder sb;
        sb = new StringBuilder();
        logs_tecnicos.forEach((k, v) -> sb.append(v.salvar()).append("\n"));
        logs_funcionarios.forEach((k, v) -> sb.append(v.salvar()).append("\n"));
        return sb.toString();
    }

    public String get_logs_tecnicos_simples(){
        StringBuilder sb = new StringBuilder();
        logs_tecnicos.forEach((k, v)-> sb.append(v.get_user_id()).append("-> Passos [").append(v.get_numero_passos_completos()).append("] ")
                .append("Pedidos Expresso [").append(v.get_numero_reparacoes_expresso()).append("] ")
                .append("Duracao Media [").append(v.get_media_duracao_real()).append("] ")
                .append("Desvio Duracao Media [").append(v.get_media_duracao_esperada()-v.get_media_duracao_real()).append("]\n"));
        return sb.toString();
    }


    public String get_logs_funcionarios(){
        StringBuilder sb = new StringBuilder();
        logs_funcionarios.forEach((k, v)-> sb.append(v.getUserId()).append("-> Rececoes [").append(v.get_numero_rececoes()).append("] ")
                .append("Entregas [").append(v.get_numero_entregas()).append("]\n"));
        return sb.toString();
    }

    public List<LogTecnico> get_logs_tecnicos_extensivos(){
        List<LogTecnico> tecnicos = new ArrayList<>();
        logs_tecnicos.forEach((k, v)-> tecnicos.add(v));
        return tecnicos;
    }
}
