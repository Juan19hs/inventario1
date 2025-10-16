/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.domain;

import com.inventario.modelo.Producto;
import jakarta.enterprise.context.Dependent;

/**
 *
 * @author juanm
 */
@Dependent
public class ValidadorProducto {
    
    public void validarCodigo(String codigo) throws Exception {
        if (codigo == null || codigo.trim().length() < 3) {
            throw new Exception("El código debe tener al menos 3 caracteres");
        }
    }
    
    public void validarNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().length() < 5) {
            throw new Exception("El nombre debe tener al menos 5 caracteres");
        }
    }
    
    public void validarCategoria(String categoria) throws Exception {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new Exception("La categoría es obligatoria");
        }
        
        String cat = categoria.trim().toUpperCase();
        if (!cat.equals("ELECTRONICOS") && !cat.equals("ACCESORIOS") 
            && !cat.equals("MUEBLES") && !cat.equals("ROPA")) {
            throw new Exception("Categoría inválida. Use: ELECTRÓNICOS, ELECTRONICOS, ACCESORIOS, MUEBLES O ROPA");
        }
    }
    
    public void validarPrecio(Double precio) throws Exception {
        if (precio == null || precio <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }
    }
    
    public void validarStock(Integer stock) throws Exception {
        if (stock == null || stock < 0) {
            throw new Exception("El stock no puede ser negativo");
        }
    }
    
    public void validarProductoCompleto(Producto p) throws Exception {
        if (p == null) {
            throw new Exception("El producto no puede ser nulo");
        }
        validarCodigo(p.getCodigo());
        validarNombre(p.getNombre());
        validarCategoria(p.getCategoria());
        validarPrecio(p.getPrecio());
        validarStock(p.getStock());
    }
}