package model.interfaces;

import model.logs.LogTecnico;

import java.util.List;

public interface IGestLogs extends Carregavel{

    void adicionar_log(String log, IUtilizador u);

    String get_logs_tecnicos_simples();

    String get_logs_funcionarios();

    List<LogTecnico> get_logs_tecnicos_extensivos();
}
