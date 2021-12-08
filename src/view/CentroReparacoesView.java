package view;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class CentroReparacoesView {

    /**
     * Interface para execucao de opcoes
     */
    public interface Handler {
         void execute() throws IOException, ClassNotFoundException;
    }

    /**
     * Interface para indicar pre-condicoes para opcoes
     */
    public interface PreCondition {
         boolean validate();
    }

    private static Scanner is = new Scanner(System.in);
    private List<String> options;            // Lista de opções
    private List<PreCondition> available;  // Lista de pré-condições
    private List<Handler> handlers;         // Lista de handlers
    private boolean exit = false;
}
