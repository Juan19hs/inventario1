/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.controller;

import com.inventario.facade.ProductoFacade;
import com.inventario.modelo.Producto;
import com.inventario.web.MensajeBean;
import com.inventario.web.PreferenciasBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author juanm
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/productos"})
public class ProductoServlet extends HttpServlet {
    
    @Inject
    private ProductoFacade productoFacade;
    
    @Inject
    private MensajeBean mensajeBean;
    
    @Inject
    private PreferenciasBean preferenciasBean;
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion == null || accion.equals("listar")) {
                listarProductos(request, response);
            } else if (accion.equals("nuevo")) {
                mostrarFormularioNuevo(request, response);
            } else if (accion.equals("editar")) {
                mostrarFormularioEditar(request, response);
            } else {
                listarProductos(request, response);
            }
        } catch (Exception e) {
            mensajeBean.setTextoError("Error: " + e.getMessage());
            request.getRequestDispatcher("/productos.jsp").forward(request, response);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        try {
            if (accion == null) {
                response.sendRedirect("productos");
                return;
            }
            
            switch (accion) {
                case "crear":
                    crearProducto(request, response);
                    break;
                case "actualizar":
                    actualizarProducto(request, response);
                    break;
                case "eliminar":
                    eliminarProducto(request, response);
                    break;
                default:
                    response.sendRedirect("productos");
            }
        } catch (Exception e) {
            mensajeBean.setTextoError("Error: " + e.getMessage());
            request.getRequestDispatcher("/productos.jsp").forward(request, response);
        }
    }
    
    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Producto> productos = productoFacade.listar();
        request.setAttribute("productos", productos);
        request.getRequestDispatcher("/productos.jsp").forward(request, response);
    }
    
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/productoForm.jsp").forward(request, response);
    }
    
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    int id = Integer.parseInt(request.getParameter("id"));
    Producto producto = productoFacade.buscarPorId(id);

    if (producto == null) {
        mensajeBean.setTextoError("Producto no encontrado");
        response.sendRedirect("productos");
        return;
    }

    request.setAttribute("producto", producto);
    listarProductos(request, response); // vuelve a cargar todo y muestra el mismo JSP
}
    private void crearProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        Producto p = new Producto();
        p.setCodigo(request.getParameter("codigo"));
        p.setNombre(request.getParameter("nombre"));
        p.setCategoria(request.getParameter("categoria"));
        p.setPrecio(Double.parseDouble(request.getParameter("precio")));
        p.setStock(Integer.parseInt(request.getParameter("stock")));
        p.setActivo(true);
        
        productoFacade.crear(p);
        mensajeBean.setTextoInfo("Producto creado exitosamente");
        response.sendRedirect("productos");
    }
    
    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        Producto p = new Producto();
        p.setId(Integer.parseInt(request.getParameter("id")));
        p.setCodigo(request.getParameter("codigo"));
        p.setNombre(request.getParameter("nombre"));
        p.setCategoria(request.getParameter("categoria"));
        p.setPrecio(Double.parseDouble(request.getParameter("precio")));
        p.setStock(Integer.parseInt(request.getParameter("stock")));
        
        productoFacade.actualizar(p);
        mensajeBean.setTextoInfo("Producto actualizado exitosamente");
        response.sendRedirect("productos");
    }
    
    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        int id = Integer.parseInt(request.getParameter("id"));
        productoFacade.eliminar(id);
        mensajeBean.setTextoInfo("Producto eliminado exitosamente");
        response.sendRedirect("productos");
    }
}