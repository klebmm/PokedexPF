/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Dominio.Usuario;
import java.sql.*;

/**
 *
 * @author Caleb
 */
public class UsuarioJDBC {
    
    private static final String SQL_SELECT = "SELECT * FROM test.users ";
    private static final String SQL_INSERT = "INSERT INTO users(username, mail, password) VALUES(?,?,?)";

    public static String selectCorreo(String USER) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String correo = null;
        try {
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT + "WHERE username = '" + USER + "'");
            rs = stmt.executeQuery();
            rs.next();
            correo = rs.getString(4);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return correo;
    }
    
    public boolean select_validacion(Usuario datos) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean permiso = false;
        try {
            String condicion = SQL_SELECT + " where username='" + datos.getUsername() + "' and password ='" + datos.getPassword() + "'";
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(condicion);
            rs = stmt.executeQuery();
            while (rs.next()) {
                permiso = true;
            }
        } catch (SQLException ex) { 
           ex.printStackTrace(System.out);
        }
        return permiso;
    }
        
    public int insert(Usuario usuario){
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        ResultSet rs = null;
        try {
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getMail());
            stmt.setString(3, usuario.getPassword());
            rows = stmt.executeUpdate();
        }catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            ClsConexion.close(stmt);
            ClsConexion.close(conn);
        }
        return rows;
    }
}