package com.ugahaeju.server.service;

import com.google.cloud.storage.Storage;
import com.ugahaeju.server.dao.DashboardDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DashboardService {
    final private DashboardDao dashboardDao;
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}") // application.yml에 써둔 bucket 이름
    private String bucketName;

    /** Dashboard INSERT API **/
    public String postDashboard(String url, long product_id) throws IOException, InterruptedException {
//        String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름
//        String ext = file.getContentType(); // 파일의 형식 ex) JPG
//
//        System.out.println(ext);
//
//        // Cloud에 이미지 업로드
//        BlobInfo blobInfo = storage.create(
//                BlobInfo.newBuilder(bucketName, uuid)
//                        .setContentType(ext)
//                        .build(),
//                file.getInputStream()
//        );
//
//        // https://storage.googleapis.com/{Bucket Name}/{파일경로}/{파일명}
//        String url = "https://storage.googleapis.com/" + bucketName + "/" + uuid;
        dashboardDao.insertDashboard(url, product_id);

        return url;
    }
}
