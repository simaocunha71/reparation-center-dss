package controller;

import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import model.interfaces.ISessao;
import view.CRView;
import view.AuxiliarView;

import java.io.IOException;
import java.util.Scanner;

public class CRController {

    private ICentroReparacoes centro;
    private ISessao sessaoAtual;
    private AuxiliarView auxView = new AuxiliarView();
    private Scanner scanner = new Scanner(System.in);
    private final String[] menuInicial = new String[]{
            "Login",
            "Sair"
    };

    private final String[] menuLogin = new String[]{
            "User id",
            "Password",
            "Voltar"
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
            "Passoword",
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

    private void login() {
        CRView menu = new CRView("Autenticação", menuLogin);
        menu.setHandler(1, ()->{
            auxView.perguntaNomeDeUtilizador();
            String nomeDeUtilizador = scanner.nextLine();
            menu.changeOption(1,"User id: ["+nomeDeUtilizador+"]");
        });
        menu.setHandler(2, ()->{
        });
    }


}
