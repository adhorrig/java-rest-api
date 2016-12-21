package com.groupo.bank.service;

/**
 *
 * @author anthony
 */
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

@Path("/withdrawl")
@Produces("application/json")

public class WithdrawalResource {
    

    private Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }
    
    @POST
    @Path("/")
    @Produces("application/json")
     public Response makeWidthdrawl(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String account = info.getQueryParameters().getFirst("account");
        double amount = Double.parseDouble(info.getQueryParameters().getFirst("amount"));
        
        Validator v = new Validator();
        Connection db = getConnection();
        
        if (v.isValidAPI(apiKey) && v.isValidAccountNumber(account)) {

            if (v.hasSufficentFunds(account, amount)) {
                String updateBalance = "UPDATE account SET current_balance = current_balance - ? WHERE account_number = ?";
                PreparedStatement st3 = db.prepareStatement(updateBalance);
                st3.setDouble(1, amount);
                st3.setString(2, account);
                st3.executeUpdate();

                return Response.status(200).entity(gson.toJson(new APIResponse("200", "Withdrawl complete."))).build();
            } else {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "The sender has insufficient funds to make this transfer."))).build();
            }

        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API."))).build();
        }


    }


    
    

}
