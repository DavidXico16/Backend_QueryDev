package com.finsus.Backend_QueryDev.service;

import com.finsus.Backend_QueryDev.model.RequestCredentials;

public interface IQueryService {
    
    void ValidateCredentials( RequestCredentials credentials )throws UnsatisfiedLinkError;
    
    void Procedencia(String userAgent,String typePhone)throws UnsatisfiedLinkError;
    
    void CheckPhone(String phone)throws UnsatisfiedLinkError;
    
    String QueryAmount(String phone);
    
    String QueryUser(String phone);
}
