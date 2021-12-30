package model.interfaces;

public interface IUtilizador extends Carregavel,Validavel {

    String get_nome();

    String get_id();

    String get_password();

    IUtilizador clone();

}
