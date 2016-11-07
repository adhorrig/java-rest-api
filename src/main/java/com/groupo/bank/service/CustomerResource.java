/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.groupo.bank.service;

/**
 *
 * @author adamhorrigan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/customer")
@Produces("application/json")
public class CustomerResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    public Customer getFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("customer_id"));
        customer.setEmail(rs.getString("email"));
        customer.setName(rs.getString("name"));
        return customer;
    }

    @GET
    public List getList() throws SQLException, NamingException {
        List events = new ArrayList<>();
        Connection db = getConnection();

        try {
            PreparedStatement st = db.prepareStatement("SELECT customer_id, email, name from customer");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Customer e = getFromResultSet(rs);
                events.add(e);
            }
            return events;
        } finally {
            db.close();
        }
    }
}
