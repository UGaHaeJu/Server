package com.ugahaeju.server.utils.BigQuery;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;

/*
    구글 빅쿼리
 */
public class BigQueryAuthentication {
    /*
        구글 credentials.json을 이용한 BigQuery 생성
        @return {BigQuery}
     */
    public static BigQuery getBigQuery(){
        try {
            // credentials.json 읽기
            File credentialsFile = ResourceUtils.getFile("hf016-394001-369b3ad6aa46.json");
            GoogleCredentials credentials;
            try (FileInputStream fileInputStream = new FileInputStream(credentialsFile)) {
                credentials = ServiceAccountCredentials.fromStream(fileInputStream);
            }

            //BigQuery 생성
            BigQuery bigQuery = BigQueryOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId("hf016-394001")
                    .build()
                    .getService();
            return bigQuery;
        } catch (Exception e){
            return null;
        }
    }
}
