package model.interfaces;

public interface IUtilizador {

    String getName();

    String getId();

    String getPassword();

    void load_utilizador(String linha);
    boolean valida_utilizador();

    String toString();
}
