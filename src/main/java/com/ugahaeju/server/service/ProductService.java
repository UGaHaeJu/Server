package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.ProductDao;
import com.ugahaeju.server.dto.PostProductsReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;
    public boolean postProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        boolean isSuccess = productDao.insertProducts(postProductsReq);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }
}
