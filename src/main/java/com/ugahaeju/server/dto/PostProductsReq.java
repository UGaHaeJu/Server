package com.ugahaeju.server.dto;

import java.time.LocalDate;

public class PostProductsReq {
    public Long product_id;
    public String store_id;
    public String product_name;
    public float star;
    public int review;
    public int heart;
    public LocalDate date;
    public int price;
    public boolean discount;
    public int point;
}
