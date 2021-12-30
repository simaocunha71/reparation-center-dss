package model.interfaces;

public interface ISessao {


    boolean login (String user_id, String password);

    void logout();

    boolean logged_funcionario();
    boolean logged_tecnico();
    boolean logged_gestor();
}
