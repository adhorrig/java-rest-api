package com.groupo.bank.service;

/**
 *
 * @author anthony
 */
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/transfer")
@Produces("application/json")
public class TransferResource {

    Connection conn;

    public TransferResource() throws SQLException, NamingException {
        conn = this.getConnection();
    }

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    private boolean isValidAPI(String api) throws SQLException, NamingException {

        String verifyAPI = "SELECT * FROM api_keys WHERE api_key = ?";
        PreparedStatement st = conn.prepareStatement(verifyAPI);
        st.setString(1, api);
        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    private boolean isValidAccountNumber(String accountNumber) throws SQLException, NamingException {
        String verifyAccount = "SELECT * FROM account WHERE account_number = ?";
        PreparedStatement st;
        st = conn.prepareStatement(verifyAccount);
        st.setString(1, accountNumber);
        ResultSet rs2 = st.executeQuery();
        return rs2.next();

    }

    private boolean hasSufficentFunds(String accountNumber, double amount) throws SQLException, NamingException {
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

    @POST
    @Path("/create")
    @Produces("application/json")
    public Response mix(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String from = info.getQueryParameters().getFirst("from");
        String to = info.getQueryParameters().getFirst("to");
        double amount = Double.parseDouble(info.getQueryParameters().getFirst("amount"));

        if (isValidAPI(apiKey) && isValidAccountNumber(from) && isValidAccountNumber(to)) {

            if (this.hasSufficentFunds(from, amount)) {
                String updateBalance = "UPDATE account SET current_balance = current_balance + ? WHERE account_number = ?";
                PreparedStatement st3 = conn.prepareStatement(updateBalance);
                st3.setDouble(1, amount);
                st3.setString(2, to);
                st3.executeUpdate();
                
                String updateSenderBalance = "UPDATE account SET current_balance = current_balance - ? WHERE account_number = ?";
                PreparedStatement st4 = conn.prepareStatement(updateSenderBalance);
                st4.setDouble(1, amount);
                st4.setString(2, from);
                st4.executeUpdate();
                
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "Transfer successful."))).build();
            } else {
                return Response.status(500).entity(gson.toJson(new APIResponse("500", "The sender has insufficient funds to make this transfer."))).build();
            }

        } else {
            return Response.status(500).entity(gson.toJson(new APIResponse("500", "Invalid API."))).build();
        }


    }

}
