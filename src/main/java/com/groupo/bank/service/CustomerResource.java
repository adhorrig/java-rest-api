package com.groupo.bank.service;

/**
 *
 * @author adamhorrigan
 * @author anthonybloomer
 */
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/customer")
@Produces("application/json")
public class CustomerResource {

    Connection conn;

    public CustomerResource() throws SQLException, NamingException {
        conn = this.getConnection();
    }

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
    @Path("/{id}")
    @Produces("application/json")
    public Response getCustomerById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Validator v = new Validator();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            String verifyAPI = "SELECT * FROM customer WHERE customer_id = ?";
            PreparedStatement st = this.conn.prepareStatement(verifyAPI);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            List events = new ArrayList<>();
            if (rs.next()) {
                Customer e = getFromResultSet(rs);
                events.add(e);

            }

            return Response.status(200).entity(gson.toJson(events)).build();

        }

        return null;

    }

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getList(@Context UriInfo info) throws SQLException, NamingException {
        Gson gson = new Gson();
        Validator v = new Validator();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            List events = new ArrayList<>();
            Connection db = getConnection();

            try {
                PreparedStatement st = db.prepareStatement("SELECT customer_id, email, name from customer");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    Customer e = getFromResultSet(rs);
                    events.add(e);
                }
                return Response.status(200).entity(gson.toJson(events)).build();
            } finally {
                db.close();
            }
        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API key"))).build();
        }

    }

    @POST
    @Path("/create")
    @Produces("application/json")
    public Response mix(@Context UriInfo info) throws SQLException, NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Gson gson = new Gson();
        Connection db = getConnection();

        String name = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("name"), "UTF-8");
        String email = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("email"), "UTF-8");
        String address = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("address"), "UTF-8");
        String password = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("password"), "UTF-8");
        String apiKey = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("api_key"), "UTF-8");

        Validator v = new Validator();

        if (v.isValidAPI(apiKey)) {

            String generatedPassword = null;

            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            generatedPassword = sb.toString();

            String accountType;
            try {
                accountType = info.getQueryParameters().getFirst("account_type");
                if (accountType.equalsIgnoreCase("Current")) {
                    accountType = "1";
                } else {
                    accountType = "2";
                }

            } catch (java.lang.NullPointerException e) {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "No account type specified."))).build();
            }

            String sort = UUID.randomUUID().toString().substring(0, 8);
            String account = UUID.randomUUID().toString().substring(0, 8);

            int balance = 0;

            try {
                String insertCustomer = "INSERT INTO customer"
                        + "(name, email, address, password) VALUES"
                        + "(?,?,?,?)";

                String createAccount = "INSERT INTO account"
                        + "(sort_code, account_number, current_balance, account_type, customer_id) VALUES"
                        + "(?,?,?,?,?)";

                PreparedStatement st = db.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);
                st.setString(1, name);
                st.setString(2, email);
                st.setString(3, address);
                st.setString(4, generatedPassword);
                st.executeUpdate();

                // get the last insert ID
                int lastInsertId = 0;
                ResultSet rs = st.getGeneratedKeys();

                if (rs.next()) {
                    lastInsertId = rs.getInt(1);
                }

                PreparedStatement stm = db.prepareStatement(createAccount);
                stm.setString(1, sort);
                stm.setString(2, account);
                stm.setInt(3, balance);
                stm.setString(4, accountType);
                stm.setInt(5, lastInsertId);
                stm.executeUpdate();

                return Response.status(200).entity(gson.toJson(new APIResponse("200", "Customer created successfully."))).build();
            } finally {
                db.close();
            }
        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API key."))).build();
        }

    }
}
