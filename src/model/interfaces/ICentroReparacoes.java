package model.interfaces;

import model.armazem.Equipamento;
import model.orcamento.Orcamento;
import model.orcamento.PlanoDeTrabalho;
import model.excecoes.JaExistenteExcecao;

import java.io.IOException;
import java.util.List;

public interface ICentroReparacoes {

    IUtilizador get_utilizador_by_ID(String id);

    String get_logged_id();

    void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao, IOException;

    void adicionar_cliente(String nif, String nome, String numTelemovel, String email) throws JaExistenteExcecao, IOException;

    void adicionar_orcamento(Orcamento orcamento) throws IOException; //**

    void concluir_reparacao(Orcamento orcamento) throws IOException; //**

    void carregar_utilizadores(String filename) throws IOException, JaExistenteExcecao;

    void carregar_cp(String utilizadoresFN,String clientesFN,String armazemFN,String pedidosFN, String planosFN) throws IOException, JaExistenteExcecao;

    void adicionar_pedido_orcamento(String nifCliente, String modelo, String descricaoEquipamento, String descricaoPedido) throws IOException;

    boolean exists_plan();

    boolean login (String id, String password);

    void logout();

    boolean logged_funcionario();
    boolean logged_tecnico();
    boolean logged_gestor();

    boolean exists_user(String id);

    boolean exists_cliente(String nif);

    int get_ultimo_numero_de_registo_equipamento();

    List<String> get_pedidos_orcamento();

    IPedido get_pedido(int posicao);

    void gerar_orcamento(PlanoDeTrabalho plano) throws IOException; //**

    List<Orcamento> get_orcamentos_por_confirmar();

    ICliente get_cliente(String nif);

    void confirmar_orcamento(int num_ref) throws IOException; //**

    List<Orcamento> get_orcamentos_confirmados();

    Orcamento get_orcamento(int num_ref);

    Equipamento getEquipamento(int num_ref);
}
