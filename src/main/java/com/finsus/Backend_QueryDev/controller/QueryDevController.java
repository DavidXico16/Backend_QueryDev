package com.finsus.Backend_QueryDev.controller;

import com.finsus.Backend_QueryDev.model.RequestQueryAmount;
import com.finsus.Backend_QueryDev.resources.Interceptor;
import com.finsus.Backend_QueryDev.service.IQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("Service")
public class QueryDevController {

    private static final String TOKEN_URL = "https://pruebas.dimmerapp.com/LoginDev/core/authTkn";

    @Autowired
    private IQueryService service;

    private HttpServletRequest httpServletRequest;

    private Interceptor interceptor;

    @PostMapping( "/QueryAmount" )
    public String QueryAmount( @RequestBody RequestQueryAmount requestQueryAmount){
        try{
            //service.ValidateCredentials(requestQueryAmount.getRequestCredentials());
            if (! Interceptor.validateToken(
                    TOKEN_URL,
                    Interceptor.extractToken(httpServletRequest, "Authorization", " ", 1),
                    requestQueryAmount.getPhone()
            )) {
                throw new UnsatisfiedLinkError("{\"statusCode\":\"800\",\"messageCode\":\"Error de Credenciales\"}");
            }
            service.Procedencia(httpServletRequest.getHeader("user-agent"), requestQueryAmount.getTypePhone());
            service.CheckPhone(requestQueryAmount.getPhone());
        }catch(UnsatisfiedLinkError e){
            return e.getMessage();
        }
        return service.QueryAmount(requestQueryAmount.getPhone());
    }

    @PostMapping( "/QueryUser" )
    public String QueryUser( @RequestBody RequestQueryAmount requestQueryAmount) {
        try{
            service.ValidateCredentials( requestQueryAmount.getRequestCredentials() );
            service.CheckPhone( requestQueryAmount.getPhone() );
        }catch(UnsatisfiedLinkError e){
            return e.getMessage();
        }
        return service.QueryUser( requestQueryAmount.getPhone() );
    }

}
