package br.com.arcasoftware.sbs.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class StatusProcessamentoNFeService {
    private static final String REGION = "us-east-1";
    private static final String TABLE_NAME = "nfe_processing_status";

    AmazonDynamoDB amazonDynamoDB;
    public  StatusProcessamentoNFeService() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
    }

    public List getAllStatus(String sequencer){
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(":v_pk", new AttributeValue(sequencer));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(TABLE_NAME)
                .withKeyConditionExpression("id = :v_pk")
                .withExpressionAttributeValues(map);
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);

        return queryResult.getItems();
    }

    public void markAsProcessing(String sequencer){
        updateStatusProcessamento(sequencer, "PROCESSING", "Arquivo sendo Processado ");
    }

    public void updateSucessoProcessamento(String sequencer){
        updateStatusProcessamento(sequencer, "SUCCESS", "Arquivo Processado corretamente");
    }

    public void updateErroProcessamento(String sequencer, String message){
        updateStatusProcessamento(sequencer, "FAILURE", message);
    }

    private void updateStatusProcessamento(String sequencer, String status, String message){
        AttributeValueUpdate valueStatus = new AttributeValueUpdate().withValue(new AttributeValue(status));
        AttributeValueUpdate valueMessage = new AttributeValueUpdate().withValue(new AttributeValue(message));

        HashMap<String, AttributeValueUpdate> updateMap = new HashMap<>();
        updateMap.put("status", valueStatus);
        updateMap.put("message", valueMessage);

        HashMap<String, AttributeValue> updateKey = new HashMap<>();
        updateKey.put("id", new AttributeValue(sequencer));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(updateKey)
                .withAttributeUpdates(updateMap);

        this.amazonDynamoDB.updateItem(updateItemRequest);
    }

}
