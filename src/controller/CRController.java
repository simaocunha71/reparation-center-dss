package controller;

import model.CRFacade;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import view.CRView;
import view.AuxiliarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CRController {

    private ICentroReparacoes centro = new CRFacade();
    private AuxiliarView auxView = new AuxiliarView();
    private Scanner scanner = new Scanner(System.in);
    private final String[] menuInicial = new String[]{
            "Autenticação"
    };

    private final String[] menuLogin = new String[]{
            "User id",
            "Password",
            "Entrar",
    };

    //TODO: confirmar orçamento
    private final String[] menuPrincipalGestor = new String[]{
            "Registar cliente",
            "Registar pedido",
            "Lista de pedidos de orçamento",
            "Lista de equipamentos para reparação",
            "Lista de funcionários",
            "Lista de técnicos",
            "Registar utilizador",
            "Logout",
    };

    private final String[] menuPrincipalTecnico = new String[]{
            "Lista de pedidos de orçamento",
            "Lista de equipamentos para reparação",
            "Logout",
    };

    //TODO: confirmar orçamento
    private final String[] menuPrincipalFuncionario = new String[]{
            "Registar pedido",
            "Logout",
    };

    private final String[] menuRegistoCliente = new String[]{
            "Nome",
            "NIF",
            "Telemovel",
            "Email", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
            "Voltar"
    };


    private final String[] menuRegistoUtilizador = new String[]{
            "User id",
            "Nome",
            "Password",
            "Tipo de Utilizador", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
            "Voltar"
    };

    private final String[] escolheTipoUtilizador = new String[]{
            "Gestor",
            "Funcionário",
            "Técnico"
    };

    private final String[] menuRegistoPedido = new String[]{
            "Serviço Express",
            "Pedido de orçamento",
            "Voltar"
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
            "Guardar e sair",
    };

    private boolean logged = false;



    public void run() throws IOException, ClassNotFoundException {
        try {
            centro.carregar_cp("cp/utilizadores.csv", "cp/clientes.csv","cp/armazem.csv", "cp/pedidos.csv");
        }
        catch (JaExistenteExcecao e){
        }
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
            menu.changeOption(2,"Password: "+ credentials.toString());
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

        menu.setHandler(1,this::registarCliente);

        menu.setHandler(2,this::registarPedido);

        menu.setHandler(3,this::listaDePedidosOrcamento);
//
        //menu.setHandler(4,this::listaDeEquipamentosReparacao);
//
        //menu.setHandler(5,this::listaDeFuncionarios);
        //menu.setHandler(6,this::listaDeTecnicos);
//
        menu.setHandler(7,this::registarUtilizador);
        menu.setHandler(8,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }

    private void listaDePedidosOrcamento() throws IOException, ClassNotFoundException {
        List<String> pedidos = centro.get_pedidos_orcamento();
        CRView menu = new CRView("Pedidos de orçamento",pedidos.toArray(new String[0]));
        AtomicInteger i = new AtomicInteger(0);
        for(; i.get() < pedidos.size();i.incrementAndGet()){
            menu.setHandler(i.get(),()->fazerPlano(i.get()));
        }
        menu.simpleRun();
    }

    //TODO: FAZER MENU DE FAZER PLANO
    private void fazerPlano(int i) {
        //TODO:Adicionar passo (vai ter um menu) [preço estimado/tempo estimado/descricao e sub-passos (com as mesmas merdas)]
        //TODO:Apresentar plano
        //TODO:Gravar
    }


    private void menuInicialTecnico() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalTecnico);

        //menu.setHandler(1,this::listaDePedidosOrcamento);

        //menu.setHandler(2,this::listaDeEquipamentosReparacao);
        menu.setHandler(3,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }

    private void menuInicialFuncionario() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalFuncionario);

        menu.setHandler(1,this::registarPedido);

        menu.setHandler(2,()->{menu.returnMenu();logout();});

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
            if(verifInt(nif.get()) && verifLength(nif.get(),9)) {
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
            if(verifInt(telemovel.get()) && verifLength(telemovel.get(),9)) {
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
            menu.changeOption(3,"Password: "+ credentials.toString());
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

        //menu.setHandler(1,()->{pedidoExpress();menu.returnMenu();});
        menu.setHandler(2,()->{registarPedidoOrcamento();menu.returnMenu();});
        menu.setHandler(3,menu::returnMenu);


        menu.simpleRun();
    }


    private void registarPedidoOrcamento() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Pedido Express", menuPedido);
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
            if(verifInt(nif.get()) && verifLength(nif.get(),9)) {
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
                menu.changeOption(2,"Equipamento [Registado #"+centro.get_ultimo_numero_de_registo_equipamento()+1+"]");
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

        auxView.normalMessage("#"+centro.get_ultimo_numero_de_registo_equipamento()+1+" equipamento a registar.");

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
                menu.changeOption(1,"Descrição: "+descricao.get());
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


    private void mostraPlanos() {
    }




}
