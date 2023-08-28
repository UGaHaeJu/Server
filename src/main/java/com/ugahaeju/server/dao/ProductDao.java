package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.*;
import com.ugahaeju.server.dto.GetProductsRes;
import com.ugahaeju.server.dto.PostProductsReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
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

    /** Product 테이블에 데이터 INSERT **/
    public boolean insertProducts(List<PostProductsReq> postProductsReq) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 삭제
            String query = "TRUNCATE TABLE STOREDB.Product;\n";

            // (업데이트된) 데이터 삽입
            query += "INSERT STOREDB.Product (product_id, product_url, product_name, price, delivery_price, product_amount, " +
                    "review, review_score, heart, register_date, store_id, store_url)\n"
                    + "VALUES";

            // 상품 정보를 넣을 insert문
            for (PostProductsReq postProductReq : postProductsReq) {
                query +=
                        String.format(
                                "(%d, '%s', '%s', %d, %d, %d, %d, %f, %f, '%s', '%s', '%s'),",
                                postProductReq.product_id,
                                postProductReq.productURl,
                                postProductReq.product_name,
                                postProductReq.price,
                                postProductReq.delivery_price,
                                postProductReq.product_amount,
                                postProductReq.review,
                                postProductReq.review_score,
                                postProductReq.heart,
                                postProductReq.registerdate,
                                postProductReq.store_id,
                                postProductReq.storeURL
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
            e.printStackTrace();
            System.out.println("Table cannot be updated successfully using DML");
            return false;
        }
    }

    /** Product 테이블에서 순서대로 데이터 SELECT **/
    public ArrayList<GetProductsRes> selectProducts() throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 조회
            String query = "SELECT * FROM STOREDB.Product";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            // 개별 항목
            for (FieldValueList fieldValues : result.iterateAll()) {
                GetProductsRes res = new GetProductsRes(
                        fieldValues.get("product_id").getLongValue(),
                        fieldValues.get("product_url").getStringValue(),
                        fieldValues.get("product_name").getStringValue(),
                        fieldValues.get("price").getNumericValue().intValue(),
                        fieldValues.get("delivery_price").getNumericValue().intValue(),
                        fieldValues.get("product_amount").getNumericValue().intValue(),
                        fieldValues.get("review").getNumericValue().intValue(),
                        fieldValues.get("review_score").getNumericValue().floatValue(),
                        fieldValues.get("heart").getNumericValue().floatValue(),
                        fieldValues.get("register_date").getStringValue(),
                        fieldValues.get("store_id").getStringValue(),
                        fieldValues.get("store_url").getStringValue()
                );
                products.add(res);
            }
            System.out.println("SELECT query is done successfully");
            return products;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return products;
        }
    }

    /** Product 테이블에서 리뷰 많은 순으로 데이터 SELECT **/
    public ArrayList<GetProductsRes> selectProductsByReview(String store_id) throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 리뷰 많은 순으로 조회
            String query = "SELECT * FROM STOREDB.Product ORDER BY review DESC;";
            //+ " WHERE store_id NOT IN(" + store_id + ") ORDER BY review DESC;";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            // 개별 항목
            for (FieldValueList fieldValues : result.iterateAll()) {
                GetProductsRes res = new GetProductsRes(
                        fieldValues.get("product_id").getLongValue(),
                        fieldValues.get("product_url").getStringValue(),
                        fieldValues.get("product_name").getStringValue(),
                        fieldValues.get("price").getNumericValue().intValue(),
                        fieldValues.get("delivery_price").getNumericValue().intValue(),
                        fieldValues.get("product_amount").getNumericValue().intValue(),
                        fieldValues.get("review").getNumericValue().intValue(),
                        fieldValues.get("review_score").getNumericValue().floatValue(),
                        fieldValues.get("heart").getNumericValue().floatValue(),
                        fieldValues.get("register_date").getStringValue(),
                        fieldValues.get("store_id").getStringValue(),
                        fieldValues.get("store_url").getStringValue()
                );
                products.add(res);
            }
            System.out.println("SELECT query is done successfully");
            return products;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return products;
        }
    }

    /** Product 테이블에서 리뷰 평점 높은 순으로 데이터 SELECT **/
    public ArrayList<GetProductsRes> selectProductsByReviewScore(String store_id) throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 리뷰 평점 높은 순으로 조회
            String query = "SELECT * FROM STOREDB.Product ORDER BY review_score DESC;";
                    // + "WHERE store_id NOT IN(" + store_id + ") ORDER BY review_score DESC;";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            // 개별 항목
            for (FieldValueList fieldValues : result.iterateAll()) {
                GetProductsRes res = new GetProductsRes(
                        fieldValues.get("product_id").getLongValue(),
                        fieldValues.get("product_url").getStringValue(),
                        fieldValues.get("product_name").getStringValue(),
                        fieldValues.get("price").getNumericValue().intValue(),
                        fieldValues.get("delivery_price").getNumericValue().intValue(),
                        fieldValues.get("product_amount").getNumericValue().intValue(),
                        fieldValues.get("review").getNumericValue().intValue(),
                        fieldValues.get("review_score").getNumericValue().floatValue(),
                        fieldValues.get("heart").getNumericValue().floatValue(),
                        fieldValues.get("register_date").getStringValue(),
                        fieldValues.get("store_id").getStringValue(),
                        fieldValues.get("store_url").getStringValue()
                );
                products.add(res);
            }
            System.out.println("SELECT query is done successfully");
            return products;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return products;
        }
    }
}
