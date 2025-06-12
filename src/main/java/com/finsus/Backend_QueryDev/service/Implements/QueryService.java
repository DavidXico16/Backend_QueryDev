package com.finsus.Backend_QueryDev.service.Implements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsus.Backend_QueryDev.model.RequestCredentials;
import com.finsus.Backend_QueryDev.repository.QueryRepository;
import com.finsus.Backend_QueryDev.responses.ResponseQueryAmount;
import com.finsus.Backend_QueryDev.service.IQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QueryService implements IQueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Override
    public void ValidateCredentials(RequestCredentials credentials) throws UnsatisfiedLinkError {
        String loginId = credentials.getLoginId();
        String applicationCode = credentials.getApplicationCode();
        String password = credentials.getPassword();
        if (!loginId.equals("CDFMH") || !applicationCode.equals("CDF585WEB") || !password.equals("Fg595@700")) {
            throw new UnsatisfiedLinkError(getJsonResponse(new ResponseQueryAmount("800", "Error de Credenciales"))); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private String getJsonResponse(Object object) {
        String json = "{}";
        try {
            ObjectMapper om = new ObjectMapper();
            json = om.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(QueryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @Override
    public void Procedencia(String userAgent, String typePhone) throws UnsatisfiedLinkError {
        if (userAgent.contains("CFNetwork/1220.1")) {
            if (userAgent.contains("Finsus")) {
                if (!typePhone.contains("ios")) {
                    throw new UnsupportedOperationException(getJsonResponse(new ResponseQueryAmount("800", "Para ver tu saldo, por favor inicia sesión."))); //To change body of generated methods, choose Tools | Templates.
                }
            }
        }

    }

   @Override
    public String QueryAmount(String phone) {
        //QueryRepository base = new QueryRepository();
        String[] amount = queryRepository.getAmount(phone);
        if (amount == null) {
            return getJsonResponse(new ResponseQueryAmount("100", "Intente mas tarde"));
        } else {
            BigDecimal amount1 = new BigDecimal(amount[0]);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            amount[0] = df.format(amount1);
            return getJsonResponse(new ResponseQueryAmount("000", "Saldo encontrado", amount[0], amount[1]));
        }
    }

    @Override
    public void CheckPhone(String phone) throws UnsatisfiedLinkError {
        // comprueba que el Telefono este correcto
        Pattern p = Pattern.compile("^\\d{9,10}$");
        Matcher m;
        boolean test;
        if (phone != null) {
            m = p.matcher(phone);
            test = m.find();
            if (!test) {
                throw new UnsupportedOperationException(getJsonResponse(new ResponseQueryAmount("800", "El Numero No Es Valido")));
            }
        } else {
            throw new UnsupportedOperationException(getJsonResponse(new ResponseQueryAmount("800", "Informacion Importante Nula")));
        }
    }

    @Override
    public String QueryUser(String phone) {
        ResponseQueryAmount response = queryRepository.getUser(phone);
        if (response != null) {
            response.setStatusCode("000");
            response.setMessageCode("Usuario Existente");
            return getJsonResponse(response);
        } else {
            response = new ResponseQueryAmount("100", "Usuario No Existente");
            return getJsonResponse(response);
        }
    }

}
