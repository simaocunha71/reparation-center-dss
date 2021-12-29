package controller;

import model.*;
import model.excecoes.JaExistenteExcecao;
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
    private final AuxiliarView auxView = new AuxiliarView();
    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String[] menuInicial = new String[]{
            "Autenticação"
    };

    private final String[] menuLogin = new String[]{
            "User id",
            "Password",
            "Entrar",
    };

    private final String[] menuPrincipalGestor = new String[]{
            "Registar cliente",
            "Registar pedido",
            "Gerar orçamento/Criar plano",
            "Confirmar orcamento",
            "Realizar pedido expresso",
            "Processar reparação",
            "Concluir pedido",
            "Lista de funcionários",
            "Lista de técnicos",
            "Registar utilizador",
            "Logout",
    };

    private final String[] menuPrincipalTecnico = new String[]{
            "Gerar orçamento/Criar plano",
            "Realizar pedido expresso",
            "Processar reparação",
            "Logout",
    };

    private final String[] menuPrincipalFuncionario = new String[]{
            "Registar cliente",
            "Registar pedido",
            "Confirmar orcamento",
            "Concluir pedido",
            "Logout",
    };

    private final String[] menuRegistoCliente = new String[]{
            "Nome",
            "NIF",
            "Telemovel",
            "Email", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
    };


    private final String[] menuRegistoUtilizador = new String[]{
            "User id",
            "Nome",
            "Password",
            "Tipo de Utilizador", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
    };

    private final String[] escolheTipoUtilizador = new String[]{
            "Gestor",
            "Funcionário",
            "Técnico"
    };

    private final String[] menuRegistoPedido = new String[]{
            "Serviço Express",
            "Pedido de orçamento",
    };

    private final String[] menuPedido = new String[]{
            "NIF de cliente",
            "Equipamento",
            "Descrição",
            "Guardar e sair",
    };

    private final String[] menuEquipamentoInfo = new String[]{
            "Modelo",
            "Descricao",
            "Guardar e sair"
    };

    private final String[] menuPlano = new String[]{
            "Adicionar Passo",
            "Apresentar Plano",
            "Guardar e sair"
    };

    private final String[] menuPasso = new String[]{
            "Adicionar subpasso",
            "Apresentar passo",
            "Preco estimado",
            "Duracao estimada",
            "Descrição",
            "Guardar e sair"
    };

    private final String[] menuSubPasso = new String[]{
            "Apresentar subpasso",
            "Preco estimado",
            "Duracao estimada",
            "Descrição",
            "Guardar e sair"
    };

    private final String[] menuProcessarReparacao = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada, custo gasto até ao momento, tempo gasto até ao momento, percentagem de orçamento gasto;
            "Executar passo", //aberto até o orçamento passar 120% do valor estimado
            "Notificar cliente", //fechado até o orçamento passar 120% do valor estimado.
            "Guardar",
            "Concluir reparacao"
    };

    private final String[] menuExecutarPasso = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada
            "Custo real [0]",
            "Duração real [0]",
            "Executar SubPasso",
            "Guardar"
    };

    private final String[] menuExecutarSubPasso = new String[]{
            "Apresentar informações", //descriçao, custo estimado, duração estimada
            "Custo real [0]",
            "Duração real [0]",
            "Guardar e voltar"
    };

    private final String[] menuAtualizarUtilizador = new String[]{
            "Nome",
            "Password",
            "Remover e voltar",
            "Guardar e voltar"
    };

    private final String[] pedidosExpressos = new String[]{
           "Trocar ecrã [Custo 50€]",
           "Instalar sistema operativo [Custo 20€]",
           "Trocar bateria [Custo 25€]",
           "Limpar equipamento [Custo 10€]",
    };


    private boolean logged = false;

    public CRController() throws IOException {
    }


    public void run() throws IOException, ClassNotFoundException {

        //centro.debug();

        CRView menu = new CRView("Centro de Reparações",menuInicial);
        menu.setHandler(1, this::login);
        menu.simpleRun();
    }

    private void login() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Autenticação", menuLogin);
        AtomicBoolean credenciais = new AtomicBoolean(false);
        AtomicReference<String> nomeDeUtilizador = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        menu.setPreCondition(3, credenciais::get);

        menu.setHandler(1, ()->{
            auxView.perguntaNomeDeUtilizador();
            nomeDeUtilizador.set(scanner.nextLine());
            menu.changeOption(1,"User id: ["+nomeDeUtilizador+"]");
        });
        menu.setHandler(2, ()->{
            auxView.perguntaPasseDeUtilizador();
            password.set(scanner.nextLine());
            StringBuilder credentials = new StringBuilder();
            for(int i = 0; i<password.get().length();i++) credentials.append("*");
            menu.changeOption(2,"Password: "+ credentials);
            credenciais.set(true);
        });
        menu.setHandler(3, ()->{
            if(centro.login(nomeDeUtilizador.get(),password.get())){
                logged = true;
                menu.confirmationMessage("Logged in");
                menu.returnMenu();
            }
            else{
                nomeDeUtilizador.set(null);
                password.set(null);
                menu.changeOption(1,"User id");
                menu.changeOption(2,"Password");
                auxView.errorMessage("Credenciais inválidas!");
                credenciais.set(false);
            }
        });
        menu.simpleRun();
        if(logged) menuInicial();
    }


    private void menuInicial() throws IOException, ClassNotFoundException {
        if(centro.logged_gestor())
            menuInicialGestor();
        else if (centro.logged_tecnico())
            menuInicialTecnico();
        else if (centro.logged_funcionario())
            menuInicialFuncionario();
    }

    private void menuInicialGestor() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalGestor);

        menu.setPreCondition(5,()->!centro.disponibilidade_pedido_expresso());
        menu.setPreCondition(6, centro::disponibilidade_pedido_expresso);

        menu.setHandler(1,this::registarCliente);

        menu.setHandler(2,this::registarPedido);

        menu.setHandler(3,this::listaDePedidosOrcamento);

        menu.setHandler(4,this::confirmarOrcamento);
        menu.setHandler(5,this::realizarPedidoExpresso);

        menu.setHandler(6,this::listaDeEquipamentosReparacao);
        menu.setHandler(7,this::concluir_pedido);

        menu.setHandler(8,()->listaDeUsuarios(centro.get_utilizadores().values().stream().filter(v->v.getClass().equals(Funcionario.class)).collect(Collectors.toMap(IUtilizador::getId, Function.identity())),"Lista de Funcionarios"));

        menu.setHandler(9,()->listaDeUsuarios(centro.get_utilizadores().values().stream().filter(v->v.getClass().equals(Tecnico.class)).collect(Collectors.toMap(IUtilizador::getId, Function.identity())),"Lista de Tecnicos"));

        menu.setHandler(10,this::registarUtilizador);
        menu.setHandler(11,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }




    private void listaDePedidosOrcamento() throws IOException, ClassNotFoundException {
        List<String> pedidos = centro.get_pedidos_orcamento();
        CRView menu = new CRView("Pedidos de orçamento",pedidos.toArray(new String[0]));
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= pedidos.size();i.incrementAndGet()){
            int posicao = i.get();
            menu.setHandler(i.get(),()->{fazerPlano(posicao);menu.returnMenu();});
        }
        menu.simpleRun();
    }

    private void fazerPlano(int i) throws IOException, ClassNotFoundException {
        IPedido pedido = centro.get_pedido_orcamento(i);
        IPlanoDeTrabalho plano = new PlanoDeTrabalho(pedido);
        CRView menu = new CRView("Registar Plano",menuPlano);
        menu.setPreCondition(3, plano::valida);

        menu.setHandler(1,()->{
            Passo p = adicionarPasso();
            if(p!= null && p.valida()) plano.adicionar_passo(p);
        });

        menu.setHandler(2,()->{
            auxView.apresentarPlano(plano.toString());
        });

        menu.setHandler(3,()->{
            if(plano.valida()) centro.gerar_orcamento(plano);
            menu.returnMenu();
        });
        menu.simpleRun();
    }

    private Passo adicionarPasso() throws IOException, ClassNotFoundException {
        Passo p = new Passo();
        AtomicBoolean guardar = new AtomicBoolean(false);
        CRView menu = new CRView("Adicionar Passo",menuPasso);

        menu.setSamePreCondition(new int[]{3,4}, ()-> !p.temSubPassos());
        menu.setPreCondition(6, p::valida);

        menu.setHandler(1,()->{
            SubPasso sp = adicionarSubPasso();
            if(sp!=null && sp.valida()) p.adicionar_subpasso(sp);
        });

        menu.setHandler(2,()->{
            auxView.apresentarPasso(p.toString());
        });

        menu.setHandler(3,()->{
            float custoEstimado = scanFloat("Custo estimado: ");
            p.setCustoEstimado(custoEstimado);
        });

        menu.setHandler(4,()->{
            float duracaoEstimado = scanFloat("Tempo estimado: ");
            p.setDuracaoEstimada(duracaoEstimado);
        });

        menu.setHandler(5,()->{
            auxView.normalMessage("Descrição: ");
            String string = scanner.nextLine();
            if(verifLength(string,25)) p.setDescricao(string);
        });

        menu.setHandler(6,()->{
            guardar.set(true);
            menu.returnMenu();
        });

        menu.simpleRun();
        if(guardar.get()) return p;
        return null;
    }

    private float scanFloat(String message) {
        boolean valida = false;
        float result = 0;
        while(!valida) {
            auxView.normalMessage(message);
            String linha = scanner.nextLine();
            try{
                result = Float.parseFloat(linha);
                valida = true;
            }
            catch (NumberFormatException ignored){
                auxView.errorMessage("Insira um valor valido");
            }
        }
        return result;
    }

    private SubPasso adicionarSubPasso() throws IOException, ClassNotFoundException {
        SubPasso sp = new SubPasso();
        AtomicBoolean guardar = new AtomicBoolean(false);
        CRView menu = new CRView("Adicionar SubPasso",menuSubPasso);
        menu.setPreCondition(5,sp::valida);

        menu.setHandler(1,()->{
            auxView.apresentarSubPasso(sp.toString());
        });
        menu.setHandler(2,()->{
            float custoEstimado = scanFloat("Custo estimado: ");
            sp.setCustoEstimado(custoEstimado);
        });
        menu.setHandler(3,()->{
            float duracaoEstimado = scanFloat("Tempo estimado: ");
            sp.setDuracaoEstimada(duracaoEstimado);
        });
        menu.setHandler(4,()->{
            auxView.normalMessage("Descrição: ");
            String string = scanner.nextLine();
            if(verifLength(string,25)) sp.setDescricao(string);
        });

        menu.setHandler(5,()->{
            guardar.set(true);
            menu.returnMenu();
        });

        menu.simpleRun();
        if(guardar.get()) return sp;
        return null;
    }


    private void menuInicialTecnico() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalTecnico);

        menu.setPreCondition(2,()->!centro.disponibilidade_pedido_expresso());
        menu.setPreCondition(3, centro::disponibilidade_pedido_expresso);

        menu.setHandler(1,this::listaDePedidosOrcamento);
        menu.setHandler(2,this::realizarPedidoExpresso);
        menu.setHandler(3,this::listaDeEquipamentosReparacao);
        menu.setHandler(4,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }

    private void realizarPedidoExpresso() throws IOException, ClassNotFoundException {
        IPedido p = centro.get_pedido_expresso();
        if(p!=null) {
            System.out.println("DEBUG p!=NULL");
            int tipo = 0;
            if (p.getClass().equals(PedidoExpresso.class)) {
                tipo = ((PedidoExpresso) p).getTipo();
            }
            CRView menu = new CRView("Cliente: [" + p.getNifCliente() + "] Data de Registo: [" + p.getTempoRegisto() + "] Equipamento :[#" + p.getNumeroRegistoEquipamento() + "] Tipo: [" + tipo + "]", new String[]{"Concluir"});
            menu.setHandler(1, () -> {
                centro.completa_pedido_expresso();
                ICliente cliente = centro.get_cliente(p.getNifCliente());
                menu.showInfo("Cliente notificado para "+cliente.getNumTelemovel());
                menu.returnMenu();
            });

            menu.simpleRun();
        }
    }


    private void listaDeEquipamentosReparacao() throws IOException, ClassNotFoundException {
        List<IOrcamento> orcamentos = centro.get_orcamentos_confirmados();
        String[] orcamentosString = new String[orcamentos.size()];
        for(int i =0; i < orcamentos.size() && i < 10 ;i++){
            IOrcamento orcamento = orcamentos.get(i);
            IPedido pedido = orcamento.get_pedido();
            String sb = "Equipamento [#" + orcamento.get_num_ref() + "]|" +
                    "Data de Registo [" + pedido.getTempoRegisto().format(formatter) + "]|" +
                    "Data de Confirmação [" + orcamento.getDataConfirmacao().format(formatter) + "]|" +
                    "Preco estimado [" + orcamento.calcula_gasto_estimado() + "]|" +
                    "Tempo estimado [" + orcamento.calcula_duracao_estimada() + "]";
            orcamentosString[i] = sb;
        }
        CRView menu = new CRView("Lista de Equipamentos a reparar",orcamentosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= orcamentosString.length;i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = orcamentos.get(posicao-1).get_num_ref();
            menu.setHandler(i.get(),()->{processar_reparacao(num_ref);menu.returnMenu();});
        }
        menu.simpleRun();
    }


    private void processar_reparacao(int num_ref) throws IOException, ClassNotFoundException {
        IOrcamento orcamento = centro.get_orcamento(num_ref);
        IOrcamento clone = orcamento.clone();
        List<String> logs = new ArrayList<>();
        List<String> logsTemporarios = new ArrayList<>();
        CRView menu = new CRView("Processar Reparacao",menuProcessarReparacao);
        if(clone!=null) {
            menu.setPreCondition(2,()->!clone.ultrapassou_120porcento_orcamento() && !clone.concluido() && clone.get_proximo_passo()!=null);
            menu.setPreCondition(3, clone::ultrapassou_120porcento_orcamento);
            menu.setPreCondition(4, ()-> clone.valida() && !clone.concluido());
            menu.setPreCondition(5, ()-> clone.concluido() && !clone.ultrapassou_120porcento_orcamento() );


            menu.setHandler(1, () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Equipamento [#" + num_ref +"]\n")
                        .append("Custo Estimado [" + clone.calcula_gasto_estimado() + "]\n")
                        .append("Custo Real [" + clone.calcula_custo_gasto() + "]\n")
                        .append("Percentagem gasta [" + clone.orcamento_gasto() + "]\n")
                        .append("Tempo Estimado [" + clone.calcula_duracao_estimada() + "]\n")
                        .append("Tempo Real [" + clone.calcula_tempo_gasto() + "]\n")
                        .append("Orcamento excedido [" + clone.ultrapassou_120porcento_orcamento() +"]");
                menu.showInfo(sb);
            });
            menu.setHandler(2,()->{
                logsTemporarios.addAll(executarPasso(clone));
            });
            menu.setHandler(3,()->{
                menu.showInfo("Cliente notificado, orcamento retornado a lista de espera.");
                clone.desconfirma();
                centro.adicionar_orcamento(clone);
                logs.addAll(logsTemporarios);
            });
            menu.setHandler(4,()->{
                orcamento.carregar(clone);
                centro.adicionar_orcamento(orcamento);
                logs.addAll(logsTemporarios);
            });
            menu.setHandler(5,()->{
                orcamento.carregar(clone);
                centro.concluir_reparacao(clone);
                logs.addAll(logsTemporarios);
                menu.showInfo("Reparacao concluida.");
                menu.returnMenu();
            });
            menu.simpleRun();
            System.out.println(logsTemporarios.size());
            System.out.println(logs.size());
            logs.forEach(v-> {
                try {
                    centro.adicionar_log(v, centro.get_logged_id());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private List<String> executarPasso(IOrcamento orcamento) throws IOException, ClassNotFoundException {
        Passo passo = orcamento.get_proximo_passo();
        Passo clone = passo.clone();
        List<String> logs = new ArrayList<>();
        List<String> logsTemporarios = new ArrayList<>();
        int total_passos = orcamento.get_total_passos();
        String title = "Executar Passo ["+clone.getNumero_passo()+"/"+total_passos+"]";
        CRView menu = new CRView(title,menuExecutarPasso);
        menu.setSamePreCondition(new int[]{2,3}, ()-> !clone.temSubPassos());
        menu.setPreCondition(4, ()-> clone.existe_proximo_subpasso() && orcamento.calcula_gasto_estimado()*1.2 >= orcamento.calcula_custo_gasto()+clone.calcula_custo_gasto());
        menu.setPreCondition(5, clone::valida);
        int total_subpassos = clone.get_total_subpassos();
        float percentagem_orcamento = (orcamento.calcula_custo_gasto())*100/orcamento.calcula_gasto_estimado();

        menu.setHandler(1, () -> {
            float percentagem_gasta = percentagem_orcamento;
            percentagem_gasta += clone.calcula_custo_gasto()*100/orcamento.calcula_gasto_estimado();
            StringBuilder sb = new StringBuilder();
            sb.append("Descricao [#" + clone.getDescricao() +"]\n")
                    .append("Custo Estimado [" + clone.getCustoEstimado() + "]\n")
                    .append("Custo Real [" + clone.calcula_custo_gasto() + "]\n")
                    .append("Tempo Estimado [" + clone.getDuracaoEstimada() + "]\n")
                    .append("Tempo Real [" + clone.calcula_tempo_gasto() + "]\n")
                    .append("Realizado [" + clone.concluido() + "]\n")
                    .append("Percentagem gasta [" + percentagem_gasta + "]\n");

            menu.showInfo(sb);

        });
        menu.setHandler(2,()->{
            clone.setCustoReal(scanFloat("Custo Real:"));
            menu.changeOption(2,"Custo Real [" + clone.getCustoReal() + "]" );
        });
        menu.setHandler(3,()->{
            clone.setDuracaoReal(scanFloat("Tempo Real:"));
            menu.changeOption(3,"Tempo Real [" + clone.getDuracaoReal() + "]" );

        });
        menu.setHandler(4,()->{
                String l = executarSubpasso(clone.get_proximo_subpasso(),total_subpassos,orcamento);
                if(l!=null) logsTemporarios.add(l);
        });
        menu.setHandler(5,()->{
            if(passo.temSubPassos()){
                passo.carrega(clone);
                if(clone.concluido()){
                    menu.returnMenu();
                }
            }else{
                clone.concluir(centro.get_logged_id(),clone.getCustoReal(),clone.getDuracaoReal());
                passo.carrega(clone);
                int num_ref = orcamento.get_num_ref();
                logs.add("1;" + num_ref + ";"
                        + centro.get_equipamento(num_ref).getModelo() + ";"
                        + passo.getDescricao() + ";"
                        + LocalDateTime.now() + ";"
                        + passo.getDuracaoEstimada() + ";"
                        + passo.getDuracaoReal());
                menu.returnMenu();
            }
            logs.addAll(logsTemporarios);
        });
        menu.simpleRun();
        return logs;
    }


    private String executarSubpasso(SubPasso subPasso, int total_subpassos,IOrcamento orcamento) throws IOException, ClassNotFoundException {
        AtomicReference<String> log = new AtomicReference<>(null);

        String title = "Executar SubPasso ["+subPasso.getNumero_subpasso()+"/"+total_subpassos+"]";
        CRView menu = new CRView(title,menuExecutarSubPasso);
        SubPasso clone = subPasso.clone();
        menu.setPreCondition(4, subPasso::valida);


        menu.setHandler(1, () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Descricao [#" + clone.getDescricao() +"]\n")
                    .append("Custo Estimado [" + clone.getCustoEstimado() + "]\n")
                    .append("Custo Real [" + clone.getCustoReal() + "]\n")
                    .append("Tempo Estimado [" + clone.getDuracaoEstimada() + "]\n")
                    .append("Tempo Real [" + clone.getDuracaoReal() + "]\n")
                    .append("Realizado [" + subPasso.concluido() + "]\n");
            menu.showInfo(sb);
        });
        menu.setHandler(2,()->{
            clone.setCustoReal(scanFloat("Custo Real:"));
            menu.changeOption(2,"Custo Real [" + clone.getCustoReal() + "]" );
        });
        menu.setHandler(3,()->{
            clone.setDuracaoReal(scanFloat("Tempo Real:"));
            menu.changeOption(3,"Duracao Real [" + clone.getDuracaoReal() + "]" );

        });
        menu.setHandler(4,()->{
            clone.concluir(centro.get_logged_id(),clone.getCustoReal(),clone.getDuracaoReal());
            subPasso.carrega(clone);
            int num_ref = orcamento.get_num_ref();
            log.set("2;"+num_ref+";"
                    +centro.get_equipamento(num_ref).getModelo()+";"
                    +subPasso.getDescricao()+";"
                    +LocalDateTime.now()+";"
                    +subPasso.getDuracaoEstimada()+";"
                    +subPasso.getDuracaoReal());
            menu.returnMenu();
        });
        menu.simpleRun();
        return log.get();
    }

    private void concluir_pedido() throws IOException, ClassNotFoundException {
        List<IOrcamento> orcamentos = centro.get_orcamentos_completos();
        String[] orcamentosString = new String[orcamentos.size()];
        for(int i =0; i < orcamentos.size() && i < 10 ;i++){
            IOrcamento orcamento = orcamentos.get(i);
            IPedido pedido = orcamento.get_pedido();
            ICliente cliente = centro.get_cliente(pedido.getNifCliente());
            String sb = "Equipamento [#" + orcamento.get_num_ref() + "]|" +
                    "Cliente [" + cliente.getNome() + "]" +
                    "Nif [" + cliente.getNif() + "]" +
                    "Email [" + cliente.getEmail() + "]" +
                    "Telemovel [" + cliente.getNumTelemovel() + "]";
            orcamentosString[i] = sb;
        }
        CRView menu = new CRView("Lista de pedidos completos",orcamentosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= orcamentosString.length;i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = orcamentos.get(posicao-1).get_num_ref();
            menu.setHandler(i.get(),()->{centro.remover_orcamento(num_ref);menu.returnMenu();});
        }
        menu.simpleRun();
    }




    private void menuInicialFuncionario() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalFuncionario);

        menu.setHandler(1,this::registarCliente);

        menu.setHandler(2,this::registarPedido);

        menu.setHandler(3,this::confirmarOrcamento);

        menu.setHandler(4, this::concluir_pedido);

        menu.setHandler(5,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }

    private void confirmarOrcamento() throws IOException, ClassNotFoundException {
        List<IOrcamento> orcamentos = centro.get_orcamentos_por_confirmar();
        String[] orcamentosString = new String[orcamentos.size()];
        for(int i =0; i<orcamentos.size();i++){
            IOrcamento orcamento = orcamentos.get(i);
            IPedido pedido = orcamento.get_pedido();
            ICliente cliente = centro.get_cliente(pedido.getNifCliente());
            String sb = "Equipamento [#" + orcamento.get_num_ref() + "]" +
                    "Data [" + pedido.getTempoRegisto().format(formatter) + "]" +
                    "Preco estimado [" + orcamento.calcula_gasto_estimado() + "]" +
                    "Tempo estimado [" + orcamento.calcula_duracao_estimada() + "]" +
                    "Cliente [" + cliente.getNif() + "]" +
                    "Email [" + cliente.getEmail() + "]";
            orcamentosString[i] = sb;
        }
        CRView menu = new CRView("Lista de Orcamentos por confirmar",orcamentosString);
        AtomicInteger i = new AtomicInteger(1);
        for(; i.get() <= orcamentos.size();i.incrementAndGet()){
            int posicao = i.get();
            int num_ref = orcamentos.get(posicao-1).get_num_ref();
            menu.setHandler(i.get(),()->{centro.confirmar_orcamento(num_ref);menu.returnMenu();});
        }
        menu.simpleRun();

    }


    private void listaDeUsuarios(Map<String, IUtilizador> utilizadores, String titulo) throws IOException, ClassNotFoundException {
        String[] utilizadoresString = new String[utilizadores.size()];
        Iterator<IUtilizador> iterator = utilizadores.values().iterator();

        for(int i =0; i<utilizadores.size() && iterator.hasNext();i++){
                IUtilizador f = iterator.next();
                String sb = "User [#" + f.getId() + "]" +
                        "Nome [" + f.getName() + "]";
                utilizadoresString[i] = sb;
        }
        CRView menu = new CRView(titulo,utilizadoresString);
        AtomicInteger i = new AtomicInteger(1);
        iterator = utilizadores.values().iterator();
        for(; i.get() <= utilizadores.size() && iterator.hasNext();i.incrementAndGet()){
            String id = iterator.next().getId();
            menu.setHandler(i.get(),()-> {
                        int option = menu.readOptionBetween(1, 2, new String[]{"Remover user", "Voltar"});
                        if (option == 1) {
                            auxView.normalMessage("Confirme a operecao [Y/N]:");
                            String line = scanner.nextLine();
                            if (line.equals("y") || line.equals("Y")) {
                                centro.remover_utilizador(id);
                                menu.returnMenu();
                            }
                        }
                    }
            );
        }
        menu.simpleRun();

    }


    private void registarCliente() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Registar Cliente", menuRegistoCliente);
        AtomicReference<String> nome = new AtomicReference<>();
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicReference<String> telemovel = new AtomicReference<>();
        AtomicReference<String> email = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(6);
        for(int i = 0; i < 4; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(5,()-> condicao.stream().noneMatch(k -> k.get() == 0));


        menu.setHandler(1,()->{
            auxView.perguntaNomeCliente();
            nome.set(scanner.nextLine());
            menu.changeOption(1,"Nome do cliente: "+ nome.get());
            condicao.get(0).set(1);
        });
        menu.setHandler(2,()->{
            auxView.perguntaNIFCliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifInt(nif.get()) && verifSameLength(nif.get(),9)) {
                menu.changeOption(2, "NIF do cliente: " + nif.get());
                condicao.get(1).set(1);
            }
            else{
                auxView.errorMessage("NIF inválido!");
                nif.set(auxNif);
            }
        });
        menu.setHandler(3,()->{
            auxView.perguntaTelemovel();
            String auxTelemovel = telemovel.get();
            telemovel.set(scanner.nextLine());
            if(verifInt(telemovel.get()) && verifSameLength(telemovel.get(),9)) {
                menu.changeOption(3, "Telemóvel do cliente: " + telemovel.get());
                condicao.get(2).set(1);
            }
            else{
                auxView.errorMessage("Telemóvel inválido!");
                telemovel.set(auxTelemovel);
            }
        });
        menu.setHandler(4, ()->{
            auxView.perguntaEmail();
            String auxEmail = email.get();
            email.set(scanner.nextLine());
            if(verifEmail(email.get())){
                menu.changeOption(4,"Email do cliente: "+email.get());
                condicao.get(3).set(1);
            }
            else{
                email.set(auxEmail);
            }
        });
        menu.setHandler(5,()->{
            centro.adicionar_cliente(nif.get(),nome.get(),telemovel.get(),email.get());
            menu.returnMenu();
        });
        menu.simpleRun();
    }


    private void registarUtilizador() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Registar Utilizador", menuRegistoUtilizador);
        AtomicReference<String> id = new AtomicReference<>();
        AtomicReference<String> nome = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        AtomicInteger tipoUtilizador = new AtomicInteger(-1);

        List<AtomicInteger> condicao = new ArrayList<>(4);
        for(int i = 0; i < 4; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(5,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1,()->{
            auxView.perguntaId();
            String old = id.get();
            id.set(scanner.nextLine());
            if(!centro.exists_user(id.get())){
                menu.changeOption(1,"Id: " + id.get());
                condicao.get(0).set(1);
            }else {
                id.set(old);
                auxView.errorMessage("Id invalido");
            }
        });

        menu.setHandler(2,()->{
            auxView.normalMessage("Nome: ");
            nome.set(scanner.nextLine());
            menu.changeOption(2,"Nome: " + nome.get());
            condicao.get(1).set(1);
        });

        menu.setHandler(3,()->{
            auxView.normalMessage("Password: ");
            password.set(scanner.nextLine());
            StringBuilder credentials = new StringBuilder();
            for(int i = 0; i<password.get().length();i++) credentials.append("*");
            menu.changeOption(3,"Password: "+ credentials);
            condicao.get(2).set(1);
        });

        menu.setHandler(4, ()-> {
            tipoUtilizador.set(menu.readOptionBetween(1,3,escolheTipoUtilizador));
            if(tipoUtilizador.get()!=-1){
                switch (tipoUtilizador.get()) {
                    case 1 -> menu.changeOption(4,"Gestor");
                    case 2 -> menu.changeOption(4,"Funcionario");
                    case 3 -> menu.changeOption(4,"Tecnico");
                }
                condicao.get(3).set(1);
            }
            else condicao.get(3).set(0);
        });

        menu.setHandler(5, ()-> {
            centro.adicionar_utilizador(id.get(),nome.get(),password.get(),tipoUtilizador.get());
            menu.returnMenu();
        });
        menu.simpleRun();
    }


    private void logout() throws IOException, ClassNotFoundException {
        this.logged = false;
        centro.logout();
        login();
    }

    private void registarPedido() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Registo Pedido", menuRegistoPedido);
        menu.setPreCondition(1, centro::disponibilidade_pedido_expresso);

        menu.setHandler(1,()->{pedidoExpress();menu.returnMenu();});
        menu.setHandler(2,()->{registarPedidoOrcamento();menu.returnMenu();});


        menu.simpleRun();
    }

    private void pedidoExpress() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Pedido Expresso", menuPedido);
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicInteger tipo = new AtomicInteger(0);
        AtomicReference<String> modelo = new AtomicReference<>();
        AtomicReference<String> descricaoEquipamento = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(4,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1,()->{
            auxView.perguntaNIFCliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifInt(nif.get()) && verifSameLength(nif.get(),9)) {
                if(centro.exists_cliente(nif.get())) {
                    menu.changeOption(1, "NIF do cliente: " + nif.get());
                    condicao.get(0).set(1);
                }
                else{
                    auxView.errorMessage("Cliente não registado!");
                }
            }
            else{
                auxView.errorMessage("Nif inválido!");
                nif.set(auxNif);
            }
        });
        menu.setHandler(2, ()->{
            equipamentoInfo(modelo,descricaoEquipamento);

            if(modelo.get() == null){
                menu.changeOption(2,"Equipamento");
                condicao.get(1).set(0);
            }
            else {
                condicao.get(1).set(1);
                menu.changeOption(2,"Equipamento [Registado #"+(centro.get_ultimo_numero_de_registo_equipamento()+1)+"]");
            }
        });
        menu.setHandler(3, ()->{
            auxView.normalMessage("Tipo de Pedido Expresso: ");;
            tipo.set(menu.readOptionBetween(1,4,pedidosExpressos));
            menu.changeOption(3,"Tipo: "+tipo.get());
            condicao.get(2).set(1);
        });
        menu.setHandler(4, ()->{
            centro.adicionar_pedido_expresso(nif.get(),modelo.get(),descricaoEquipamento.get(),tipo.get());
            menu.returnMenu();
        });

        menu.simpleRun();
    }


    private void registarPedidoOrcamento() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Pedido Orçamento", menuPedido);
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicReference<String> descricaoPedido = new AtomicReference<>();
        AtomicReference<String> modelo = new AtomicReference<>();
        AtomicReference<String> descricaoEquipamento = new AtomicReference<>();
        List<AtomicInteger> condicao = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(4,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1,()->{
            auxView.perguntaNIFCliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifInt(nif.get()) && verifSameLength(nif.get(),9)) {
                if(centro.exists_cliente(nif.get())) {
                    menu.changeOption(1, "NIF do cliente: " + nif.get());
                    condicao.get(0).set(1);
                }
                else{
                    auxView.errorMessage("Cliente não registado!");
                }
            }
            else{
                auxView.errorMessage("Nif inválido!");
                nif.set(auxNif);
            }
        });
        menu.setHandler(2, ()->{
            equipamentoInfo(modelo,descricaoEquipamento);

            if(modelo.get() == null){
                menu.changeOption(2,"Equipamento");
                condicao.get(1).set(0);
            }
            else {
                condicao.get(1).set(1);
                menu.changeOption(2,"Equipamento [Registado #"+(centro.get_ultimo_numero_de_registo_equipamento()+1)+"]");
            }
        });
        menu.setHandler(3, ()->{
            auxView.normalMessage("Descrição do Pedido: ");;
            String auxDescricao = descricaoPedido.get();
            descricaoPedido.set(scanner.nextLine());
            if(verifLength(descricaoPedido.get(),25)) {
                menu.changeOption(3,"Descrição: "+descricaoPedido.get());
                condicao.get(2).set(1);
            }
            else {
                descricaoPedido.set(auxDescricao);
            }
        });
        menu.setHandler(4, ()->{
            centro.adicionar_pedido_orcamento(nif.get(),modelo.get(),descricaoEquipamento.get(),descricaoPedido.get());
            menu.returnMenu();
        });

        menu.simpleRun();
    }

    private void equipamentoInfo(AtomicReference<String> modelo, AtomicReference<String> descricao) throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Equipamento Info", menuEquipamentoInfo);

        List<AtomicInteger> condicao = new ArrayList<>(2);
        for(int i = 0; i < 2; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(3,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1, ()->{
            auxView.normalMessage("Modelo do Equipamento: ");;
            String auxModelo = modelo.get();
            modelo.set(scanner.nextLine());
            if(verifLength(modelo.get(),25)){
                menu.changeOption(1,"Modelo: "+modelo.get());
                condicao.get(0).set(1);
            }
            else {
                modelo.set(auxModelo);
            }
        });
        menu.setHandler(2, ()->{
            auxView.normalMessage("Descrição do Equipamento: ");
            String auxDescricao = descricao.get();
            descricao.set(scanner.nextLine());
            if(verifLength(descricao.get(),25)) {
                menu.changeOption(2,"Descrição: "+descricao.get());
                condicao.get(1).set(1);
                }
            else {
                descricao.set(auxDescricao);
            }
        });

        menu.setHandler(3, menu::returnMenu);

        menu.simpleRun();
    }


    private boolean verifInt(String string){
        try{
            Integer.parseInt(string);
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private boolean verifLength(String string,int limit){
        return string.length() <= limit;
    }

    private boolean verifSameLength(String string,int limit){
        return string.length() == limit;
    }

    private boolean verifEmail(String email){
        String[] splitEmail = email.split("@");
        try{
            return splitEmail[0].length() > 0 && splitEmail[1].length() > 0;
        }
        catch(ArrayIndexOutOfBoundsException e){
            auxView.errorMessage("Email inválido!");
            return false;
        }
    }






}
