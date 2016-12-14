package com.groupo.bank.service;

/**
 *
 * @author anthony
 */
import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/transfer")
@Produces("application/json")
public class TransferResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }


    @POST
    @Path("/create")
    @Produces("application/json")
    public Response mix(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();

        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String accountNumber = info.getQueryParameters().getFirst("account_number");
        String amount = info.getQueryParameters().getFirst("amount");

        String verifyAPI = "SELECT * FROM api_keys WHERE api_key = ?";
        PreparedStatement st = db.prepareStatement(verifyAPI);
        st.setString(1, apiKey);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {

            String verifyAccount = "SELECT * FROM account WHERE account_number = ?";
            PreparedStatement st2 = db.prepareStatement(verifyAccount);
            st2.setString(1, accountNumber);
            ResultSet rs2 = st.executeQuery();

            if (rs2.next()) {

                String updateBalance = "UPDATE account SET current_balance = current_balance + ? WHERE account_number = ?";
                PreparedStatement st3 = db.prepareStatement(updateBalance);
                st3.setString(1, amount);
                st3.setString(2, accountNumber);
                st3.executeUpdate();
            }

        } else {
            return Response.status(500).entity(gson.toJson("No API key specified or invalid API key.")).build();
        }

        return Response.status(200).entity(gson.toJson("Transfer successful.")).build();

    }

}
