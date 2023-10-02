package com.ugahaeju.server.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductsRes {
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
}
