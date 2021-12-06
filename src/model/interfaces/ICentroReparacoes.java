package model.interfaces;

import model.excecoes.JaExistenteExcecao;

import java.io.IOException;

public interface ICentroReparacoes {

     IUtilizador getUtilizadorByID(String id);

     void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao;

     void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao;

     void carregar_utilizadores(String filename) throws IOException, JaExistenteExcecao;

     void carregar_clientes(String filename) throws IOException, JaExistenteExcecao;

     Boolean login(String id,String password);



}
