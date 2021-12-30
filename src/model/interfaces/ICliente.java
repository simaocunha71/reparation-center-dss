package model.interfaces;

public interface ICliente extends Carregavel,Validavel {


    String get_nif();

    String get_email();
    String get_nome();


    String get_num_telemovel();

    ICliente clone();

}

