package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.*;
import com.ugahaeju.server.dto.AllStoreProd;
import com.ugahaeju.server.dto.Average;
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

    /** 내 스토어의 Products 데이터 SELECT **/
    public ArrayList<GetProductsRes> selectMyProducts(String url) throws IOException, InterruptedException {
        ArrayList<GetProductsRes> products = new ArrayList<>();
        try {
            BigQuery bigQuery = getBigQuery();

            String query = "SELECT * FROM STOREDB.Product WHERE store_url = '"  + url +"'";

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

    /** 내 스토어의 Product 데이터 SELECT **/
    public GetProductsRes selectMyProduct(long id) throws IOException, InterruptedException {
        GetProductsRes product = new GetProductsRes();
        try {
            BigQuery bigQuery = getBigQuery();

            String query = "SELECT * FROM STOREDB.Product WHERE product_id = " + id;

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
                product = res;
            }
            System.out.println("SELECT query is done successfully");
            return product;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return product;
        }
    }

    public ArrayList<AllStoreProd> selectProductNStore() throws IOException, InterruptedException {
        ArrayList<AllStoreProd> all = new ArrayList<>();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 조회
            String query = "SELECT * FROM STOREDB.Product AS p " +
                    "LEFT JOIN STOREDB.Store AS s " +
                    "ON p.store_id = s.store_id";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            // 개별 항목
            for (FieldValueList fieldValues : result.iterateAll()) {
                AllStoreProd res = new AllStoreProd(
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
                        fieldValues.get("store_url").getStringValue(),
                        fieldValues.get("store_name").getStringValue(),
                        fieldValues.get("category1").getStringValue(),
                        fieldValues.get("category2").getStringValue(),
                        fieldValues.get("category3").getStringValue()
                        );
                all.add(res);
            }
            System.out.println("SELECT query is done successfully");
            return all;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return all;
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

    /** Product 테이블에서 리뷰 평점 높은 순으로 수치 평균 분석 **/
    public Average selectProductsByReviewScore() throws IOException, InterruptedException {
        //ArrayList<GetProductsRes> products = new ArrayList<>();
        String store_ids = "";
        Average average = new Average();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 top 80 데이터 리뷰 평점 높은 순으로 조회
            String query = "SELECT * FROM STOREDB.Product ORDER BY review_score DESC limit 80;";
                    // + "WHERE store_id NOT IN(" + store_id + ") ORDER BY review_score DESC;";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            int price = 0;
            int delivery_price = 0;
            int product_amount = 0;
            int review = 0;
            int review_score = 0;
            int heart = 0;
            for (FieldValueList fieldValues : result.iterateAll()) {
                price += fieldValues.get("price").getNumericValue().intValue();
                delivery_price += fieldValues.get("delivery_price").getNumericValue().intValue();
                product_amount += fieldValues.get("product_amount").getNumericValue().intValue();
                review += fieldValues.get("review").getNumericValue().intValue();
                review_score += fieldValues.get("review_score").getNumericValue().floatValue();
                heart += fieldValues.get("heart").getNumericValue().floatValue();
//                GetProductsRes res = new GetProductsRes(
//                        fieldValues.get("product_id").getLongValue(),
//                        fieldValues.get("product_url").getStringValue(),
//                        fieldValues.get("product_name").getStringValue(),
//                        fieldValues.get("price").getNumericValue().intValue(),
//                        fieldValues.get("delivery_price").getNumericValue().intValue(),
//                        fieldValues.get("product_amount").getNumericValue().intValue(),
//                        fieldValues.get("review").getNumericValue().intValue(),
//                        fieldValues.get("review_score").getNumericValue().floatValue(),
//                        fieldValues.get("heart").getNumericValue().floatValue(),
//                        fieldValues.get("register_date").getStringValue(),
//                        fieldValues.get("store_id").getStringValue(),
//                        fieldValues.get("store_url").getStringValue()
//                );
                store_ids += "'" + fieldValues.get("store_id").getStringValue() + "', ";
                //products.add(res);
            }

            String query2 = "SELECT rank FROM STOREDB.Store WHERE store_id IN(" + store_ids.substring(0, store_ids.length()-2) + ");";

            queryConfig = QueryJobConfiguration.newBuilder(query2).build();
            result = bigQuery.query(queryConfig);

            int rank = 0;
            for (FieldValueList fieldValues : result.iterateAll()) {
                rank += fieldValues.get("rank").getNumericValue().intValue();
            }

            average.price = price / 80;
            average.delivery_price = delivery_price / 80;
            average.product_amount = product_amount / 80;
            average.review = review / 80;
            average.review_score = review_score / 80;
            average.heart = heart / 80;
            average.rank = rank / 80;

            System.out.println("SELECT query is done successfully");
            return average;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("SELECT query cannot be done successfully");
            return average;
        }
    }

    /** Product 테이블에서 내 상품 데이터 평균 수치 분석 **/
    public Average selectMyProductsAverage(String url) throws IOException, InterruptedException {
        Average average = new Average();
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 내 상품 조회
            String query = "SELECT * FROM STOREDB.Product WHERE store_url ='" + url + "';";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            int price = 0;
            int delivery_price = 0;
            int product_amount = 0;
            int review = 0;
            int review_score = 0;
            int heart = 0;
            int cnt = 0;
            for (FieldValueList fieldValues : result.iterateAll()) {
                cnt++;
                price += fieldValues.get("price").getNumericValue().intValue();
                delivery_price += fieldValues.get("delivery_price").getNumericValue().intValue();
                product_amount += fieldValues.get("product_amount").getNumericValue().intValue();
                review += fieldValues.get("review").getNumericValue().intValue();
                review_score += fieldValues.get("review_score").getNumericValue().floatValue();
                heart += fieldValues.get("heart").getNumericValue().floatValue();
            }

            String query2 = "SELECT * FROM STOREDB.Store WHERE store_url ='"+ url +"';";

            queryConfig = QueryJobConfiguration.newBuilder(query2).build();
            result = bigQuery.query(queryConfig);

            int rank = 0;
            for (FieldValueList fieldValues : result.iterateAll()) {
                rank += fieldValues.get("rank").getNumericValue().intValue();
            }

            average.price = price / cnt;
            average.delivery_price = delivery_price / cnt;
            average.product_amount = product_amount / cnt;
            average.review = review / cnt;
            average.review_score = review_score / cnt;
            average.heart = heart / cnt;
            average.rank = rank / cnt;

            System.out.println("SELECT query is done successfully");
            return average;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("SELECT query cannot be done successfully");
            return average;
        }
    }
}
