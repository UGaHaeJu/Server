package com.ugahaeju.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStoreProdRes {
    public int code;
    public String message;
    public ArrayList<GetProductsRes> myProducts;
}
