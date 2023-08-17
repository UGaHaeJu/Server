package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.ProductDao;
import com.ugahaeju.server.dto.GetProductsRes;
import com.ugahaeju.server.dto.PostProductsReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;

    /** Product INSERT API **/
    public boolean postProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        boolean isSuccess = productDao.insertProducts(postProductsReq);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }

    /** Product SELECT API **/
    public ArrayList<GetProductsRes> getProducts() throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = productDao.selectProducts();
        return products;
    }
}
