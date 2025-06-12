package com.finsus.Backend_QueryDev.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Images {

    private ContentImage imageSelfie;
    private ContentImage imageIdBack;
    private ContentImage imageIdFront;
    private ContentVideo video;

}
