package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.*;
import com.ugahaeju.server.model.PostProductsReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.ugahaeju.server.utils.BigQuery.BigQueryAuthentication.getBigQuery;

@Component
@RequiredArgsConstructor
public class ProductDao {
    /**데이터셋(테이블의 집합) 생성**/
    public void createDataset() {
        BigQuery bigQuery = getBigQuery();

        String datasetName = "STOREDB";
        DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).build();
        Dataset dataset = bigQuery.create(datasetInfo);
        System.out.println("Dataset: " + dataset.getDatasetId().getDataset());
    }

    /** Product 테이블에 데이터 삽입 **/
    public boolean insertProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();
            TableId tableId = TableId.of("STOREDB", "Store");

            // 테이블의 전체 데이터 삭제
            String query = "TRUNCATE TABLE STOREDB.Product;\n";

            // (업데이트된) 데이터 삽입
            query += "INSERT STOREDB.Product (product_id, store_id, product_name, star, review, heart, date, price, discount, point)\n"
                    + "VALUES";

            // 상품 정보를 넣을 insert문
            for (PostProductsReq postProductReq : postProductsReq) {
                query +=
                        String.format(
                                "(%d, '%s', '%s', %f, %d, %d, '%s', %d, %s, %d),",
                                postProductReq.product_id,
                                postProductReq.store_id,
                                postProductReq.product_name,
                                postProductReq.star,
                                postProductReq.review,
                                postProductReq.heart,
                                postProductReq.date,
                                postProductReq.price,
                                postProductReq.discount,
                                postProductReq.point);
            }

            query = query.substring(0, query.length()-1);
            query += ";";
            System.out.println(query);

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);
            // 결과
            result.iterateAll().forEach(rows -> rows.forEach(row -> System.out.println(row.getValue())));

            /*
                // 다른 방법
                JobId jobId = JobId.of(UUID.randomUUID().toString());
                Job job = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
                job = job.waitFor();

                if (job == null) {
                    throw new RuntimeException("쿼리가 존재하지 않습니다.");
                } else {
                    if (job.getStatus().getError() != null) {
                        String errorMessage =
                                job.getStatus().getError().getMessage() + "\n"
                                        + job.getStatus().getError().getReason() + "\n"
                                        + job.getStatus().getError().toString();
                        throw new RuntimeException(errorMessage);
                    } else {
                        TableResult tableResult = job.getQueryResults();
                        System.out.println(tableResult);
                    }
                }
             */
            System.out.println("Table is updated successfully using DML");
            return true;
        } catch (Exception e){
            System.out.println("Table cannot be updated successfully using DML");
            return false;
        }
    }
}
