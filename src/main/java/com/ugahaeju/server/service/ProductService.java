package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.ProductDao;
import com.ugahaeju.server.dao.StoreDao;
import com.ugahaeju.server.dto.AllStoreProd;
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
    private final StoreDao storeDao;

    /** Product INSERT API **/
    public boolean postProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        boolean isSuccess = productDao.insertProducts(postProductsReq);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }

    /** MyProducts SELECT API **/
    public ArrayList<GetProductsRes> getMyProducts(String url) throws IOException, InterruptedException {
        ArrayList<GetProductsRes> myProducts = productDao.selectMyProducts(url);
        return myProducts;
    }

    /** MyProduct SELECT API **/
    public GetProductsRes getMyProduct(long id) throws IOException, InterruptedException {
        GetProductsRes myProduct = productDao.selectMyProduct(id);
        return myProduct;
    }

    /** Product 와 Store 조인 데이터 SELECT API **/
    public ArrayList<AllStoreProd> getProductNStore() throws IOException, InterruptedException {
        ArrayList<AllStoreProd> all = productDao.selectProductNStore();
        return all;
    }


    /** Product SELECT API **/
    public ArrayList<GetProductsRes> getProducts() throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = productDao.selectProducts();
        return products;
    }

    /** Product SELECT API ordered by review **/
    public ArrayList<GetProductsRes> getProductsByReview(String store) throws IOException, InterruptedException {
        String store_id = "";
        if(store.substring(0, 5).equals("http")){
            store_id = storeDao.selectStoreIdByURL(store);
        } else {
            store_id = storeDao.selectStoreIdByName(store);
        }

        // 리뷰 많은 순으로 정보 조회
        ArrayList<GetProductsRes> products = productDao.selectProductsByReview(store_id);
        return products;
    }

    /** Product SELECT API ordered by review score **/
    public ArrayList<GetProductsRes> getProductsByReviewScore(String store) throws IOException, InterruptedException {
        String store_id = "";
        if(store.substring(0, 5).equals("http")){
            store_id = storeDao.selectStoreIdByURL(store);
        } else {
            store_id = storeDao.selectStoreIdByName(store);
        }

        // 리뷰 평점 높은 순으로 정보 조회
        ArrayList<GetProductsRes> products = productDao.selectProductsByReviewScore(store_id);
        return products;
    }
}
