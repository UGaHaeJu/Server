package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.BigQueryDao;
import com.ugahaeju.server.model.PostProductsReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final BigQueryDao bigQueryDao;
    public boolean postProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        boolean isSuccess = bigQueryDao.insertProducts(postProductsReq);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }
}
