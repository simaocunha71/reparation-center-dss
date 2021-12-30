package controller;

import model.*;
import model.interfaces.*;
import model.orcamento.Passo;
import model.orcamento.PlanoDeTrabalho;
import model.orcamento.SubPasso;
import model.pedidos.PedidoExpresso;
import model.utilizadores.Funcionario;
import model.utilizadores.Tecnico;
import view.CRView;
import view.AuxiliarView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CRController {

    private final ICentroReparacoes centro = new CRFacade("cp/utilizadores.csv", "cp/clientes.csv","cp/armazem.csv", "cp/pedidos.csv","cp/orcamentos.csv","cp/logs.txt");
    private final AuxiliarView aux_view = new AuxiliarView();
    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String[] menu_inicial = new String[]{
            "Autenticação"
    };

    private final String[] menu_login = new String[]{
            "User id",
            "Password",
            "Entrar",
    };

    private final String[] menu_principal_gestor = new String[]{
            "Registar cliente",
            "Registar pedido",
            "Gerar orçamento/Criar plano",
            "Confirmar orcamento",
            "Realizar pedido expresso",
            "Processar reparação",
            "Concluir pedido/Entregar equipamento",
            "Lista de funcionários",
            "Lista de técnicos",
            "Registar utilizador",
            "Estatisticas de utilizadores",
            "Logout",
    };

    private final String[] menu_principal_tecnico = new String[]{
            "Gerar orçamento/Criar plano",
            "Realizar pedido expresso",
            "Processar reparação",
            "Logout",
    };

    private final String[] menu_principal_funcionario = new String[]{
            "Registar cliente",
            "Registar pedido",
            "Confirmar orcamento",
            "Concluir pedido/Entregar equipamento",
            "Logout",
    };

    private final String[] menu_registo_cliente = new String[]{
            "Nome",
            "NIF",
            "Telemovel",
            "Email",
            "Guardar e sair",
    };


    private final String[] menu_registo_utilizador = new String[]{
            "User id",
            "Nome",
            "Password",
            "Tipo de Utilizador", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
    };

    private final String[] escolhe_tipo_utilizador = new String[]{
            "Gestor",
            "Funcionário",
            "Técnico"
    };

    private final String[] menu_registo_pedido = new String[]{
            "Serviço Express",
            "Pedido de orçamento",
    };

    private final String[] menu_pedido = new String[]{
            "NIF de cliente",
            "Equipamento",
            "Descrição",
            "Guardar e sair",
    };

    private final String[] menu_pedido_expresso = new String[]{
            "NIF de cliente",
            "Equipamento",
            "Tipo",
            "Guardar e sair",
    };

    private final String[] menu_equipamento_info = new String[]{
            "Modelo",
            "Descricao",
            "Guardar e sair"
    };

    private final String[] menu_plano = new String[]{
            "Adicionar Passo",
            "Apresentar Plano",
            "Guardar e sair"
    };

    private final String[] menu_passo = new String[]{
            "Adicionar subpasso",
            "Apresentar passo",
            "Preco estimado",
            "Duracao estimada",
            "Descrição",
            "Guardar e sair"
    };

    private final String[] menu_sub_passo = new String[]{
            "Apresentar subpasso",
            "Preco estimado",
            "Duracao estimada",
            "Descrição",
            "Guardar e sair"
    };

    private final String[] menu_processar_reparacao = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada, custo gasto até ao momento, tempo gasto até ao momento, percentagem de orçamento gasto;
            "Executar passo", //aberto até o orçamento passar 120% do valor estimado
            "Notificar cliente", //fechado até o orçamento passar 120% do valor estimado.
            "Guardar",
            "Concluir reparacao"
    };

    private final String[] menu_executar_passo = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada
            "Custo real [0]",
            "Duração real [0]",
            "Executar SubPasso",
            "Guardar"
    };

    private final String[] menu_executar_subpasso = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada
            "Custo real [0]",
            "Duração real [0]",
            "Guardar e voltar"
    };


    private final String[] pedidos_expressos = new String[]{
           "Trocar ecrã [Custo 50€]",
           "Instalar sistema operativo [Custo 20€]",
           "Trocar bateria [Custo 25€]",
           "Limpar equipamento [Custo 10€]",
    };

    private final String[] menu_estatisticas = new String[]{
            "Tecnicos Simples",
            "Funcionarios",
            "Tecnicos Extenso",
    };

    private boolean logged = false;

    public CRController() {
    }


    public void run() {
        CRView menu = new CRView("Centro de Reparações", menu_inicial);
        menu.set_handler(1, this::login);
        menu_run(menu);
    }

    private void menu_run(CRView menu){
        try{
            menu.simple_run();
        }
        catch(ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
    }

    private void login() {
        CRView menu = new CRView("Autenticação", menu_login);
        AtomicBoolean credenciais = new AtomicBoolean(false);
        AtomicReference<String> nome_de_utilizador = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        menu.set_pre_condition(3, credenciais::get);

        menu.set_handler(1, ()->{
            aux_view.pergunta_nome();
            nome_de_utilizador.set(scanner.nextLine());
            menu.change_option(1,"User id: ["+nome_de_utilizador+"]");
        });
        menu.set_handler(2, ()->{
            aux_view.pergunta_password();
            password.set(scanner.nextLine());
            menu.change_option(2,"Password: "+ "*".repeat(password.get().length()));
            credenciais.set(true);
        });
        menu.set_handler(3, ()->{
            if(centro.login(nome_de_utilizador.get(),password.get())){
                logged = true;
                menu.confirmation_message("Logged in");
                menu.return_menu();
            }
            else{
                nome_de_utilizador.set(null);
                password.set(null);
                menu.change_option(1,"User id");
                menu.change_option(2,"Password");
                aux_view.mensagem_de_erro("Credenciais inválidas!");
                credenciais.set(false);
            }
        });
        menu_run(menu);
        if(logged) menu_inicial();
    }


    private void menu_inicial() {
        if(centro.logged_gestor())
            menu_inicial_gestor();
        else if (centro.logged_tecnico())
            menu_inicial_tecnico();
        else if (centro.logged_funcionario())
            menu_inicial_funcionario();
    }

    private void menu_inicial_gestor() {
        CRView menu = new CRView("Menu Inicial", menu_principal_gestor);

        menu.set_pre_condition(5,()->!centro.disponibilidade_pedido_expresso());
        menu.set_pre_condition(6, centro::disponibilidade_pedido_expresso);

        menu.set_handler(1,this::registar_cliente);

        menu.set_handler(2,this::registar_pedido);

        menu.set_handler(3,this::lista_de_pedidos_orcamento);

        menu.set_handler(4,this::confirmar_orcamento);
        menu.set_handler(5,this::realizar_pedido_expresso);

        menu.set_handler(6,this::lista_de_equipamentos_para_reparacao);
        menu.set_handler(7,this::concluir_pedido);

        menu.set_handler(8,()-> lista_de_utilizadores(centro.get_utilizadores().values().stream().filter(v->v.getClass().equals(Funcionario.class)).collect(Collectors.toMap(IUtilizador::get_id, Function.identity())),"Lista de Funcionarios"));

        menu.set_handler(9,()-> lista_de_utilizadores(centro.get_utilizadores().values().stream().filter(v->v.getClass().equals(Tecnico.class)).collect(Collectors.toMap(IUtilizador::get_id, Function.identity())),"Lista de Tecnicos"));

        menu.set_handler(10,this::registar_utilizador);

        menu.set_handler(11,this::estatisticas_utilizadores);

        menu.set_handler(12,()->{menu.return_menu();logout();});

        menu_run(menu);
    }



    private void lista_de_pedidos_orcamento() {
        List<IPedido> pedidos = centro.get_pedidos_orcamento();
        String[] pedidosString = new String[pedidos.size()];
        for(int i = 0; i < pedidos.size(); i++){
            IPedido p = pedidos.get(i);
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Data Registo: [").append(p.get_tempo_registo().format(formatter)).append("] ");
            sb.append("Cliente: [NIF.").append(p.get_nif_cliente()).append("] ");
            sb.append("Equipamento: [#").append(p.get_num_registo()).append("]");
            pedidosString[i] = sb.toString();
        }
        CRView menu = new CRView("Pedidos de orçamento",pedidosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= pedidos.size();i.incrementAndGet()){
            int posicao = i.get();
            menu.set_handler(i.get(),()->{
                fazer_plano(pedidos.get(posicao));menu.return_menu();});
        }
        menu_run(menu);
    }

    private void fazer_plano(IPedido pedido) {
        IPlanoDeTrabalho plano = new PlanoDeTrabalho(pedido);
        CRView menu = new CRView("Registar Plano", menu_plano);
        menu.set_pre_condition(3, plano::valida);

        menu.set_handler(1,()->{
            Passo p = adicionar_passo();
            if(p!= null && p.valida()) plano.adicionar_passo(p);
        });

        menu.set_handler(2,()-> aux_view.apresentar_plano(plano.toString()));

        menu.set_handler(3,()->{
            if(plano.valida()) centro.gerar_orcamento(plano);
            menu.return_menu();
        });
        menu_run(menu);
    }

    private Passo adicionar_passo() {
        Passo p = new Passo();
        AtomicBoolean guardar = new AtomicBoolean(false);
        CRView menu = new CRView("Adicionar Passo", menu_passo);

        menu.set_same_pre_condition(new int[]{3,4}, ()-> !p.tem_subpassos());
        menu.set_pre_condition(6, p::valida);

        menu.set_handler(1,()->{
            SubPasso sp = adicionar_subpasso();
            if(sp!=null && sp.valida()) p.adicionar_subpasso(sp);
        });

        menu.set_handler(2,()-> aux_view.apresentar_passo(p.toString()));

        menu.set_handler(3,()->{
            float custoEstimado = scan_float("Custo estimado: ");
            p.set_custo_estimado(custoEstimado);
        });

        menu.set_handler(4,()->{
            float duracaoEstimado = scan_float("Tempo estimado: ");
            p.set_duracao_estimada(duracaoEstimado);
        });

        menu.set_handler(5,()->{
            aux_view.mensagem_normal("Descrição: ");
            String string = scanner.nextLine();
            if(verifica_length(string,25)) p.set_descricao(string);
        });

        menu.set_handler(6,()->{
            guardar.set(true);
            menu.return_menu();
        });

        menu_run(menu);
        if(guardar.get()) return p;
        return null;
    }

    private float scan_float(String message) {
        boolean valida = false;
        float result = 0;
        while(!valida) {
            aux_view.mensagem_normal(message);
            String linha = scanner.nextLine();
            try{
                result = Float.parseFloat(linha);
                valida = true;
            }
            catch (NumberFormatException ignored){
                aux_view.mensagem_de_erro("Insira um valor valido");
            }
        }
        return result;
    }

    private SubPasso adicionar_subpasso() {
        SubPasso sp = new SubPasso();
        AtomicBoolean guardar = new AtomicBoolean(false);
        CRView menu = new CRView("Adicionar SubPasso", menu_sub_passo);
        menu.set_pre_condition(5,sp::valida);

        menu.set_handler(1,()-> aux_view.apresentar_subpasso(sp.toString()));
        menu.set_handler(2,()->{
            float custoEstimado = scan_float("Custo estimado: ");
            sp.set_custo_estimado(custoEstimado);
        });
        menu.set_handler(3,()->{
            float duracaoEstimado = scan_float("Tempo estimado: ");
            sp.set_duracao_estimada(duracaoEstimado);
        });
        menu.set_handler(4,()->{
            aux_view.mensagem_normal("Descrição: ");
            String string = scanner.nextLine();
            if(verifica_length(string,40)) sp.set_descricao(string);
        });

        menu.set_handler(5,()->{
            guardar.set(true);
            menu.return_menu();
        });

        menu_run(menu);
        if(guardar.get()) return sp;
        return null;
    }


    private void menu_inicial_tecnico() {
        CRView menu = new CRView("Menu Inicial", menu_principal_tecnico);

        menu.set_pre_condition(2,()->!centro.disponibilidade_pedido_expresso());
        menu.set_pre_condition(3, centro::disponibilidade_pedido_expresso);

        menu.set_handler(1,this::lista_de_pedidos_orcamento);
        menu.set_handler(2,this::realizar_pedido_expresso);
        menu.set_handler(3,this::lista_de_equipamentos_para_reparacao);
        menu.set_handler(4,()->{menu.return_menu();logout();});

        menu_run(menu);
    }

    private void realizar_pedido_expresso() {
        IPedido p = centro.get_pedido_expresso();
        if(p!=null) {
            if (p.getClass().equals(PedidoExpresso.class)) {
                PedidoExpresso pe = (PedidoExpresso) p;

                CRView menu = new CRView("Cliente: [Nif." + pe.get_nif_cliente() + "] Data de Registo: [" + pe.get_tempo_registo() + "] Equipamento :[#" + pe.get_num_registo() + "] Descricao: [" + pe.get_descricao_pedido() + "] Custo: ["+ pe.get_custo_fixo() + "]", new String[]{"Concluir"});
                menu.set_handler(1, () -> {
                    centro.completa_pedido_expresso();
                    ICliente cliente = centro.get_cliente(p.get_nif_cliente());
                    IEquipamento e = centro.get_equipamento(p.get_num_registo());
                    String log = "0;" + p.get_num_registo() + ";"
                            + e.get_modelo() + ";"
                            + e.get_descricao() + ";"
                            + LocalDateTime.now();
                    centro.adicionar_log(log, centro.get_logged_id());
                    menu.show_info("Cliente notificado para " + cliente.get_num_telemovel());
                    menu.return_menu();
                });
                menu_run(menu);
            }

        }
    }


    private void lista_de_equipamentos_para_reparacao(){
        List<IOrcamento> orcamentos = centro.get_orcamentos_confirmados();
        String[] orcamentosString = new String[orcamentos.size()];
        for(int i =0; i < orcamentos.size() && i < 10 ;i++){
            IOrcamento orcamento = orcamentos.get(i);
            IPlanoDeTrabalho plano = orcamento.get_plano_de_trabalho();
            IPedido pedido = plano.get_pedido();
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Equipamento: [#").append(orcamento.get_num_registo()).append("] ");
            sb.append("Data de Registo: [").append(pedido.get_tempo_registo().format(formatter)).append("] ");
            sb.append("Data de Confirmação: [").append(orcamento.get_data_confirmacao().format(formatter)).append("] ");
            sb.append("Preco estimado: [").append(plano.calcula_custo_estimado()).append("] ");
            sb.append("Tempo estimado: [").append(plano.calcula_duracao_estimada()).append("]");
            orcamentosString[i] = sb.toString();
        }
        CRView menu = new CRView("Lista de Equipamentos para reparação",orcamentosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= orcamentosString.length;i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = orcamentos.get(posicao-1).get_num_registo();
            menu.set_handler(i.get(),()->{processar_reparacao(num_ref);menu.return_menu();});
        }
        menu_run(menu);
    }


    private void processar_reparacao(int num_ref) {
        IOrcamento orcamento = centro.get_orcamento(num_ref);
        IOrcamento clone = orcamento.clone();
        List<String> logs = new ArrayList<>();
        List<String> logsTemporarios = new ArrayList<>();
        CRView menu = new CRView("Processar Reparacao", menu_processar_reparacao);
        if(clone!=null) {
            IPlanoDeTrabalho plano = clone.get_plano_de_trabalho();
            menu.set_pre_condition(2,()->!plano.ultrapassou_120porcento_orcamento() && !plano.concluido() && plano.get_proximo_passo()!=null);
            menu.set_pre_condition(3, plano::ultrapassou_120porcento_orcamento);
            menu.set_pre_condition(4, ()-> plano.valida() && !plano.concluido());
            menu.set_pre_condition(5, ()-> plano.concluido() && !plano.ultrapassou_120porcento_orcamento() );


            menu.set_handler(1, () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Equipamento [#").append(num_ref).append("]\n");
                sb.append("Custo Estimado [").append(plano.calcula_custo_estimado()).append("]\n");
                sb.append("Custo Real [").append(plano.calcula_custo_gasto()).append("]\n");
                sb.append("Percentagem gasta [").append(plano.orcamento_gasto()).append("]\n");
                sb.append("Tempo Estimado [").append(plano.calcula_duracao_estimada()).append("]\n");
                sb.append("Tempo Real [").append(plano.calcula_tempo_gasto()).append("]\n");
                sb.append("Orcamento excedido [").append(plano.ultrapassou_120porcento_orcamento()).append("]");
                menu.show_info(sb);
            });
            menu.set_handler(2,()-> logsTemporarios.addAll(executar_passo(clone)));
            menu.set_handler(3,()->{
                menu.show_info("Cliente notificado, orcamento retornado a lista de espera.");
                clone.desconfirma();
                centro.adicionar_orcamento(clone);
                logs.addAll(logsTemporarios);
            });
            menu.set_handler(4,()->{
                orcamento.carregar(clone);
                centro.adicionar_orcamento(orcamento);
                logs.addAll(logsTemporarios);
            });
            menu.set_handler(5,()->{
                orcamento.carregar(clone);
                centro.concluir_reparacao(clone);
                logs.addAll(logsTemporarios);
                menu.show_info("Reparacao concluida.");
                menu.return_menu();
            });
            menu_run(menu);
            logs.forEach(v-> centro.adicionar_log(v, centro.get_logged_id()));
        }
    }

    private List<String> executar_passo(IOrcamento orcamento) {
        IPlanoDeTrabalho plano = orcamento.get_plano_de_trabalho();
        Passo passo = plano.get_proximo_passo();
        Passo clone = passo.clone();
        List<String> logs = new ArrayList<>();
        List<String> logsTemporarios = new ArrayList<>();
        int total_passos = plano.get_total_passos();
        String title = "Executar Passo ["+clone.get_numero_do_passo()+"/"+total_passos+"]";
        CRView menu = new CRView(title, menu_executar_passo);
        menu.set_same_pre_condition(new int[]{2,3}, ()-> !clone.tem_subpassos());
        menu.set_pre_condition(4, ()-> clone.existe_proximo_subpasso() && plano.calcula_custo_estimado()*1.2 >= plano.calcula_custo_gasto()+clone.calcula_custo_gasto());
        menu.set_pre_condition(5, clone::valida);
        int total_subpassos = clone.get_total_subpassos();
        float percentagem_orcamento = (plano.calcula_custo_gasto())*100/plano.calcula_custo_estimado();

        menu.set_handler(1, () -> {
            float percentagem_gasta = percentagem_orcamento;
            percentagem_gasta += clone.calcula_custo_gasto()*100/plano.calcula_custo_estimado();
            StringBuilder sb = new StringBuilder();
            sb.append("Descricao [#").append(clone.get_descricao()).append("]\n");
            sb.append("Custo Estimado [").append(clone.get_custo_estimado()).append("]\n");
            sb.append("Custo Real [").append(clone.calcula_custo_gasto()).append("]\n");
            sb.append("Tempo Estimado [").append(clone.get_duracao_estimada()).append("]\n");
            sb.append("Tempo Real [").append(clone.calcula_tempo_gasto()).append("]\n");
            sb.append("Realizado [").append(clone.concluido()).append("]\n");
            sb.append("Percentagem gasta [").append(percentagem_gasta).append("]\n");

            menu.show_info(sb);

        });
        menu.set_handler(2,()->{
            clone.set_custo_real(scan_float("Custo Real:"));
            menu.change_option(2,"Custo Real [" + clone.get_custo_real() + "]" );
        });
        menu.set_handler(3,()->{
            clone.set_duracao_real(scan_float("Tempo Real:"));
            menu.change_option(3,"Tempo Real [" + clone.get_duracao_real() + "]" );

        });
        menu.set_handler(4,()->{
                String l = executar_subpasso(clone.get_proximo_subpasso(),total_subpassos,orcamento);
                if(l!=null) logsTemporarios.add(l);
        });
        menu.set_handler(5,()->{
            if(passo.tem_subpassos()){
                passo.carrega(clone);
                if(clone.concluido()){
                    menu.return_menu();
                }
            }else{
                clone.concluir(centro.get_logged_id(),clone.get_custo_real(),clone.get_duracao_real());
                passo.carrega(clone);
                int num_ref = orcamento.get_num_registo();
                logs.add("1;" + num_ref + ";"
                        + centro.get_equipamento(num_ref).get_modelo() + ";"
                        + passo.get_descricao() + ";"
                        + LocalDateTime.now() + ";"
                        + passo.get_duracao_estimada() + ";"
                        + passo.get_duracao_real());
                menu.return_menu();
            }
            logs.addAll(logsTemporarios);
        });
        menu_run(menu);
        return logs;
    }


    private String executar_subpasso(SubPasso subPasso, int total_subpassos, IOrcamento orcamento) {
        AtomicReference<String> log = new AtomicReference<>(null);

        String title = "Executar SubPasso ["+subPasso.get_numero_do_passo()+"/"+total_subpassos+"]";
        CRView menu = new CRView(title, menu_executar_subpasso);
        SubPasso clone = subPasso.clone();
        menu.set_pre_condition(4, subPasso::valida);


        menu.set_handler(1, () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Descricao [#").append(clone.get_descricao()).append("]\n");
            sb.append("Custo Estimado [").append(clone.get_custo_estimado()).append("]\n");
            sb.append("Custo Real [").append(clone.get_custo_real()).append("]\n");
            sb.append("Tempo Estimado [").append(clone.get_duracao_estimada()).append("]\n");
            sb.append("Tempo Real [").append(clone.get_duracao_real()).append("]\n");
            sb.append("Realizado [").append(subPasso.concluido()).append("]\n");
            menu.show_info(sb);
        });
        menu.set_handler(2,()->{
            clone.set_custo_real(scan_float("Custo Real:"));
            menu.change_option(2,"Custo Real [" + clone.get_custo_real() + "]" );
        });
        menu.set_handler(3,()->{
            clone.set_duracao_real(scan_float("Tempo Real:"));
            menu.change_option(3,"Duracao Real [" + clone.get_duracao_real() + "]" );

        });
        menu.set_handler(4,()->{
            clone.concluir(centro.get_logged_id(),clone.get_custo_real(),clone.get_duracao_real());
            subPasso.carrega(clone);
            int num_ref = orcamento.get_num_registo();
            log.set("2;"+num_ref+";"
                    +centro.get_equipamento(num_ref).get_modelo()+";"
                    +subPasso.get_descricao()+";"
                    +LocalDateTime.now()+";"
                    +subPasso.get_duracao_estimada()+";"
                    +subPasso.get_duracao_real());
            menu.return_menu();
        });
        menu_run(menu);
        return log.get();
    }

    private void concluir_pedido() {
        List<IPedido> pedidos = centro.get_pedidos_completos();
        String[] pedidosString = new String[pedidos.size()];
        for(int i =0; i < pedidos.size() && i < 10 ;i++){
            IPedido pedido = pedidos.get(i);
            ICliente cliente = centro.get_cliente(pedido.get_nif_cliente());
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Equipamento: [#").append(pedido.get_num_registo()).append("] ");
            sb.append("Cliente: [").append(cliente.get_nome()).append("] ");
            sb.append("NIF: [").append(cliente.get_nif()).append("] ");
            sb.append("Email: [").append(cliente.get_email()).append("] ");
            sb.append("Telemóvel: [").append(cliente.get_num_telemovel()).append("]");
            pedidosString[i] = sb.toString();
        }
        CRView menu = new CRView("Registo de entrega de equipamento ao cliente",pedidosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= pedidosString.length;i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = pedidos.get(posicao-1).get_num_registo();
            menu.set_handler(i.get(),()->{centro.remover_orcamento(num_ref);menu.return_menu();});
        }
        menu_run(menu);
    }




    private void menu_inicial_funcionario() {
        CRView menu = new CRView("Menu Inicial", menu_principal_funcionario);

        menu.set_handler(1,this::registar_cliente);

        menu.set_handler(2,this::registar_pedido);

        menu.set_handler(3,this::confirmar_orcamento);

        menu.set_handler(4, this::concluir_pedido);

        menu.set_handler(5,()->{menu.return_menu();logout();});

        menu_run(menu);
    }

    private void confirmar_orcamento() {
        List<IOrcamento> orcamentos = centro.get_orcamentos_por_confirmar();
        String[] orcamentosString = new String[orcamentos.size()];
        for(int i =0; i<orcamentos.size();i++){
            IOrcamento orcamento = orcamentos.get(i);
            IPlanoDeTrabalho plano = orcamento.get_plano_de_trabalho();
            IPedido pedido = plano.get_pedido();
            ICliente cliente = centro.get_cliente(pedido.get_nif_cliente());
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Equipamento: [#").append(orcamento.get_num_registo()).append("] ");
            sb.append("Data de registo: [").append(pedido.get_tempo_registo().format(formatter)).append("] ");
            sb.append("Preco estimado: [").append(plano.calcula_custo_estimado()).append("] ");
            sb.append("Tempo estimado: [").append(plano.calcula_duracao_estimada()).append("] ");
            sb.append("Cliente: [Nif.").append(cliente.get_nif()).append("] ");
            sb.append("Email: [").append(cliente.get_email()).append("]");
            orcamentosString[i] = sb.toString();
        }
        CRView menu = new CRView("Lista de Orcamentos por confirmar",orcamentosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= orcamentos.size();i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = orcamentos.get(posicao-1).get_num_registo();
            menu.set_handler(i.get(),()->{
                int opt = menu.read_option_between(1,2,new String[]{"Confirmar","Recusa"});
                if(opt == 1) centro.confirmar_orcamento(num_ref);menu.return_menu();
                if(opt == 2) centro.recusa_orcamento(num_ref);menu.return_menu();
            });
        }
        menu_run(menu);

    }


    private void lista_de_utilizadores(Map<String, IUtilizador> utilizadores, String titulo) {
        String[] utilizadoresString = new String[utilizadores.size()];
        Iterator<IUtilizador> iterator = utilizadores.values().iterator();

        for(int i =0; i<utilizadores.size() && iterator.hasNext();i++){
                IUtilizador f = iterator.next();
                String sb = "User [#" + f.get_id() + "]" +
                        "Nome [" + f.get_nome() + "]";
                utilizadoresString[i] = sb;
        }
        CRView menu = new CRView(titulo,utilizadoresString);
        AtomicInteger i = new AtomicInteger(1);
        iterator = utilizadores.values().iterator();
        for(; i.get() <= utilizadores.size() && iterator.hasNext();i.incrementAndGet()){
            String id = iterator.next().get_id();
            menu.set_handler(i.get(),()-> {
                        int option = menu.read_option_between(1, 2, new String[]{"Remover user", "Voltar"});
                        if (option == 1) {
                            aux_view.mensagem_normal("Confirme a operecao [Y/N]:");
                            String line = scanner.nextLine();
                            if (line.equals("y") || line.equals("Y")) {
                                centro.remover_utilizador(id);
                                menu.return_menu();
                            }
                        }
                    }
            );
        }
        menu_run(menu);

    }


    private void registar_cliente() {
        CRView menu = new CRView("Registar Cliente", menu_registo_cliente);
        AtomicReference<String> nome = new AtomicReference<>();
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicReference<String> telemovel = new AtomicReference<>();
        AtomicReference<String> email = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(6);
        for(int i = 0; i < 4; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.set_pre_condition(5,()-> condicao.stream().noneMatch(k -> k.get() == 0));


        menu.set_handler(1,()->{
            aux_view.pergunta_nome_cliente();
            nome.set(scanner.nextLine());
            menu.change_option(1,"Nome do cliente: "+ nome.get());
            condicao.get(0).set(1);
        });
        menu.set_handler(2,()->{
            aux_view.pergunta_nif_do_cliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifica_int(nif.get()) && verifica_9digitos(nif.get())) {
                menu.change_option(2, "NIF do cliente: " + nif.get());
                condicao.get(1).set(1);
            }
            else{
                aux_view.mensagem_de_erro("NIF inválido!");
                nif.set(auxNif);
            }
        });
        menu.set_handler(3,()->{
            aux_view.pergunta_numero_de_telemovel();
            String auxTelemovel = telemovel.get();
            telemovel.set(scanner.nextLine());
            if(verifica_int(telemovel.get()) && verifica_9digitos(telemovel.get())) {
                menu.change_option(3, "Telemóvel do cliente: " + telemovel.get());
                condicao.get(2).set(1);
            }
            else{
                aux_view.mensagem_de_erro("Telemóvel inválido!");
                telemovel.set(auxTelemovel);
            }
        });
        menu.set_handler(4, ()->{
            aux_view.pergunta_email();
            String auxEmail = email.get();
            email.set(scanner.nextLine());
            if(verifica_email(email.get())){
                menu.change_option(4,"Email do cliente: "+email.get());
                condicao.get(3).set(1);
            }
            else{
                email.set(auxEmail);
            }
        });
        menu.set_handler(5,()->{
            centro.adicionar_cliente(nif.get(),nome.get(),telemovel.get(),email.get());
            menu.return_menu();
        });
        menu_run(menu);
    }


    private void registar_utilizador() {
        CRView menu = new CRView("Registar Utilizador", menu_registo_utilizador);
        AtomicReference<String> id = new AtomicReference<>();
        AtomicReference<String> nome = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        AtomicInteger tipoUtilizador = new AtomicInteger(-1);

        List<AtomicInteger> condicao = new ArrayList<>(4);
        for(int i = 0; i < 4; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.set_pre_condition(5,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.set_handler(1,()->{
            aux_view.pergunta_id();
            String old = id.get();
            id.set(scanner.nextLine());
            if(!centro.existe_utilizador(id.get())){
                menu.change_option(1,"Id: " + id.get());
                condicao.get(0).set(1);
            }else {
                id.set(old);
                aux_view.mensagem_de_erro("Id invalido");
            }
        });

        menu.set_handler(2,()->{
            aux_view.mensagem_normal("Nome: ");
            nome.set(scanner.nextLine());
            menu.change_option(2,"Nome: " + nome.get());
            condicao.get(1).set(1);
        });

        menu.set_handler(3,()->{
            aux_view.mensagem_normal("Password: ");
            password.set(scanner.nextLine());
            menu.change_option(3,"Password: "+ "*".repeat(password.get().length()));
            condicao.get(2).set(1);
        });

        menu.set_handler(4, ()-> {
            tipoUtilizador.set(menu.read_option_between(1,3, escolhe_tipo_utilizador));
            if(tipoUtilizador.get()!=-1){
                switch (tipoUtilizador.get()) {
                    case 1 -> menu.change_option(4,"Gestor");
                    case 2 -> menu.change_option(4,"Funcionario");
                    case 3 -> menu.change_option(4,"Tecnico");
                }
                condicao.get(3).set(1);
            }
            else condicao.get(3).set(0);
        });

        menu.set_handler(5, ()-> {
            centro.adicionar_utilizador(id.get(),nome.get(),password.get(),tipoUtilizador.get());
            menu.return_menu();
        });
        menu_run(menu);
    }


    private void logout() {
        this.logged = false;
        centro.logout();
        login();
    }

    private void registar_pedido() {
        CRView menu = new CRView("Registo Pedido", menu_registo_pedido);
        menu.set_pre_condition(1, centro::disponibilidade_pedido_expresso);

        menu.set_handler(1,()->{
            pedido_expresso();menu.return_menu();});
        menu.set_handler(2,()->{
            registar_pedido_orcamento();menu.return_menu();});


        menu_run(menu);
    }

    private void pedido_expresso(){
        CRView menu = new CRView("Pedido Expresso", menu_pedido_expresso);
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicInteger tipo = new AtomicInteger(0);
        AtomicReference<String> modelo = new AtomicReference<>();
        AtomicReference<String> descricaoEquipamento = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.set_pre_condition(4,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.set_handler(1,()->{
            aux_view.pergunta_nif_do_cliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifica_int(nif.get()) && verifica_9digitos(nif.get())) {
                if(centro.existe_cliente(nif.get())) {
                    menu.change_option(1, "NIF do cliente: " + nif.get());
                    condicao.get(0).set(1);
                }
                else{
                    aux_view.mensagem_de_erro("Cliente não registado!");
                }
            }
            else{
                aux_view.mensagem_de_erro("Nif inválido!");
                nif.set(auxNif);
            }
        });
        menu.set_handler(2, ()->{
            equipamento_info(modelo,descricaoEquipamento);

            if(modelo.get() == null){
                menu.change_option(2,"Equipamento");
                condicao.get(1).set(0);
            }
            else {
                condicao.get(1).set(1);
                menu.change_option(2,"Equipamento [Registado #"+(centro.get_ultimo_numero_de_registo_equipamento()+1)+"]");
            }
        });
        menu.set_handler(3, ()->{
            aux_view.mensagem_normal("Tipo de Pedido Expresso: ");
            tipo.set(menu.read_option_between(1,4, pedidos_expressos));
            menu.change_option(3,"Tipo: "+tipo.get());
            condicao.get(2).set(1);
        });
        menu.set_handler(4, ()->{
            centro.adicionar_pedido_expresso(nif.get(),modelo.get(),descricaoEquipamento.get(),tipo.get());
            menu.return_menu();
        });

        menu_run(menu);
    }


    private void registar_pedido_orcamento() {
        CRView menu = new CRView("Pedido Orçamento", menu_pedido);
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicReference<String> descricaoPedido = new AtomicReference<>();
        AtomicReference<String> modelo = new AtomicReference<>();
        AtomicReference<String> descricaoEquipamento = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.set_pre_condition(4,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.set_handler(1,()->{
            aux_view.pergunta_nif_do_cliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifica_int(nif.get()) && verifica_9digitos(nif.get())) {
                if(centro.existe_cliente(nif.get())) {
                    menu.change_option(1, "NIF do cliente: " + nif.get());
                    condicao.get(0).set(1);
                }
                else{
                    aux_view.mensagem_de_erro("Cliente não registado!");
                }
            }
            else{
                aux_view.mensagem_de_erro("Nif inválido!");
                nif.set(auxNif);
            }
        });
        menu.set_handler(2, ()->{
            equipamento_info(modelo,descricaoEquipamento);

            if(modelo.get() == null){
                menu.change_option(2,"Equipamento");
                condicao.get(1).set(0);
            }
            else {
                condicao.get(1).set(1);
                menu.change_option(2,"Equipamento [Registado #"+(centro.get_ultimo_numero_de_registo_equipamento()+1)+"]");
            }
        });
        menu.set_handler(3, ()->{
            aux_view.mensagem_normal("Descrição do Pedido: ");
            String auxDescricao = descricaoPedido.get();
            descricaoPedido.set(scanner.nextLine());
            if(verifica_length(descricaoPedido.get(),25)) {
                menu.change_option(3,"Descrição: "+descricaoPedido.get());
                condicao.get(2).set(1);
            }
            else {
                descricaoPedido.set(auxDescricao);
            }
        });
        menu.set_handler(4, ()->{
            centro.adicionar_pedido_orcamento(nif.get(),modelo.get(),descricaoEquipamento.get(),descricaoPedido.get());
            menu.return_menu();
        });

        menu_run(menu);
    }

    private void equipamento_info(AtomicReference<String> modelo, AtomicReference<String> descricao) {
        CRView menu = new CRView("Equipamento Info", menu_equipamento_info);

        List<AtomicInteger> condicao = new ArrayList<>(2);
        for(int i = 0; i < 2; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.set_pre_condition(3,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.set_handler(1, ()->{
            aux_view.mensagem_normal("Modelo do Equipamento: ");
            String auxModelo = modelo.get();
            modelo.set(scanner.nextLine());
            if(verifica_length(modelo.get(),25)){
                menu.change_option(1,"Modelo: "+modelo.get());
                condicao.get(0).set(1);
            }
            else {
                modelo.set(auxModelo);
            }
        });
        menu.set_handler(2, ()->{
            aux_view.mensagem_normal("Descrição do Equipamento: ");
            String auxDescricao = descricao.get();
            descricao.set(scanner.nextLine());
            if(verifica_length(descricao.get(),25)) {
                menu.change_option(2,"Descrição: "+descricao.get());
                condicao.get(1).set(1);
                }
            else {
                descricao.set(auxDescricao);
            }
        });

        menu.set_handler(3, menu::return_menu);

        menu_run(menu);
    }


    private boolean verifica_int(String string){
        try{
            Integer.parseInt(string);
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private boolean verifica_length(String string, int limit){
        return string.length() <= limit;
    }

    private boolean verifica_9digitos(String string){
        return string.length() == 9;
    }

    private boolean verifica_email(String email){
        String[] split_email = email.split("@");
        try{
            return split_email[0].length() > 0 && split_email[1].length() > 0;
        }
        catch(ArrayIndexOutOfBoundsException e){
            aux_view.mensagem_de_erro("Email inválido!");
            return false;
        }
    }

    private void estatisticas_utilizadores() {
        CRView menu = new CRView("Estatisticas de utilizadores", menu_estatisticas);

        menu.set_handler(1,this::estatisticas_tecnicos_simples);
        menu.set_handler(2,this::estatisticas_funcionarios);
        menu.set_handler(3,this::estatisticas_tecnicos_extensivas);
        menu_run(menu);
    }

    private void estatisticas_tecnicos_simples() {
        String estatisticas = centro.get_logs_tecnicos_simples();
        CRView menu = new CRView("",new String[]{});
        menu.show_info(estatisticas);
    }



    private void estatisticas_funcionarios() {
        String estatisticas = centro.get_logs_funcionarios();
        CRView menu = new CRView("",new String[]{});
        menu.show_info(estatisticas);
    }

    private void estatisticas_tecnicos_extensivas() {
        List<LogTecnico> tecnicosLogs = centro.get_logs_tecnicos_extensivos();
        String[] tecnicos = new String[tecnicosLogs.size()];
        for(int i =0; i < tecnicosLogs.size() && i < 10 ;i++){
            String sb = "Tecnico [" + tecnicosLogs.get(i).get_user_id() + "]";
            tecnicos[i] = sb;
        }
        CRView menu = new CRView("Lista de Tecnicosr",tecnicos);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= tecnicos.length;i.incrementAndGet()){
            int posicao = i.get() - 1;
            menu.set_handler(i.get(),()->{menu.show_info(tecnicosLogs.get(posicao).estatisticas_extensivas());menu.return_menu();});
        }

        menu_run(menu);

    }


}
