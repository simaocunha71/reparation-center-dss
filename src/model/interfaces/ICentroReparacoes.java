package model.interfaces;

import model.LogTecnico;
import model.excecoes.JaExistenteExcecao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ICentroReparacoes {

    //void debug();

    IUtilizador get_utilizador_by_ID(String id);

    String get_logged_id();

    void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao, IOException;

    void adicionar_cliente(String nif, String nome, String numTelemovel, String email) throws JaExistenteExcecao, IOException;

    void adicionar_orcamento(IOrcamento orcamento) throws IOException; //**

    void concluir_reparacao(IOrcamento orcamento) throws IOException; //**

    void carregar_cp() throws IOException, JaExistenteExcecao;

    void adicionar_pedido_orcamento(String nifCliente, String modelo, String descricaoEquipamento, String descricaoPedido) throws IOException;

    void adicionar_log(String log, String user_id) throws IOException;

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

    List<IPedido> get_orcamentos_completos();

    IPedido get_pedido_orcamento(int posicao);

    void gerar_orcamento(IPlanoDeTrabalho plano) throws IOException; //**

    List<IOrcamento> get_orcamentos_por_confirmar();

    ICliente get_cliente(String nif);

    void confirmar_orcamento(int num_ref) throws IOException; //**

    List<IOrcamento> get_orcamentos_confirmados();

    IOrcamento get_orcamento(int num_ref);

    IEquipamento getEquipamento(int num_ref);

    void remover_orcamento(int num_ref) throws IOException;

    Map<String,IUtilizador> get_utilizadores();

    void remover_utilizador(String id) throws IOException;

    IEquipamento get_equipamento(int num_ref);

    boolean disponibilidade_pedido_expresso();

    void adicionar_pedido_expresso(String nifCliente, String modelo, String descricaoEquipamento, int tipo) throws IOException;

    IPedido get_pedido_expresso();
    void completa_pedido_expresso() throws IOException;

    String get_logs_tecnicos_simples();

    String get_logs_funcionarios();

    List<LogTecnico> get_logs_tecnicos_simples_extensivos();
}
