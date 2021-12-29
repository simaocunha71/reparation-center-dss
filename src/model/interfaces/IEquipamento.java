package model.interfaces;

public interface IEquipamento extends Carregavel{
    String getNifCliente();

    int getNumeroRegisto();

    String getModelo();

    String getDescricao();

    IEquipamento clone();
}
