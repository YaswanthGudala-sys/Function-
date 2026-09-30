package com.hallbooking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Main {
  static final String DB_URL="jdbc:mysql://localhost:3306/function_hall?useSSL=false&serverTimezone=UTC";
  static final String DB_USER="root";
  static final String DB_PASSWORD="root"; // change this
  static final Gson gson=new Gson();

  public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver");
    HttpServer s=HttpServer.create(new InetSocketAddress(8080),0);
    s.createContext("/api/health", e -> send(e,200,"{\"ok\":true}"));
    s.createContext("/api/bookings", Main::bookings);
    s.setExecutor(null); s.start(); System.out.println("Java JDBC API: http://localhost:8080");
  }
  static Connection db() throws SQLException { return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD); }
  static void bookings(HttpExchange e) throws IOException {
    if(!e.getRequestMethod().equalsIgnoreCase("POST")){send(e,405,"{\"error\":\"POST required\"}");return;}
    try(InputStream in=e.getRequestBody()){
      JsonObject j=gson.fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),JsonObject.class);
      String sql="INSERT INTO bookings(customer_name,phone,email,event_type,event_date,guests,hall_name,hall_category,food_items,total_amount,payment_method) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
      try(Connection c=db(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
        p.setString(1,j.get("customerName").getAsString()); p.setString(2,j.get("phone").getAsString()); p.setString(3,j.get("email").getAsString());
        p.setString(4,j.get("eventType").getAsString()); p.setDate(5,Date.valueOf(j.get("date").getAsString())); p.setInt(6,j.get("guests").getAsInt());
        p.setString(7,j.get("hallName").getAsString()); p.setString(8,j.get("hallCategory").getAsString()); p.setString(9,j.get("foodItems").getAsString());
        p.setBigDecimal(10,j.get("total").getAsBigDecimal()); p.setString(11,j.get("paymentMethod").getAsString()); p.executeUpdate();
        try(ResultSet r=p.getGeneratedKeys()){ long id=r.next()?r.getLong(1):0; send(e,201,"{\"success\":true,\"bookingId\":"+id+"}"); }
      }
    }catch(Exception ex){ex.printStackTrace();send(e,500,"{\"success\":false,\"error\":\"Database/API error\"}");}
  }
  static void send(HttpExchange e,int code,String body)throws IOException{e.getResponseHeaders().set("Content-Type","application/json");e.getResponseHeaders().set("Access-Control-Allow-Origin","*");byte[] b=body.getBytes(StandardCharsets.UTF_8);e.sendResponseHeaders(code,b.length);try(OutputStream o=e.getResponseBody()){o.write(b);}}
}
