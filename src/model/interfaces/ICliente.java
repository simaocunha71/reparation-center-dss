package model.interfaces;

public interface ICliente {
    public void load_cliente(String string);


    public String getNif();

    public String getEmail();
    public String getNome();


    public String getNumTelemovel();

    public ICliente clone();

    public boolean valida_cliente();
}
