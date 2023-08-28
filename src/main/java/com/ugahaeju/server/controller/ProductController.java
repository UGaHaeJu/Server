package com.ugahaeju.server.controller;

import com.ugahaeju.server.dto.GetProductsRes;
import com.ugahaeju.server.dto.PostProductsReq;
import com.ugahaeju.server.dto.PostProductsRes;
import com.ugahaeju.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * [POST] /products
     * 크롤링한 Product 정보 저장 API
     **/
    @PostMapping(value = "/products")
    public PostProductsRes postProducts(@RequestBody List<PostProductsReq> postProductsReq){
        try{
            /*
            for(PostProductsReq postProductReq : postProductsReq) {
                if (postProductReq.product_id == null || postProductReq.product_id < 0) {
                    return new PostProductsRes(401, "상품 식별자의 형식이 올바르지 않습니다.");
                }
                if (postProductReq.store_id == null) {
                    return new PostProductsRes(401, "Store 식별자가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
                if (postProductReq.product_name == null) {
                    return new PostProductsRes(401, "상품 이름이 입력되지 않았습니다.");
                }
                if (postProductReq.star < 0) {
                    return new PostProductsRes(401, "별점이 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
                if (postProductReq.review < 0) {
                    return new PostProductsRes(401, "리뷰 수가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
                if (postProductReq.heart < 0) {
                    return new PostProductsRes(401, "찜하기 수가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
                if (postProductReq.date == null) {
                    return new PostProductsRes(401, "등록일이 입력되지 않았습니다.");
                }
                if (postProductReq.price < 0) {
                    return new PostProductsRes(401, "가격이 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
                if (postProductReq.point < 0) {
                    return new PostProductsRes(401, "포인트 접릭액이 입력되지 않았거나 형식이 올바르지 않습니다.");
                }
            }
             */

            boolean isSuccess = productService.postProducts(postProductsReq);
            if(isSuccess) {
                return new PostProductsRes(200, "상품 정보 저장에 성공하였습니다.");
            } else {
                return new PostProductsRes(400, "상품 정보 저장에 실패하였습니다.");
            }
        } catch (Exception e){
            return new PostProductsRes(400, "상품 정보 저장에 실패하였습니다.");
        }
    }

    /**
     * [GET] /products
     * 전체 Product 정보 조회 API
     **/
    @GetMapping(value = "/products")
    public ArrayList<GetProductsRes> getProducts(){
       ArrayList<GetProductsRes> products = new ArrayList<>();
        try{
            products = productService.getProducts();

            return products;
        } catch (Exception e){
            return products;
        }
    }

    /**
     * [GET] /products/review
     * 리뷰 많은 순으로 Product 정보 조회 API
     **/
    @GetMapping(value = "/products/review")
    public ArrayList<GetProductsRes> getProductsByReview(){
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try{
            //store로 나의 스토어 입력 (제외)
            products = productService.getProductsByReview("http://uhokf.co.kr");

            return products;
        } catch (Exception e){
            return products;
        }
    }

    /**
     * [GET] /products/score
     * 리뷰 점수 높은 순으로 Product 정보 조회 API
     **/
    @GetMapping(value = "/products/score")
    public ArrayList<GetProductsRes> getProductsByScore(){
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try{
            //store로 나의 스토어 입력 (제외)
            products = productService.getProductsByReviewScore("http://uhokf.co.kr");

            return products;
        } catch (Exception e){
            return products;
        }
    }
}


