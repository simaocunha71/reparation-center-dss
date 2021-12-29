package model.interfaces;

import model.orcamento.Passo;

public interface Planeavel {

    float orcamento_gasto();

    boolean concluido();

    IPedido get_pedido();

    boolean ultrapassou_120porcento_orcamento();

    int get_total_passos();

    float calcula_custo_gasto();

    float calcula_tempo_gasto();

    float calcula_gasto_estimado();

    float calcula_duracao_estimada();

    int get_num_ref();

    Passo get_proximo_passo();
}
