package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PedidoOrcamento implements IPedido {
    private String nifCliente;
    private LocalDateTime dataRegisto;
    private LocalDateTime dataConclusao;
    private int numeroRegistoEquipamento;
    private String descricaoPedido;

    public PedidoOrcamento(String nifCliente, int numeroRegistoEquipamento, String descricaoPedido){
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        this.descricaoPedido = descricaoPedido;
        this.dataRegisto = LocalDateTime.now();
    }

    public PedidoOrcamento(String nifCliente, int numeroRegistoEquipamento, String descricaoPedido, LocalDateTime dataRegisto, LocalDateTime dataConclusao){
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        this.descricaoPedido = descricaoPedido;
        this.dataRegisto = dataRegisto;
        this.dataConclusao = dataConclusao;
    }

    public PedidoOrcamento(){
        this.nifCliente = "";
        this.numeroRegistoEquipamento = -1;
        this.descricaoPedido = "";
        this.dataRegisto = null;
        this.dataConclusao = null;
    }

    public boolean valida(){
        return nifCliente.length() > 0 && numeroRegistoEquipamento > 0 && descricaoPedido.length() > 0 && dataRegisto != null;
    }

    public String getNifCliente() {
        return nifCliente;
    }


    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 5) {
            this.nifCliente = split[0];
            try{
                if(!split[1].equals("null")) this.dataRegisto = LocalDateTime.parse(split[1]);
                if(!split[2].equals("null")) this.dataConclusao = LocalDateTime.parse(split[2]);
                this.numeroRegistoEquipamento = Integer.parseInt(split[3]);
            }
            catch(DateTimeParseException e){
                this.dataRegisto = null;
                this.dataConclusao = null;
            }
            catch(NumberFormatException e){
                this.numeroRegistoEquipamento = -1;
            }
            this.descricaoPedido = split[4];

        }
    }


    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }

    public int getNumeroRegistoEquipamento() {
        return this.numeroRegistoEquipamento;
    }

    //tipoPedido@nifCliente;dataRegisto;dataConclusao;numeroRegistoEquipamento;descricaoPedido
    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(nifCliente).append(";");
        if(dataRegisto != null) sb.append(dataRegisto.toString()).append(";");
        else sb.append("null").append(";");
        if(dataConclusao != null) sb.append(dataConclusao.toString()).append(";");
        else sb.append("null").append(";");
        sb.append(numeroRegistoEquipamento).append(";").append(descricaoPedido);
        return sb.toString();
    }

    public IPedido clone(){
        return new PedidoOrcamento(this.nifCliente,this.numeroRegistoEquipamento,this.descricaoPedido,this.dataRegisto,this.dataConclusao);
    }

    public void concluiPedido(){
        this.dataConclusao = LocalDateTime.now();
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }
}
