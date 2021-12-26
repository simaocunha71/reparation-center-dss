import model.CentroReparacoesFacade;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.IUtilizador;
import model.utilizadores.Funcionario;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, JaExistenteExcecao {

        CentroReparacoesFacade centro = new CentroReparacoesFacade();
        centro.carregar_utilizadores("cp/utilizadores.csv");
        System.out.println(centro.getUtilizadorByID("u004").getName());
    }

}
