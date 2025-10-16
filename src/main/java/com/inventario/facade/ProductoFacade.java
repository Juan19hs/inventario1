/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.facade;

import com.inventario.Persistence.ProductoDAO;
import com.inventario.domain.ValidadorProducto;
import com.inventario.modelo.Producto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author juanm
 */
@Named("ProductoFacade")  // Accesible desde EL como #{productoFacade}
@ApplicationScoped        // Una sola instancia para toda la aplicación
public class ProductoFacade {
    
    @Inject
    private ProductoDAO dao;
    
    @Inject
    private ValidadorProducto validador;
    
    public List<Producto> listar() throws Exception {
        return dao.listar();
    }
    
    public void crear(Producto p) throws Exception {
        // Validar primero
        validador.validarProductoCompleto(p);
        
        // Verificar unicidad de código
        if (dao.existeCodigo(p.getCodigo())) {
            throw new Exception("Ya existe un producto con el código: " + p.getCodigo());
        }
        
        // Insertar
        dao.insertar(p);
    }
    
    public void actualizar(Producto p) throws Exception {
        // Validar
        validador.validarProductoCompleto(p);
        
        // Verificar que existe
        Producto existente = dao.buscarPorId(p.getId());
        if (existente == null) {
            throw new Exception("No existe un producto con ID: " + p.getId());
        }
        
        // Verificar que no haya otro producto con el mismo código
        if (dao.existeCodigo(p.getCodigo())) {
            Producto otroPorCodigo = dao.buscarPorCodigo(p.getCodigo()).orElse(null);
            if (otroPorCodigo != null && !otroPorCodigo.getId().equals(p.getId())) {
                throw new Exception("Ya existe otro producto con el código: " + p.getCodigo());
            }
        }
        
        dao.actualizar(p);
    }
    
    public void eliminar(int id) throws Exception {
        Producto producto = dao.buscarPorId(id);
        if (producto == null) {
            throw new Exception("No existe un producto con ID: " + id);
        }
        dao.eliminarPorId(id);
    }
    
    public Producto buscarPorId(int id) throws Exception {
        return dao.buscarPorId(id);
    }
}