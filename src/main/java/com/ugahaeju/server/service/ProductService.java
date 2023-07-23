package com.ugahaeju.server.service;

import com.google.cloud.bigquery.*;
import com.ugahaeju.server.model.PostProductsReq;
import com.ugahaeju.server.utils.BigQuery.BigQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final BigQueryService bigQueryService;
    public boolean postProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        boolean isSuccess = bigQueryService.insertProducts(postProductsReq);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }
}
