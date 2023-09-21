package com.ugahaeju.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllStoreProd {
    public long product_id;
    public String product_url;
    public String product_name;
    public int price;
    public int delivery_price;
    public int product_amount;
    public int review;
    public float review_score;
    public float heart;
    public String register_date;
    public String store_id;
    public String store_url;
    public String store_name;
    public String category1;
    public String category2;
    public String category3;
}
