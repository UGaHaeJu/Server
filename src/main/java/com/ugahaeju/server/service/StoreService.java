package com.ugahaeju.server.service;

import com.ugahaeju.server.dao.StoreDao;
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
}
