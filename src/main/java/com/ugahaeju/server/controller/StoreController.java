package com.ugahaeju.server.controller;

import com.ugahaeju.server.dto.*;
import com.ugahaeju.server.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

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

    /** <내 스토어 분석> 기능을 위한 정보 요청 **/
    @RequestMapping(value = "/analyze")
    public GetMyStoreRes analyzeMyStore(@RequestParam(value = "store") String store){
        MyStore myStore = new MyStore();
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<Competitors> competitors = new ArrayList<>();

        try{
            if(store == null){
                return new GetMyStoreRes(401, "스토어 이름 혹은 스토어 URL을 입력하지 않았습니다.", myStore, keywords, competitors);
            }
            // 내 스토어 기본 정보
            myStore = storeService.selectStore(store);

            return new GetMyStoreRes(200, "내 스토어 분석 정보 반환에 성공하였습니다.", myStore, keywords, competitors);
        } catch (Exception e){
            return new GetMyStoreRes(400, "내 스토어 분석 정보 반환에 실패하였습니다.", myStore, keywords, competitors);
        }
    }
}
