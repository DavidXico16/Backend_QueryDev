package com.finsus.Backend_QueryDev.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.finsus.Backend_QueryDev.model.Images;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@JsonPropertyOrder(value = {"statusCode", "messageCode","status","interbankKey","amount","name","email","curp","idAsociado","images"}, alphabetic = false)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResponseQueryAmount {

    private String statusCode;
    private String messageCode;
    private String amount;
    private String points;
    private String name;
    private String interbankKey;
    private String status;
    private String email;
    private String curp;
    private String idAsociado;
    private Images images;

    public ResponseQueryAmount(String statusCode, String menssageCode, String amount,String points) {
        this.statusCode = statusCode;
        this.messageCode = menssageCode;
        this.amount = amount;
        this.points = points;
    }

    public ResponseQueryAmount(String statusCode, String menssageCode) {
        this.statusCode = statusCode;
        this.messageCode = menssageCode;
    }

    public ResponseQueryAmount(String name, String interbankKey, String status, String email, String curp, String idAsociado, Images images) {
        this.name = name;
        this.interbankKey = interbankKey;
        this.status = status;
        this.email = email;
        this.curp = curp;
        this.idAsociado = idAsociado;
        this.images = images;
    }
}
