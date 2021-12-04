package model.interfaces;

import model.excecoes.JaExistenteExcecao;

public interface ICentroReparacoes {

     void adicionar_utilizador(String id,String nome,int permissao) throws JaExistenteExcecao;

     void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao;



}
