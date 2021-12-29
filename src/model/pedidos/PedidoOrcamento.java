package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PedidoOrcamento implements IPedido {
    private String nifCliente;
    private LocalDateTime dataRegisto;
    private int numeroRegistoEquipamento;
    private String descricaoPedido;

    public PedidoOrcamento(String nifCliente, int numeroRegistoEquipamento, String descricaoPedido){
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        this.descricaoPedido = descricaoPedido;
        this.dataRegisto = LocalDateTime.now();
    }

    public PedidoOrcamento(String nifCliente, int numeroRegistoEquipamento, String descricaoPedido, LocalDateTime dataRegisto){
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        this.descricaoPedido = descricaoPedido;
        this.dataRegisto = dataRegisto;
    }

    public PedidoOrcamento(){
        this.nifCliente = "";
        this.numeroRegistoEquipamento = -1;
        this.descricaoPedido = "";
        this.dataRegisto = null;
    }

    public boolean valida(){
        return nifCliente.length() > 0 && numeroRegistoEquipamento > 0 && descricaoPedido.length() > 0 && dataRegisto != null;
    }

    public String getNifCliente() {
        return nifCliente;
    }


    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 4) {
            this.nifCliente = split[0];
            try{
                this.dataRegisto = LocalDateTime.parse(split[1]);
                this.numeroRegistoEquipamento = Integer.parseInt(split[2]);
            }
            catch(DateTimeParseException e){
                this.dataRegisto = null;
            }
            catch(NumberFormatException e){
                this.numeroRegistoEquipamento = -1;
            }
            this.descricaoPedido = split[3];

        }
    }


    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }

    public int getNumeroRegistoEquipamento() {
        return this.numeroRegistoEquipamento;
    }

    //tipoPedido@nifCliente;dataRegisto;numeroRegistoEquipamento;descricaoPedido
    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(nifCliente).append(";").append(dataRegisto.toString()).append(";");
        sb.append(numeroRegistoEquipamento).append(";").append(descricaoPedido);
        return sb.toString();
    }

    public IPedido clone(){
        return new PedidoOrcamento(this.nifCliente,this.numeroRegistoEquipamento,this.descricaoPedido,this.dataRegisto);
    }
}
