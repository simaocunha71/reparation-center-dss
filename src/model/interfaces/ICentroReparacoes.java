package model.interfaces;

import model.Equipamento;
import model.excecoes.JaExistenteExcecao;

import java.io.IOException;

public interface ICentroReparacoes {

    IUtilizador get_utilizador_by_ID(String id);

    void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao;

    void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao;

    void carregar_utilizadores(String filename) throws IOException, JaExistenteExcecao;

    void carregar_cp(String utilizadoresFN,String clientesFN,String pedidosFN) throws IOException, JaExistenteExcecao;

    public void adicionar_pedido_orcamento(String nifCliente, Equipamento equipamento, String descricao);

    boolean exists_plan();

    boolean login (String id, String password);

    void logout();

    boolean logged_funcionario();
    boolean logged_tecnico();
    boolean logged_gestor();

    boolean exists_user(String id);

    boolean exists_cliente(String nif);

    public String novo_numero_registo();
}
