package com.finsus.Backend_QueryDev.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ContentVideo {

    private boolean exist;
    private String data;

    public ContentVideo(boolean exist, String data) {
        this.exist = exist;
        this.data = data;
    }
}
