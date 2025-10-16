/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author juanm
 */
@Named("mensajeBean")
@RequestScoped
public class MensajeBean implements Serializable {
    
    private String textoError;
    private String textoInfo;
    
    public String getTextoError() {
        return textoError;
    }
    
    public void setTextoError(String textoError) {
        this.textoError = textoError;
    }
    
    public String getTextoInfo() {
        return textoInfo;
    }
    
    public void setTextoInfo(String textoInfo) {
        this.textoInfo = textoInfo;
    }
    
    public void limpiar() {
        this.textoError = null;
        this.textoInfo = null;
    }
    
    public boolean hayError() {
        return textoError != null && !textoError.isEmpty();
    }
    
    public boolean hayInfo() {
        return textoInfo != null && !textoInfo.isEmpty();
    }
}
