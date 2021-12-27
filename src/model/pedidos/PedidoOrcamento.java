package model.pedidos;

import model.interfaces.ICliente;
import model.interfaces.IPedido;

import java.time.LocalDateTime;

public class PedidoOrcamento implements IPedido {
    private String nifCliente;
    private LocalDateTime dataRegisto;
    private String numeroRegistoEquipamento;
    private String descricaoPedido;

    public PedidoOrcamento(String nifCliente, String numeroRegistoEquipamento, String descricaoPedido){
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        this.descricaoPedido = descricaoPedido;
        this.dataRegisto = LocalDateTime.now();
    }

    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }

    public String getNumeroRegistoEquipamento() {
        return numeroRegistoEquipamento;
    }
}
