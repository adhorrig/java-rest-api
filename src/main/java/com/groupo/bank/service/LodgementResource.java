package com.groupo.bank.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

/**
 *
 * @author anthony
 */
@Path("/lodgement")
@Produces("application/json")
public class LodgementResource {

    Connection conn;

    public LodgementResource() throws SQLException, NamingException {
        conn = this.getConnection();
    }

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    @POST
    @Path("/")
    @Produces("application/json")
    public Response mix(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String account = info.getQueryParameters().getFirst("account");
        double amount = Double.parseDouble(info.getQueryParameters().getFirst("amount"));

        Validator v = new Validator();

        if (v.isValidAPI(apiKey) && v.isValidAccountNumber(account)) {

            String updateBalance = "UPDATE account SET current_balance = current_balance + ? WHERE account_number = ?";
            PreparedStatement st3 = conn.prepareStatement(updateBalance);
            st3.setDouble(1, amount);
            st3.setString(2, account);
            st3.executeUpdate();

            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Lodgement complete."))).build();

        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API."))).build();
        }

    }

}