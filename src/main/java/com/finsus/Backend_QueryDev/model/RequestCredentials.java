package com.finsus.Backend_QueryDev.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class RequestCredentials {

    private String loginId;
    private String applicationCode;
    private String password;

}
