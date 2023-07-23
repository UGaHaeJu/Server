package com.ugahaeju.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostStoresReq {
    public String store_id;
    public String grade;
    public String category;
    public String category2;
    public int sales_amount;
    public int sales_price;
    public boolean good_service;
}
