package model.interfaces;

import model.orcamento.Passo;

public interface IPlanoDeTrabalho extends Carregavel{

    IPlanoDeTrabalho clone();

    void recalcula_estimativas();

    void adicionar_passo(Passo p);

    float orcamento_gasto();

    boolean concluido();

    IPedido get_pedido();

    boolean ultrapassou_120porcento_orcamento();

    int get_total_passos();

    float calcula_custo_gasto();

    float calcula_tempo_gasto();

    float calcula_custo_estimado();

    float calcula_duracao_estimada();

    Passo get_proximo_passo();

}
