package com.groupo.bank.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author anthony
 */
public class Validator {

    Connection conn;

    public Validator() throws SQLException, NamingException {
        conn = this.getConnection();
    }

    private Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    public boolean isValidAPI(String api) throws SQLException, NamingException {
        String verifyAPI = "SELECT * FROM api_keys WHERE api_key = ?";
        PreparedStatement st = conn.prepareStatement(verifyAPI);
        st.setString(1, api);
        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    public boolean isValidAccountNumber(String accountNumber) throws SQLException, NamingException {
        String verifyAccount = "SELECT * FROM account WHERE account_number = ?";
        PreparedStatement st;
        st = conn.prepareStatement(verifyAccount);
        st.setString(1, accountNumber);
        ResultSet rs2 = st.executeQuery();
        return rs2.next();

    }

    public boolean hasSufficentFunds(String accountNumber, double amount) throws SQLException, NamingException {
        String verifyAccount = "SELECT account_number, current_balance FROM account WHERE account_number = ?";
        PreparedStatement st2 = conn.prepareStatement(verifyAccount);
        st2.setString(1, accountNumber);
        ResultSet rs2 = st2.executeQuery();

        if (rs2.next()) {

            double balance = Double.parseDouble(rs2.getString("current_balance"));

            if (balance >= amount) {
                return true;
            }
        }

        return false;
    }

}
