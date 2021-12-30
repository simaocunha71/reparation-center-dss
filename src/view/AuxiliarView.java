package view;

public class AuxiliarView implements ANSIICores {
    public AuxiliarView(){}


    public void mensagem_de_erro(String error){

        System.out.println(RED + error + RESET);
    }

    public void pergunta_nome(){
        System.out.println("Nome de Utilizador: ");
    }

    public void pergunta_password(){
        System.out.println("Password: ");
    }


    public void pergunta_id(){
        System.out.println("Id : ");
    }

    public void pergunta_nome_cliente(){
        System.out.println("Nome do cliente: ");
    }

    public void pergunta_nif_do_cliente(){
        System.out.println("NIF do cliente: ");
    }

    public void pergunta_numero_de_telemovel(){
        System.out.println("Número de telemóvel: ");
    }

    public void pergunta_email(){
        System.out.println("Email do cliente: ");
    }

    public void apresentar_plano(String string){
        System.out.println(string);
    }

    public void mensagem_normal(String message){
        System.out.println(message);
    }

    public void apresentar_passo(String string) {
        System.out.println(string);
    }

    public void apresentar_subpasso(String string) {
        System.out.println(string);
    }


}
