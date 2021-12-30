package view;

import model.excecoes.JaExistenteExcecao;

import java.io.IOException;
import java.util.*;

public class CRView implements ANSIICores{


    /**
     * Interface para execucao de opcoes
     */
    public interface Handler {
        void execute() throws IOException, ClassNotFoundException, JaExistenteExcecao;
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
    private String title;
    private boolean exit = false;

    /**
     * Construtor de SPORTMViewer
     * @param options menu a ser imprimido e respetivas opcoes
     */
    public CRView(String title, String[] options){
        this.title = title;
        this.options = new LinkedList<>(Arrays.asList(options));
        this.available = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.options.forEach(s-> {
            this.available.add(()->true);
            this.handlers.add(()->System.out.println(RED+"Option not implemented!"+RESET));
        });
    }

    public void change_option(int opt, String option){
        if(options.size() > opt){
            this.options.remove(opt-1);
            this.options.add(opt-1,option);
        }
    }


    /**
     * Metodo de run simples, termina quando dado a opcao 0, ou caso
     * o booleano de control torne falso
     * Caso seja escolhido uma opcao valida e as pre-condicoes dessa opcao
     * sejam cumpridas, executa o handler respetivo
     * @throws IOException e
     * @throws ClassNotFoundException e
     */
    public void simple_run() throws IOException, ClassNotFoundException {
        int op;
        do {
            simple_show();
            op = read_option();
            // testar pré-condição
            if (op>0 && !this.available.get(op-1).validate()) {
                System.out.println(RED +"Option not available"+ RESET);
            } else if (op>0) {
                // executar handler
                try {
                    this.handlers.get(op-1).execute();
                } catch (JaExistenteExcecao jaExistenteExcecao) {
                    jaExistenteExcecao.printStackTrace();
                }
            }
        } while (op != 0 && !exit);
    }

    /**
     * Método que regista uma uma pré-condição numa opção do menu
     *
     * @param i índice da opção
     * @param b pré-condição a registar para a opcao
     */
    public void set_pre_condition(int i, PreCondition b) {
        this.available.set(i-1,b);
    }

    /**
     * Similar ao metodo anterior, mas coloca a mesma pre-condicao para
     * varias opcoes ao mesmo tempo
     * @param list lista das opcoes a abrangir
     * @param b pre-condicao a registar nas opcoes
     */
    public void set_same_pre_condition(int[] list, PreCondition b) {
        for(int i:list) this.available.set(i-1,b);

    }

    /**
     * Método para registar um handler numa opção do menu
     *
     * @param i indice da opção
     * @param h handlers a registar
     */
    public void set_handler(int i, Handler h) {
        this.handlers.set(i-1, h);
    }


    /**
     * Imprime as varias opcoes, verificando se as pre-condicoes estao a ser compridas
     */
    private void simple_show() {
        System.out.println(YELLOW + this.title + RESET);
        for (int i=0; i<this.options.size(); i++) {
            System.out.print(i+1);
            System.out.print(" - ");
            System.out.println(this.available.get(i).validate() ? this.options.get(i) : "---");
        }
        System.out.println("0 - Sair");
    }

    /**
     * Le uma opcao escolhida pelo user
     * @return opcao escolhida
     */
    private int read_option() {
        int op;
        //Scanner is = new Scanner(System.in);

        information_message("Option: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        if (op<0 || op>this.options.size()) {
            System.out.println(RED +"Invalid Option" + options+RESET);
            op = -1;
        }
        return op;
    }

    /**
     * Le uma opcao escolhida pelo user entre dois limites
     * @param lower limite inferior
     * @param higher limite superior
     * @param options opcoes disponiveis
     * @return input do utilizador
     */
    public int read_option_between(int lower, int higher, String[] options) {
        int op = -1;
        //Scanner is = new Scanner(System.in);
        int i = 0;
        if(options != null) {
            while (i < options.length) {
                System.out.println(i+lower + " - " + options[i]);
                i++;
            }
        }
        information_message("Write a number between "+lower+" and "+higher+"\nOption: ");
        while(op == -1) {
            try {
                String line = is.nextLine();
                op = Integer.parseInt(line);
            } catch (NumberFormatException ignored) {}
            if (op < lower || op > higher) {
                System.out.println(RED + "Invalid Option" + RESET);
                op = -1;
            }
        }
        return op;
    }

    /**
     * Faz toString de um objeto dado
     * @param o objeto a imprimir no ecra
     */
    public void show_info(Object o){
        System.out.println(o.toString());
        information_message("Press any key to continue");
        is.nextLine();
    }

    /**
     * Torna o booleano de controlo em falso, para encerrar o menu
     */
    public void return_menu(){
        exit = true;
    }

    /**
     * Imprime uma mensagem a verde
     * @param message mensagem a imprimir
     */
    public void confirmation_message(String message){
        System.out.println(GREEN + message + RESET);
    }

    /**
     * Imprime mensagens a roxo
     * @param message mensagem a imprimir
     */
    public void information_message(String message){
        System.out.println(PURPLE + message + RESET);
    }


}

