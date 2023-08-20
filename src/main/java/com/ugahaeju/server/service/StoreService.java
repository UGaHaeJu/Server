package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.StoreDao;
import com.ugahaeju.server.dto.MyStore;
import com.ugahaeju.server.dto.PostStoresReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;

    public boolean insertStores(List<PostStoresReq> postStoresReqs) throws IOException, InterruptedException {
        boolean isSuccess = storeDao.insertStores(postStoresReqs);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }

    /** <내 스토어 분석> 기능을 위한 스토어 기본 정보 검색 **/
    public MyStore selectStore(String store) throws IOException, InterruptedException {
        // 내 스토어 기본 정보
        MyStore myStore = new MyStore();
        if(store.substring(0, 5).equals("http")){
            myStore = storeDao.selectStoreByUrl(store);
        } else {
            //String store_id = storeDao.selectStoreId(store);
            myStore = storeDao.selectStoreByName(store);
        }

        return myStore;
    }
}
