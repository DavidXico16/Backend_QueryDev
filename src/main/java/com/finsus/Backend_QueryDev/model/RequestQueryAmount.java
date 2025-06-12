package com.finsus.Backend_QueryDev.model;

import lombok.Data;

@Data
public class RequestQueryAmount {

    private RequestCredentials requestCredentials;
    private String phone;
    private String typePhone = "Android|Huawei";

}
