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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController implements Serializable {
    private final StoreService storeService;
    private final ProductService productService;
    private final DashboardService dashboardService;
    private HttpEntity response;

    /** store table 정보 저장 **/
    @RequestMapping(value = "/stores")
    public PostStoresRes insertStores(@RequestBody List<StoreDto> storeDto){
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

            if(storeService.insertStores(storeDto)) {
                return new PostStoresRes(200, "상점 정보 저장에 성공하였습니다.");
            } else {
                return new PostStoresRes(400, "상점 정보 저장에 실패하였습니다.");
            }
        } catch (Exception e){
            return new PostStoresRes(400, "상점 정보 저장에 실패하였습니다.");
        }
    }

    @RequestMapping(value = "/store")
    public GetStoreRes getMyStoreInfo(@RequestParam(value = "url") String storeUrl){
        StoreDto myStore = new StoreDto();

        try{
            if(storeUrl == null){
                return new GetStoreRes(400, "내 스토어 조회에 실패하였습니다.", myStore);
            }

            // 내 스토어의 상품 정보
            myStore = storeService.selectStore(storeUrl);
            
            if(myStore.getStore_id().isEmpty()){
                return new GetStoreRes(400, "존재하지 않는 스토어 주소입니다.", myStore);
            }

            return new GetStoreRes(200, "내 스토어 조회에 성공하였습니다.", myStore);
        } catch (Exception e){
            return new GetStoreRes(400, "내 스토어 조회에 실패하였습니다.", myStore);
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

            if(myProducts.isEmpty()){
                return new GetStoreProdRes(400, "존재하지 않는 스토어 주소입니다.", myProducts);
            }

            return new GetStoreProdRes(200, "내 스토어의 상품 반환에 성공하였습니다.", myProducts);
        } catch (Exception e){
            return new GetStoreProdRes(400, "내 스토어의 상품 반환에 실패하였습니다.", myProducts);
        }
    }

    /** 경쟁 상품 추천 기능을 위한 상품 데이터 조회
     * @return**/
    @RequestMapping(value = "/recommend")
    public GetRecommedationsRes getRecommendations(@RequestParam(value = "product") long product){
         // public GetDashboardRes getProductDashBoard(@RequestParam(value = "product") long product, MultipartFile image){
            GetProductsRes myProduct = new GetProductsRes();
        ArrayList<GetProductsRes> recommendations = new ArrayList<>();
        String dashboardUrl = "";
        ArrayList<AllStoreProd> all = new ArrayList<>();
        try{
            if(product < 0){
                return new GetRecommedationsRes(401, "상품 정보가 올바르게 입력되지 않았습니다.", recommendations);
            }

            // 상품 정보
            myProduct = productService.getMyProduct(product);
            //System.out.println(myProduct.product_name);

            // 스토어 + 상품 조인 정보
            all = productService.getProductNStore();
            //System.out.println(all);

            recommendations = analyzeRecommendations(myProduct, all);

            return new GetRecommedationsRes(200, "추천 상품 조회에 성공하였습니다.", recommendations);
        } catch (Exception e){
            e.printStackTrace();
            return new GetRecommedationsRes(400, "추천 상품 조회에 실패하였습니다.", recommendations);
        }
    }

    /** 모델 서버에게 경쟁 추천 상품 분석 요청 **/
    private ArrayList<GetProductsRes> analyzeRecommendations(GetProductsRes product, ArrayList<AllStoreProd> all) throws IOException, InterruptedException{
        String uri = "http://35.208.123.6:5000/";

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
        HttpEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
        String[] res = response.getBody().toString().split("\"");
        System.out.println(response.getBody());

        ArrayList<GetProductsRes> recommendations = new ArrayList<>();
        if(res.length < 5){
            return recommendations;
        } else {
            return recommendations = productService.getRecommendedProducts(res);
        }
    }

    /** <내 스토어 분석> 기능을 위한 선별된 상품 분석 대시보드 조회
     * @return**/
    @RequestMapping(value = "/mystore/dashboard")
    public GetDashboardRes getProductDashBoard(@RequestParam(value = "product") long product){
        // public GetDashboardRes getProductDashBoard(@RequestParam(value = "product") long product, MultipartFile image){
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
            dashboardUrl = analyzeStore(myProduct, all);

            //String url = dashboardService.postDashboard(image, myProduct.product_id);

            return new GetDashboardRes(200, "추천 상품 조회에 성공하였습니다.", dashboardUrl);
        } catch (Exception e){
            e.printStackTrace();
            return new GetDashboardRes(400, "추천 상품 조회에 실패하였습니다.", dashboardUrl);
        }
    }

    /** 모델 서버에게 대시보드 요청 **/
    private String analyzeStore(GetProductsRes product, ArrayList<AllStoreProd> all) throws IOException, InterruptedException{
        String uri = "http://35.208.123.6:5000/chart";

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<String> response = restTemplate.postForEntity(uri, request, String.class);

        //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getBody());
        //MultipartFile multipartFile = new MockMultipartFile(String.valueOf(product.product_id), byteArrayInputStream.readAllBytes());
        //System.out.println();

        String url = dashboardService.postDashboard(response.getBody(), product.product_id);
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
