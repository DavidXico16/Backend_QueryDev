package com.finsus.Backend_QueryDev.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsus.Backend_QueryDev.model.ContentVideo;
import com.finsus.Backend_QueryDev.model.Images;
import com.finsus.Backend_QueryDev.responses.ResponseQueryAmount;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class QueryRepository {

    private final static String URL_IMAGE = "http://216.250.114.120:2048/";
    private final static String URL_MW = "http://216.250.114.206:2048/";
    //private final static String URL_IMAGE = "http://74.208.84.208:8080/";
    //private final static String URL_MW = "http://172.17.100.22:2048/";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public String[] getAmount(String phone) {
        String sql = "SELECT a.amount, b.points FROM datapersonal AS b " +
                     "INNER JOIN appdata AS a ON a.curp = b.curp " +
                     "WHERE b.phone = ? AND a.branchOffice = ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, phone);
            query.setParameter(2, 100);

            List<Object[]> result = query.getResultList();

            if (result.isEmpty()) {
                return null;
            }

            Object[] row = result.get(0);
            String[] amount = new String[2];
            amount[0] = row[0] != null ? row[0].toString() : null;
            amount[1] = row[1] != null ? row[1].toString() : null;

            return amount;
        } catch (Exception e) {
            System.out.println("Error al obtener monto: " + e.getMessage());
            return null;
        }
    }

    /*@SuppressWarnings("deprecation")
    public String[] getAmount(String phone) {
        String sql = "SELECT a.amount, b.points FROM datapersonal AS b INNER JOIN appdata AS a ON a.curp = b.curp WHERE b.phone = ? AND a.branchOffice = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{phone, 100}, (rs, rowNum) -> {
                String[] amount = new String[2];
                amount[0] = rs.getString("amount");
                amount[1] = rs.getString("points");
                return amount;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            System.out.println("Error al obtener monto: " + e.getMessage());
            return null;
        }
    }*/

    public ResponseQueryAmount getUser(String phone) {
        ResponseQueryAmount user = null;
        String sql = "SELECT CONCAT(name, ' ', paternal, ' ', maternal) AS nameComplete, " +
                     "interbankKey, status, email, idAsociado, datapersonal.curp " +
                     "FROM datapersonal " +
                     "JOIN appdata ON appdata.curp = datapersonal.curp " +
                     "WHERE phone = ? AND branchOffice = ?";

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, phone);
            query.setParameter(2, 100);

            List<Object[]> result = query.getResultList();

            if (!result.isEmpty()) {
                Object[] row = result.get(0);

                String nameComplete = (String) row[0];
                String interbankKey = (String) row[1];
                String status = (String) row[2].toString();
                String email = (String) row[3];
                String idAsociado = row[4].toString();
                String curp = (String) row[5];

                user = new ResponseQueryAmount(
                    nameComplete,
                    interbankKey,
                    status,
                    email,
                    idAsociado,
                    curp,
                    getImages(idAsociado)
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return user;
    }
    /*public ResponseQueryAmount getUser(String phone) {
        ResponseQueryAmount user = null;
        String sql = "SELECT CONCAT(name, ' ', paternal, ' ', maternal) AS nameComplete, interbankKey, status, email, idAsociado, datapersonal.curp FROM datapersonal JOIN appdata ON appdata.curp = datapersonal.curp WHERE phone = ? AND branchOffice = ?";
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, phone, 100);

            if (!result.isEmpty()) {
                Map<String, Object> row = result.get(0); //primer dato

                String nameComplete = row.get("nameComplete").toString();
                String interbankKey = (String) row.get("interbankKey");
                String status = (String) row.get("status").toString();
                String email = (String) row.get("email");
                String idAsociado = row.get("idAsociado").toString();
                String curp = (String) row.get("curp");

                user = new ResponseQueryAmount(
                    nameComplete,
                    interbankKey,
                    status,
                    email,
                    idAsociado,
                    curp,
                    getImages(idAsociado)
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return user;
    }
*/
    
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
