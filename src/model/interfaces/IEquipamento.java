package model.interfaces;

public interface IEquipamento extends Carregavel,Validavel{
    int get_numero_registo();

    String get_modelo();

    String get_descricao();

    IEquipamento clone();
}
