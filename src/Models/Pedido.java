/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.time.LocalDate;

/**
 *
 * @author Daniela Nahir Romero
 */
public class Pedido extends Base{
    //Definimos los atributos:
    private String numeroPedido;
    private java.time.LocalDate fecha;
    private String clienteNombre;
    private double total;
    private Estado estado;
    private Envio envio;

    //Constructor completo:

    public Pedido(String numero, LocalDate fecha, String clienteNombre, double total, Estado estado, Envio envio, Long id) {
        super(id, false);
        this.numeroPedido = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    
    //Constructor por defecto:
    
    public Pedido() {
        super();
    }
    
    //Getters y Setters

    public String getNumero() {
        return numeroPedido;
    }

    public void setNumero(String numero) {
        this.numeroPedido = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    
    

    //Generamos el toString:

    @Override
    public String toString() {
        return "Pedido{" + "ID=" + getId() + 
                "numero=" + numeroPedido + 
                ", fecha=" + fecha + 
                ", clienteNombre=" + clienteNombre + 
                ", total=" + total + 
                ", estado=" + estado + 
                ", eliminado=" + isEliminado() + 
                ", envio=" + envio + '}';
    }
   
    
}
