package model.interfaces;

import model.excecoes.JaExistenteExcecao;

import java.io.IOException;

public interface ICentroReparacoes {

     IUtilizador getUtilizadorByID(String id);

     void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao;

     void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao;

     void carregar_utilizadores(String filename) throws IOException, JaExistenteExcecao;

     void carregar_cp(String utilizadoresFN,String clientesFN,String pedidosFN) throws IOException, JaExistenteExcecao;


    boolean existsUser(String nomeDeUtilizador, String password);

    boolean existsPlans();
}
