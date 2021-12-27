package controller;

import model.CRFacade;
import model.Equipamento;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import model.pedidos.PedidoOrcamento;
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

    private final String[] menuPrincipalGestor = new String[]{
            //TODO: "Registar cliente"
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

    private final String[] menuPrincipalFuncionario = new String[]{
            "Registar pedido",
            "Logout",
    };

    private final String[] menuRegistoCliente = new String[]{
            "NIF",
            "Nome",
            "Telemovel",
            "Email", //Funcionar ou Técnico ou Gestor
            "Guardar e sair",
            "Voltar"
    };


    private final String[] menuRegistoUtilizador = new String[]{
            "Id (uXXX)",
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
            "Descricao",
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
            centro.carregar_cp("cp/utilizadores.csv", "cp/clientes.csv", "cp/pedidos.csv");
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

    public void menuInicialGestor() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalGestor);

        menu.setHandler(1,this::registarCliente);

        menu.setHandler(2,this::registarPedido);

        //menu.setHandler(3,this::listaDePedidosOrcamento);
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

    public void menuInicialTecnico() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalGestor);

        //menu.setHandler(1,this::listaDePedidosOrcamento);

        //menu.setHandler(2,this::listaDeEquipamentosReparacao);
        menu.setHandler(3,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }

    public void menuInicialFuncionario() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu Inicial", menuPrincipalGestor);

        menu.setHandler(1,this::registarPedido);

        menu.setHandler(2,()->{menu.returnMenu();logout();});

        menu.simpleRun();
    }


    public void registarCliente() throws IOException, ClassNotFoundException {
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
            if(verifNif(nif.get())) {
                menu.changeOption(2, "NIF do cliente: " + nif.get());
                condicao.get(1).set(1);
            }
            else{
                nif.set(auxNif);
            }
        });
        menu.setHandler(3,()->{
            auxView.perguntaTelemovel();
            String auxTelemovel = telemovel.get();
            telemovel.set(scanner.nextLine());
            if(verifTelemovel(telemovel.get())) {
                menu.changeOption(3, "Telemóvel do cliente: " + telemovel.get());
                condicao.get(2).set(1);
            }
            else{
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


    public void registarUtilizador() throws IOException, ClassNotFoundException {
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

    public void registarPedido() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Registo Pedido", menuRegistoPedido);

        //menu.setHandler(1,this::pedidoExpress);
        menu.setHandler(2,this::registarPedidoOrcamento);


        menu.simpleRun();
    }


    private void registarPedidoOrcamento() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Pedido Express", menuPedido);
        AtomicReference<String> nif = new AtomicReference<>();
        AtomicReference<String> descricao = new AtomicReference<>();
        AtomicReference<Equipamento> equipamento = new AtomicReference<>(null);
        List<AtomicInteger> condicao = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(4,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1,()->{
            auxView.perguntaNIFCliente();
            String auxNif = nif.get();
            nif.set(scanner.nextLine());
            if(verifNif(nif.get())) {
                if(centro.exists_cliente(nif.get())) {
                    menu.changeOption(1, "NIF do cliente: " + nif.get());
                    condicao.get(0).set(1);
                }
            }
            else{
                nif.set(auxNif);
            }
        });
        menu.setHandler(2, ()->{
            if(equipamento.get() != null) menu.changeOption(2,"Equipamento: " + equipamento.get().getNumeroRegisto());
            equipamentoInfo(equipamento);
            if(equipamento.get() != null) condicao.get(1).set(1);
            else condicao.get(1).set(0);
        });
        menu.setHandler(3, ()->{
            auxView.normalMessage("Descricao: ");
            descricao.set(scanner.nextLine());
            condicao.get(2).set(1);
        });
        menu.setHandler(4, ()->{
            centro.adicionar_pedido_orcamento(nif.get(),equipamento.get(),descricao.get());
            menu.returnMenu();
        });

        menu.simpleRun();
    }

    private void equipamentoInfo(AtomicReference<Equipamento> equipamento) throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Equipamento Info", menuEquipamentoInfo);
        AtomicReference<String> modelo = new AtomicReference<>();
        AtomicReference<String> descricao = new AtomicReference<>("");
        String numeroRegisto;
        if(equipamento.get()==null){
            numeroRegisto = centro.novo_numero_registo();
        }else{
            numeroRegisto = equipamento.get().getNumeroRegisto();
        }
        auxView.normalMessage("Numero de registo: " + numeroRegisto);

        List<AtomicInteger> condicao = new ArrayList<>(2);
        for(int i = 0; i < 2; i++){
            condicao.add(i,new AtomicInteger(0));
        }

        menu.setPreCondition(3,()-> condicao.stream().noneMatch(k -> k.get() == 0));

        menu.setHandler(1, ()->{
            auxView.perguntaEquipamento();
            modelo.set(scanner.nextLine());
            menu.changeOption(2,"Equipamento: "+equipamento.get());
            condicao.get(0).set(1);
        });
        menu.setHandler(2, ()->{
            auxView.perguntaEquipamento();
            descricao.set(scanner.nextLine());
            condicao.get(1).set(1);
        });

        menu.setHandler(3, ()->{
            equipamento.set(new Equipamento(numeroRegisto,modelo.get(),descricao.get()));
            menu.returnMenu();
        });

        menu.simpleRun();
    }


    private boolean verifNif(String nif){
        try{
            Integer.parseInt(nif);
        }
        catch (NumberFormatException e){
            auxView.errorMessage("Nif inválido!");
            return false;
        }
        return nif.length() == 9;
    }

    private boolean verifTelemovel(String telemovel){
        try{
            Integer.parseInt(telemovel);
        }
        catch (NumberFormatException e){
            auxView.errorMessage("Telemóvel inválido!");
            return false;
        }
        return telemovel.length() == 9;
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
