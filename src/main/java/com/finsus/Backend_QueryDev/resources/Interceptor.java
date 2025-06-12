package com.finsus.Backend_QueryDev.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsus.Backend_QueryDev.responses.ResponseGeneral;
import com.sun.security.auth.UserPrincipal;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Provider
@PreMatching
public class Interceptor implements ContainerRequestFilter {

    @Context
    private HttpServletRequest httpServletRequest;

    @Autowired
    public void filter(ContainerRequestContext request) throws IOException {
        String url = request.getUriInfo().getAbsolutePath().toString();
        System.out.println("LLEGO PETICION");
        System.out.println("Desde: " + httpServletRequest.getRemoteAddr());
        System.out.println(url);
        System.out.println(request.getRequest().getMethod());
        System.out.println(httpServletRequest.getContextPath());
        System.out.println("User: " + httpServletRequest.getHeader("user-agent"));
        if (httpServletRequest.getHeader("user-agent").contains("CFNetwork/1220.1")) {
            if (httpServletRequest.getHeader("user-agent").contains("Finsus")) {
                //ResponseQueryAmount rqa = new ResponseQueryAmount();
                //rqa.setStatusCode("800");
                //rqa.setMessageCode("Falta Autentificarte o Actualizar la APP");
                //request.abortWith(Response.status(Response.Status.OK).entity(gettjson(rqa)).type(MediaType.APPLICATION_JSON).build());

            }
        }

    }

    public String gettjson(Object responseCustomer) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        //System.out.println("-- after serialization --");
        String json = om.writeValueAsString(responseCustomer);
        System.out.println(json);
        return json;
    }

    public static String extractToken(HttpServletRequest request, String header, String regex, int index) {
        System.out.println("__extractToken__");
        System.out.println(request);
        System.out.println(header);
        System.out.println(regex);
        System.out.println(index);
        String token;
        try {
            token = request.getHeader(header);
            return token.split(regex)[index];
        }
        catch(NullPointerException|ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static boolean validateToken(String url, String token, String phone) {
        System.out.println("__validateToken__");
        System.out.println( url );
        System.out.println( token );
        System.out.println( phone );
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        String content = "application/x-www-form-urlencoded";
        RequestBody body = RequestBody.create(
                MediaType.parse(content),
                "phone=" + phone
        );
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", content)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseGeneral general = new ObjectMapper().readValue(response.body().string(), ResponseGeneral.class);
            return response.code() == 200 && "000".equals(general.getStatusCode());
        }
        catch(IOException|NullPointerException e) {
            return false;
        }
    }

    public static SecurityContext getAuthenticatedRoleSecurityContext(String role, SecurityContext parent) {
        Set<String> roles = new HashSet<>();
        roles.add(role);
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new UserPrincipal(role);
            }

            @Override
            public boolean isUserInRole(String role) {
                return roles.contains(role);
            }

            @Override
            public boolean isSecure() {
                return parent.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return parent.getAuthenticationScheme();
            }
        };
    }

    public static void addAuthenticatedRoleSecurityContext(String role, ContainerRequestContext request) {
        request.setSecurityContext(getAuthenticatedRoleSecurityContext(role, request.getSecurityContext()));
    }

}
