package com.finsus.Backend_QueryDev.controller;

import com.finsus.Backend_QueryDev.model.RequestQueryAmount;
import com.finsus.Backend_QueryDev.resources.Interceptor;
import com.finsus.Backend_QueryDev.service.IQueryService;
//import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScope
//@Path( "Service" )
@RestController()
@RequestMapping("Service")
public class RestService {

    private static final String TOKEN_URL = "https://pruebas.dimmerapp.com/LoginDev/core/authTkn";

    @Autowired
    private IQueryService service;

    private HttpServletRequest httpServletRequest;

    private Interceptor interceptor;

    @PostMapping( "/QueryAmount" )
    public String QueryAmount( @RequestBody RequestQueryAmount requestQueryAmount){
        System.out.println("entrooooooooo");
        System.out.println(requestQueryAmount);
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
        System.out.println("__QueryUser__");
        System.out.println( requestQueryAmount );
        System.out.println( requestQueryAmount.getRequestCredentials() );
        try{
            service.ValidateCredentials( requestQueryAmount.getRequestCredentials() );
            service.CheckPhone( requestQueryAmount.getPhone() );
        }catch(UnsatisfiedLinkError e){
            return e.getMessage();
        }
        return service.QueryUser( requestQueryAmount.getPhone() );
    }

}
