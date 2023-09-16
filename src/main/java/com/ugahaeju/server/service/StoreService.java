package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.StoreDao;
import com.ugahaeju.server.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;
    /** Store 데이터 저장 **/

    public boolean insertStores(List<StoreDto> storeDtos) throws IOException, InterruptedException {
        boolean isSuccess = storeDao.insertStores(storeDtos);
        if(isSuccess){
            return true;
        } else {
            return false;
        }
    }

    /** <내 스토어 분석> 기능을 위한 스토어 기본 정보 검색 **/
    public StoreDto selectStore(String store) throws IOException, InterruptedException {
        // 내 스토어 기본 정보
        StoreDto myStore = new StoreDto();
        myStore = storeDao.selectStoreByUrl(store);

//        if(store.substring(0, 5).equals("http")){
//            store_id = storeDao.selectStoreIdByURL(store);
//            myStore = storeDao.selectStoreByUrl(store);
//        } else {
//            store_id = storeDao.selectStoreIdByName(store);
//            myStore = storeDao.selectStoreByName(store);
//        }

        return myStore;
    }
}
