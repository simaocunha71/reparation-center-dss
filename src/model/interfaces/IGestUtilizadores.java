package model.interfaces;

import model.excecoes.JaExistenteExcecao;

import java.util.Map;

public interface IGestUtilizadores extends Carregavel {
    void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao;

    IUtilizador get_utilizador(String id);

    boolean existe_utilizador(String id);

    Map<String, IUtilizador> get_utilizadores();

    void remover_utilizador(String id);
}
