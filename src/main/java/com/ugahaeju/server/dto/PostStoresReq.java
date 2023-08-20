package com.ugahaeju.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostStoresReq {
    public String store_id;
    public String storeURL;
    public String store_name;
    public String category1;
    public String category2;
    public String category3;
    //public String grade;
    //public int sales_amount;
    //public int sales_price;
    //public boolean good_service;
}
