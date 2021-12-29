package model.interfaces;

public interface IUtilizador extends Carregavel {

    String getName();

    String getId();

    String getPassword();

    IUtilizador clone();

}
