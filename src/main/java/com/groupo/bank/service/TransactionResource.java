package com.groupo.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author anthony
 */
public class TransactionResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    public boolean addTransaction(String description, Double balance, int id) throws SQLException, NamingException {
        String t = "INSERT INTO transaction (description, post_balance, customer_id) VALUES (?,?,?);";
        Connection db = getConnection();
        PreparedStatement s = db.prepareStatement(t, Statement.RETURN_GENERATED_KEYS);
        s.setString(1, description);
        s.setDouble(2, balance);
        s.setInt(3, id);
        s.executeUpdate();
        ResultSet rs = s.getGeneratedKeys();
        return rs.next();
    }
}
