package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.ugahaeju.server.dto.PostStoresReq;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static com.ugahaeju.server.utils.BigQuery.BigQueryAuthentication.getBigQuery;

@Component
@RequestMapping
public class StoreDao {
    /** Store 테이블에 데이터 삽입 **/
    public boolean insertStores(List<PostStoresReq> postStoresReq) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 삭제
            String query = "TRUNCATE TABLE STOREDB.Store;\n";

            // (업데이트된) 데이터 삽입
            query += "INSERT STOREDB.Store (store_id, store_url, store_name, category1, category2, category3)\n"
                    + "VALUES";

            // 상품 정보를 넣을 insert문
            for (PostStoresReq postStoreReq : postStoresReq) {
                query +=
                        String.format(
                                "('%s', '%s', '%s', '%s', '%s', '%s'),",
                                postStoreReq.store_id,
                                postStoreReq.storeURL,
                                postStoreReq.store_name,
                                postStoreReq.category1,
                                postStoreReq.category2,
                                postStoreReq.category3
                                /*
                                postStoreReq.grade,
                                postStoreReq.sales_amount,
                                postStoreReq.sales_price,
                                postStoreReq.good_service
                                 */
                        );
            }

            query = query.substring(0, query.length()-1);
            query += ";";
            System.out.println(query);

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            // 결과
            result.iterateAll().forEach(rows -> rows.forEach(row -> System.out.println(row.getValue())));
            System.out.println("Table is updated successfully using DML");
            return true;
        } catch (Exception e){
            System.out.println("Table cannot be updated successfully using DML");
            return false;
        }
    }
}
