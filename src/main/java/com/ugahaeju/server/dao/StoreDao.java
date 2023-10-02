package com.ugahaeju.server.dao;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.ugahaeju.server.dto.StoreDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static com.ugahaeju.server.utils.BigQuery.BigQueryAuthentication.getBigQuery;

@Component
@RequestMapping
public class StoreDao {
    /** Store 테이블에 데이터 저장 **/
    public boolean insertStores(List<StoreDto> storeDto) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();

            // 테이블의 전체 데이터 삭제
            String query = "TRUNCATE TABLE STOREDB.Store;\n";

            // (업데이트된) 데이터 삽입
            query += "INSERT STOREDB.Store (store_id, store_url, store_name, rank, category1, category2, category3)\n"
                    + "VALUES";

            // 상품 정보를 넣을 insert문
            for (StoreDto postStoreReq : storeDto) {
                query +=
                        String.format(
                                "('%s', '%s', '%s', %d, '%s', '%s', '%s'),",
                                postStoreReq.store_id,
                                postStoreReq.storeURL,
                                postStoreReq.store_name,
                                postStoreReq.rank,
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

    /** Store 테이블에서 store_url로 store_id 검색 **/
    public String selectStoreIdByURL(String store_url) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();

            // 스토어 이름으로 스토어 아이디 검색 쿼리
            String query = "SELECT store_id FROM STOREDB.Store WHERE store_url = " + store_url + ";";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            String store_id = "";
            for (FieldValueList fieldValues : result.iterateAll()) {
                store_id = fieldValues.get("store_id").getStringValue();
            }

            // 결과
            System.out.println("SELECT query is done successfully");
            return store_id;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return null;
        }
    }

    /** Store 테이블에서 store_name으로 store_id 검색 **/
    public String selectStoreIdByName(String store_name) throws IOException, InterruptedException {
        try {
            BigQuery bigQuery = getBigQuery();

            // 스토어 이름으로 스토어 아이디 검색 쿼리
            String query = "SELECT store_id FROM STOREDB.Store WHERE store_name = " + store_name + ";";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            String store_id = "";
            for (FieldValueList fieldValues : result.iterateAll()) {
                store_id = fieldValues.get("store_id").getStringValue();
            }

            // 결과
            System.out.println("SELECT query is done successfully");
            return store_id;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return null;
        }
    }

    /** Store 테이블에서 스토어 이름으로 스토어 정보 검색 **/
    public StoreDto selectStoreByName(String store_name) throws IOException, InterruptedException {
        StoreDto myStore = new StoreDto();

        try {
            BigQuery bigQuery = getBigQuery();

            // 스토어 이름으로 스토어 아이디 검색 쿼리
            String query = "SELECT store_name, rank, category1 FROM STOREDB.Store WHERE store_name = " + store_name + ";";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            for (FieldValueList fieldValues : result.iterateAll()) {
                myStore.store_name = fieldValues.get("store_name").getStringValue();
                myStore.store_id = fieldValues.get("store_id").getStringValue();
                myStore.storeURL = fieldValues.get("store_url").getStringValue();
                myStore.rank = fieldValues.get("rank").getNumericValue().intValue();
                myStore.category1 = fieldValues.get("category1").getStringValue();
                myStore.category2 = fieldValues.get("category2").getStringValue();
                myStore.category3 = fieldValues.get("category3").getStringValue();
            }

            // 결과
            System.out.println("SELECT query is done successfully");
            return myStore;
        } catch (Exception e){
            System.out.println("SELECT query cannot be done successfully");
            return myStore;
        }
    }

    /** Store 테이블에서 스토어 URL로 스토어 정보 검색 **/
    public StoreDto selectStoreByUrl(String store_url) throws IOException, InterruptedException {
        StoreDto myStore = new StoreDto();

        try {
            BigQuery bigQuery = getBigQuery();

            // 스토어 이름으로 스토어 아이디 검색 쿼리
            String query = "SELECT * FROM STOREDB.Store WHERE store_url = '" + store_url + "';";

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            TableResult result = bigQuery.query(queryConfig);

            for (FieldValueList fieldValues : result.iterateAll()) {
                myStore.store_name = fieldValues.get("store_name").getStringValue();
                myStore.store_id = fieldValues.get("store_id").getStringValue();
                myStore.storeURL = fieldValues.get("store_url").getStringValue();
                myStore.rank = fieldValues.get("rank").getNumericValue().intValue();
                myStore.category1 = fieldValues.get("category1").getStringValue();
                myStore.category2 = fieldValues.get("category2").getStringValue();
                myStore.category3 = fieldValues.get("category3").getStringValue();
            }

            // 결과
            System.out.println("SELECT query is done successfully");
            return myStore;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("SELECT query cannot be done successfully");
            return myStore;
        }
    }
}


