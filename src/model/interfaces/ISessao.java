package model.interfaces;

public interface ISessao {


    boolean login (String id, String password);

    void logout();

    boolean logged_funcionario();
    boolean logged_tecnico();
    boolean logged_gestor();
}
