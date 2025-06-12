package com.finsus.Backend_QueryDev.repository;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {

    private Connection con;
    private InitialContext ctx;

    public Conexion() {
        String jndiName = "java:/APP";
        //Obtenemos el contexto inicial (dónde comienza el directorio de nombres)
        try {
            ctx = new InitialContext();
            //Asignamos el recurso a un objeto de tipo DataSource (Binding)
            DataSource ds = (DataSource) ctx.lookup(jndiName);
            con = ds.getConnection();
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void closeConexion() {
        try {
            con.close();
            ctx.close();
        } catch (SQLException | NamingException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
