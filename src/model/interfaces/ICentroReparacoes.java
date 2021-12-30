package model.interfaces;

import model.LogTecnico;
import model.excecoes.JaExistenteExcecao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ICentroReparacoes extends ISessao{


    IUtilizador get_utilizador(String id);

    String get_logged_id();

    void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao;

    void adicionar_cliente(String nif, String nome, String numero_telemovel, String email) throws JaExistenteExcecao;

    void adicionar_orcamento(IOrcamento orcamento);

    void concluir_reparacao(IOrcamento orcamento);

    void carregar_cp() throws IOException, JaExistenteExcecao;

    void adicionar_pedido_orcamento(String nif_cliente, String modelo, String descricao_equipamento, String descricao_pedido);

    void adicionar_log(String log, String user_id);

    boolean existe_utilizador(String id);

    boolean existe_cliente(String nif);

    int get_ultimo_numero_de_registo_equipamento();

    List<IPedido> get_pedidos_orcamento();

    List<IPedido> get_pedidos_completos();

    void gerar_orcamento(IPlanoDeTrabalho plano);

    List<IOrcamento> get_orcamentos_por_confirmar();

    ICliente get_cliente(String nif);

    void confirmar_orcamento(int num_registo);

    List<IOrcamento> get_orcamentos_confirmados();

    IOrcamento get_orcamento(int num_registo);


    void recusa_orcamento(int num_registo);

    void remover_orcamento(int num_registo);

    Map<String,IUtilizador> get_utilizadores();

    void remover_utilizador(String id);

    IEquipamento get_equipamento(int num_registo);

    boolean disponibilidade_pedido_expresso();

    void adicionar_pedido_expresso(String nif_cliente, String modelo_equipamento, String descricao_equipamento, int tipo);

    IPedido get_pedido_expresso();

    void completa_pedido_expresso();

    String get_logs_tecnicos_simples();

    String get_logs_funcionarios();

    List<LogTecnico> get_logs_tecnicos_extensivos();
}
