/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.Persistence;

/**
 *
 * @author juanm
 */


import com.inventario.modelo.Producto;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import jakarta.enterprise.context.Dependent;

@Dependent  // Ciclo de vida ligado al bean que lo inyecta
public class ProductoDAO {

    @Inject
    private DataSource dataSource;

    // Ya NO recibe Connection por constructor
    // Cada método obtiene su propia conexión

    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY nombre ASC";
        
        try (Connection con = dataSource.getConnection();
             Statement st = con.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getBoolean("activo")
                );
                lista.add(p);
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar productos: " + ex.getMessage());
            throw ex;
        }
        return lista;
    }
    
    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM productos WHERE codigo=?";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar código: " + ex.getMessage());
            throw ex;
        }
        return false;
    }
    
    public Optional<Producto> buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM productos WHERE codigo=?";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getBoolean("activo")
                    );
                    return Optional.of(p);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar por código: " + ex.getMessage());
            throw ex;
        }
        return Optional.empty();
    }
    
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id=?";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getBoolean("activo")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar por id: " + ex.getMessage());
            throw ex;
        }
        return null;
    }
        
    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al insertar producto: " + ex.getMessage());
            throw ex;
        }
    }
    
    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE productos SET codigo=?, nombre=?, categoria=?, precio=?, stock=? WHERE id=?";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar producto: " + ex.getMessage());
            throw ex;
        }
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id=?";
        
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al eliminar producto: " + ex.getMessage());
            throw ex;
        }
    }
}
