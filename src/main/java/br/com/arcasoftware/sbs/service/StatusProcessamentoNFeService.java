package br.com.arcasoftware.sbs.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.Select;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class StatusProcessamentoNFeService {
    private static final String REGION = "us-east-1";
    private static final String TABLE_NAME = "nfe_processing";

    AmazonDynamoDB amazonDynamoDB;
    DynamoDB dynamoDB;

    public StatusProcessamentoNFeService() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
        dynamoDB = new DynamoDB(this.amazonDynamoDB);
    }

    public int getEmProcessamento(String identityId) {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :identityId")
                .withFilterExpression("statusDesc in (:statusDesc1, :statusDesc2)")
                .withValueMap(new ValueMap()
                        .withString(":identityId", identityId)
                        .withString(":statusDesc1", "CREATED")
                        .withString(":statusDesc2", "PROCESSING")
                )
                .withSelect(Select.COUNT);

        Table table = dynamoDB.getTable(TABLE_NAME);

        ItemCollection<QueryOutcome> itemCollection = table.query(spec);

        Iterator<Item> iterator = itemCollection.iterator();
        while (iterator.hasNext()) {
            iterator.next().toJSONPretty();
        }
        return itemCollection.getAccumulatedItemCount();
    }

    public void markAsProcessing(String identityId, String sequencer) {
        updateStatusProcessamento(identityId, sequencer, "PROCESSING", "Arquivo sendo Processado ");
    }

    public void updateSucessoProcessamento(String identityId, String sequencer) {
        updateStatusProcessamento(identityId, sequencer, "SUCCESS", "Arquivo Processado corretamente");
    }

    public void updateErroProcessamento(String identityId, String sequencer, String message) {
        updateStatusProcessamento(identityId, sequencer, "FAILURE", message);
    }

    public void updateStatusProcessamento(String identityId, String sequencer, String status, String message) {
        Table table = dynamoDB.getTable(TABLE_NAME);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", identityId, "sequencer", sequencer)
                .withUpdateExpression("set statusDesc = :statusDesc, message = :message")
                .withValueMap(
                        new ValueMap()
                                .withString(":statusDesc", status)
                                .withString(":message", message))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        table.updateItem(updateItemSpec);
    }

    public void deleteStatusProcessamento(String identityId, String sequencer) {
        Table table = dynamoDB.getTable(TABLE_NAME);

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("id", identityId, "sequencer", sequencer);

        table.deleteItem(deleteItemSpec);
    }

}
