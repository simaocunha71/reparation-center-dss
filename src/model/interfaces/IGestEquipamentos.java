package model.interfaces;

public interface IGestEquipamentos extends Carregavel {
    void transferencia_seccao(int num_reg);

    void regista_equipamento(IEquipamento equipamento, int local);

    boolean contem_equipamento(int num_registo, int seccao);

    int get_ultimo_numero_de_registo_equipamento();

    IEquipamento get_equipamento(int num_reg);
}
