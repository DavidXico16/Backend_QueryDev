package com.finsus.Backend_QueryDev.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsus.Backend_QueryDev.model.ContentVideo;
import com.finsus.Backend_QueryDev.model.Images;
import com.finsus.Backend_QueryDev.responses.ResponseQueryAmount;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

@Repository
public class QueryRepository extends Conexion {

    private final static String URL_IMAGE = "http://216.250.114.120:2048/";
    private final static String URL_MW = "http://216.250.114.206:2048/";
    //private final static String URL_IMAGE = "http://74.208.84.208:8080/";
    //private final static String URL_MW = "http://172.17.100.22:2048/";


    public String [] getAmount(String phone) {
        String[] amount = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            String query = "select a.amount,b.points FROM datapersonal as b INNER JOIN appdata as a ON a.curp = b.curp where b.phone = '" + phone + "' AND a.branchOffice = '100';";
            pst = getConnection().prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                amount = new String[2];
                amount[0] = rs.getString(1);
                amount[1] = rs.getInt(2) + "";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {

                if (pst != null) {
                    pst.close();
                }
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
            closeConexion();
        }

        return amount;
    }

    public ResponseQueryAmount getUser(String phone){
        PreparedStatement pst = null;
        ResponseQueryAmount user = null;
        ResultSet rs = null;
        try {
            String query = "select CONCAT(name,' ',paternal,' ',maternal) AS nameComplete,interbankKey,status,email,idAsociado,datapersonal.curp FROM datapersonal JOIN appdata ON appdata.curp = datapersonal.curp WHERE phone = '" + phone + "' AND branchOffice=100;";
            pst = getConnection().prepareStatement(query);
            rs = pst.executeQuery();
            System.out.println("fase");
            System.out.println(rs);
            if (rs.next()) {
                user = new ResponseQueryAmount(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(6), rs.getString(5), getImages(rs.getString(5)));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {

                if (pst != null) {
                    pst.close();
                }
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
            //closeConexion();
        }
        return user;
    }

    public Images getImages(String idAsociado) {
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"idAsociado\":\""+idAsociado+"\"\r\n}");
        Request request = new Request.Builder().url(URL_IMAGE+"ImageServer/ImageControl/getImageContent")
                .method("POST", body).addHeader("Content-Type", "application/json").build();
        Response response = null;
        Images responseBoot = null;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.code());
            if (response.code() == 200) {
                ObjectMapper om = new ObjectMapper();
                responseBoot = om.readValue(response.body().string(), Images.class);
                responseBoot.setVideo(getVideo(idAsociado));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBoot;
    }

    public ContentVideo getVideo(String idAsociado) {
        String video = "N/A";
        boolean exist = false;
        ContentVideo data;
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"idAsociado\":\""+idAsociado+"\"}");
        Request request = new Request.Builder().url(URL_MW+"GetVideo/Service/GetVideo")
                .method("POST", body).addHeader("Content-Type", "application/json").build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            System.out.println("Esto responde el servicio GetVideo: "+response.code());
            if (response.code() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if(!jsonResponse.getString("messageCode").equals("N/A")){
                    System.out.println("Entro correcto a GetVideo");
                    video =  jsonResponse.getString("messageCode");
                    exist = true;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (JSONException ex) {
            Logger.getLogger(QueryRepository.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (response != null) {
                response.close();
            }
            data = new ContentVideo(exist, video);
        }
        return data;
    }

}
