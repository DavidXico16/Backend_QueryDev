package com.finsus.Backend_QueryDev.responses;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResponseGeneral {

    private String statusCode;
    private String messageCode;

}
