package com.ugahaeju.server.controller;

import com.ugahaeju.server.dto.*;
import com.ugahaeju.server.service.DashboardService;
import com.ugahaeju.server.service.ProductService;
import com.ugahaeju.server.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final ProductService productService;
    private final DashboardService dashboardService;

    /** store table 정보 저장 **/
    @RequestMapping(value = "/stores")
    public PostStoresRes insertStores(@RequestBody List<PostStoresReq> postStoresReq){
        try{
            /*
            for(PostStoresReq postStoreReq : postStoresReq) {
                if (postStoreReq.store_id == null) {
                    return new PostStoresRes(401, "Store 식별자가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }

                if (postStoreReq.category == null) {
                    return new PostStoresRes(401, "카테고리가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }

                if (postStoreReq.category2 == null) {
                    return new PostStoresRes(401, "카테고리2가 입력되지 않았거나 형식이 올바르지 않습니다.");
                }

                if (postStoreReq.sales_amount < 0) {
                    return new PostStoresRes(401, "총 판매량의 형식이 올바르지 않습니다.");
                }

                if (postStoreReq.sales_price < 0) {
                    return new PostStoresRes(401, "총 판매수익의 형식이 올바르지 않습니다.");
                }
            }
             */

            if(storeService.insertStores(postStoresReq)) {
                return new PostStoresRes(200, "상점 정보 저장에 성공하였습니다.");
            } else {
                return new PostStoresRes(400, "상점 정보 저장에 실패하였습니다.");
            }
        } catch (Exception e){
            return new PostStoresRes(400, "상점 정보 저장에 실패하였습니다.");
        }
    }

    /** <내 스토어 분석> 기능을 위한 스토어 상품 정보 조회 **/
    @RequestMapping(value = "/mystore/products")
    public GetStoreProdRes getMyStoreProducts(@RequestParam(value = "url") String storeUrl){
        ArrayList<GetProductsRes> myProducts = new ArrayList<>();

        try{
            if(storeUrl == null){
                return new GetStoreProdRes(401, "스토어 이름 혹은 스토어 URL을 입력하지 않았습니다.", myProducts);
            }

            // 내 스토어의 상품 정보
            myProducts = productService.getMyProducts(storeUrl);

            return new GetStoreProdRes(200, "내 스토어의 상품 반환에 성공하였습니다.", myProducts);
        } catch (Exception e){
            return new GetStoreProdRes(400, "내 스토어의 상품 반환에 실패하였습니다.", myProducts);
        }
    }

    /** <내 스토어 분석> 기능을 위한 선별된 상품 분석 대시보드 조회
     * @return**/
    @RequestMapping(value = "/mystore/dashboard")
    public GetDashboardRes getProductDashBoard(@RequestParam(value = "product") long product){
        GetProductsRes myProduct = new GetProductsRes();
        String dashboardUrl = "";
        ArrayList<AllStoreProd> all = new ArrayList<>();
        try{
            if(product < 0){
                return new GetDashboardRes(401, "상품 정보가 올바르게 입력되지 않았습니다.", dashboardUrl);
            }

            // 상품 정보
            myProduct = productService.getMyProduct(product);
            System.out.println(myProduct.product_name);

            // 스토어 + 상품 조인 정보
            all = productService.getProductNStore();
            System.out.println(all);

            // 대시보드 요청 -> 저장소에 이미지 저장 후 url 반환
            //dashboardUrl = analyzeStore(myProduct, all);

            return new GetDashboardRes(200, "내 스토어 분석 대시보드 반환에 성공하였습니다.", dashboardUrl);
        } catch (Exception e){
            return new GetDashboardRes(400, "내 스토어 분석 대시보드 반환에 실패하였습니다.", dashboardUrl);
        }
    }

//    /** <내 스토어 분석> 기능을 위한 정보 요청 **/
//    @RequestMapping(value = "/analyze")
//    public GetStoreProdRes analyzeMyStore(@RequestParam(value = "store") String store){
//        MyStore myStore = new MyStore();
//        ArrayList<GetProductsRes> mostReviewed = new ArrayList<>();
//        ArrayList<GetProductsRes> highestScore = new ArrayList<>();
//
//        try{
//            if(store == null){
//                return new GetStoreProdRes(401, "스토어 이름 혹은 스토어 URL을 입력하지 않았습니다.", myStore, mostReviewed, highestScore);
//            }
//            // 내 스토어 기본 정보
//            myStore = storeService.selectStore(store);
//
//            // 경쟁 상품 비교 정보
//            //mostReviewed = productService.getProductsByReview(store);
//            //highestScore = productService.getProductsByReviewScore(store);
//
//            return new GetStoreProdRes(200, "내 스토어 분석 정보 반환에 성공하였습니다.", myStore, mostReviewed, highestScore);
//        } catch (Exception e){
//            return new GetStoreProdRes(400, "내 스토어 분석 정보 반환에 실패하였습니다.", myStore, mostReviewed, highestScore);
//        }
//    }

    /** 모델 서버에게 내 스토어 정보 분석 대시보드 요청 **/
    private String analyzeStore(GetProductsRes product, ArrayList<AllStoreProd> all) throws IOException, InterruptedException{
        String uri = "http://localhost:8080/analyze/store";

        // Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", product);
        body.add("join", all);

        RestTemplate restTemplate = new RestTemplate();

        // HttpEntity
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        HttpEntity<MultipartFile> response = restTemplate.getForEntity(uri, MultipartFile.class, request);

        String url = dashboardService.postDashboard(response.getBody());

        // String으로 받았다면 Response Parsing
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        UserDto dto = objectMapper.readValue(response.getBody(), UserDto.class);
         */

        return url;
    }

    /** 리뷰 많은 순으로 정렬된 경쟁사 분석 요청 **/
    private static GetProductsRes analyzeWithReview(GetProductsRes products){
        String uri = "http://localhost:8080/analyze/review";

        // Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Body
        MultiValueMap<String, GetProductsRes> body = new LinkedMultiValueMap<>();
        body.add("products", products);

        RestTemplate restTemplate = new RestTemplate();

        // HttpEntity
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        HttpEntity<Object> response = restTemplate.getForEntity(uri, Object.class, request);

        // String으로 받았다면 Response Parsing
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        UserDto dto = objectMapper.readValue(response.getBody(), UserDto.class);
         */

        return new GetProductsRes();
    }

    /** 리뷰 평점 높은 순으로 정렬된 경쟁사 분석 요청 **/
    private static GetProductsRes analyzeWithScore(GetProductsRes products) throws IOException, InterruptedException {
        String uri = "http://localhost:8080/analyze/score";

        // Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Body
        MultiValueMap<String, GetProductsRes> body = new LinkedMultiValueMap<>();
        body.add("products", products);

        RestTemplate restTemplate = new RestTemplate();

        // HttpEntity
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        HttpEntity<Object> response = restTemplate.getForEntity(uri, Object.class, request);

        // String으로 받았다면 Response Parsing
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        UserDto dto = objectMapper.readValue(response.getBody(), UserDto.class);
         */

        return new GetProductsRes();
    }
}
