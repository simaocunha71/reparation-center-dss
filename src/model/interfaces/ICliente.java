package model.interfaces;

public interface ICliente extends Carregavel {


    String getNif();

    String getEmail();
    String getNome();


    String getNumTelemovel();

    ICliente clone();

}

