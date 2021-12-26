package controller;

import model.CRFacade;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import model.interfaces.ISessao;
import view.CRView;
import view.AuxiliarView;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CRController {

    private ICentroReparacoes centro = new CRFacade();
    private ISessao sessaoAtual;
    private AuxiliarView auxView = new AuxiliarView();
    private Scanner scanner = new Scanner(System.in);
    private final String[] menuInicial = new String[]{
            "Login"
    };

    private final String[] menuLogin = new String[]{
            "User id",
            "Password",
            "Login",
    };

    private final String[] menuPrincipal = new String[]{
            "Registar Utilizador",
            "Registar Pedido",
            "Ver planos",
            "Logout",
            "Sair"
    };


    private final String[] menuRegistoUtilizador = new String[]{
            "Id (uXXX)",
            "Nome",
            "Password",
            "Tipo de Utilizador", //Funcionar ou Técnico ou Gestor
            "Voltar",
            "Guardar e Sair"
    };

    private final String[] escolheTipoUtilizador = new String[]{
            "Funcionário",
            "Técnico",
            "Gestor"
    };

    private final String[] menuRegistoPedido = new String[]{
            "Serviço Express",
            "Pedido de orçamento",
            "Voltar"
    };

    private final String[] menuPedidoExpress = new String[]{
    };

    private final String[] menuPedidoOrcamento = new String[]{
            "Cliente",
            "Equipamento",
            ""
    };

    private final String[] menuEquipamentoInfo = new String[]{
            "Modelo",
            "Descricao",
            "Voltar"
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
        AtomicBoolean option1 = new AtomicBoolean(false);
        AtomicBoolean option2 = new AtomicBoolean(false);
        AtomicReference<String> nomeDeUtilizador = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        menu.setPreCondition(2, option1::get);
        menu.setPreCondition(3, option2::get);

        menu.setHandler(1, ()->{
            auxView.perguntaNomeDeUtilizador();
            nomeDeUtilizador.set(scanner.nextLine());
            menu.changeOption(1,"User id: ["+nomeDeUtilizador+"]");
            option1.set(true);
        });
        menu.setHandler(2, ()->{
            auxView.perguntaPasseDeUtilizador();
            password.set(scanner.nextLine());
            StringBuilder credentials = new StringBuilder();
            for(int i = 0; i<password.get().length();i++) credentials.append("*");
            menu.changeOption(2,"Password: "+ credentials.toString());
            option2.set(true);
        });
        menu.setHandler(3, ()->{
            if(centro.existsUser(nomeDeUtilizador.get(),password.get())){
                logged = true;
                menu.confirmationMessage("Logged in");
                menu.returnMenu();
            }
            else{
                nomeDeUtilizador.set(null);
                password.set(null);
                menu.changeOption(1,"User id");
                menu.changeOption(2,"Password");
            }
        });
        menu.simpleRun();
        if(logged) menuInicial();
    }


    private void menuInicial() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Menu", menuPrincipal);


        menu.setHandler(3,()->centro.existsPlans());

        menu.setHandler(1,this::menuRegistoUtilizador);
        menu.setHandler(2,this::menuRegistoPedido);
        menu.setHandler(3,this::mostraPlanos);
        menu.setHandler(4,()->{
            logged = false;
            menu.returnMenu();
            login();
        });
        menu.simpleRun();
    }



    public void menuRegistoUtilizador() throws IOException, ClassNotFoundException {
        CRView menu = new CRView("Registar Utilizador", menuRegistoUtilizador);
        AtomicReference<String> id = new AtomicReference<>();
        AtomicReference<String> nome = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        AtomicInteger tipoUtilizador = new AtomicInteger();
        

        menu.setHandler(1,()->{

        });

        menu.simpleRun();
    }

    private void menuRegistoPedido() {
    }

    private void mostraPlanos() {
    }




}
