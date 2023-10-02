package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

import static com.ugahaeju.server.utils.BigQuery.BigQueryAuthentication.getBigQuery;

@Component
@RequiredArgsConstructor
public class DashboardDao {
    /** Dashboard 테이블에 데이터 INSERT **/
    public void insertDashboard(String url, long product_id) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();
            LocalDate now = LocalDate.now();
            System.out.println(url);
            System.out.println(now);

            // 대시보드 데이터 삽입
            String query = "INSERT STOREDB.Dashboard (product_id, img_url, date)\n"
                    + "VALUES(" + product_id + ", '" + url + "', '" + now + "');";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            System.out.println("Table is updated successfully using DML");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Table cannot be updated successfully using DML");
        }
    }
}
