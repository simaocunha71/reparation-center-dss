package model.interfaces;

public interface ICliente extends Carregavel {


    public String getNif();

    public String getEmail();
    public String getNome();


    public String getNumTelemovel();

    public ICliente clone();

}

