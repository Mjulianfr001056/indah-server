package com.indah.sandboxingserver.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"completedMessage", "entity"})
public class ServerResponse {
    public static final String COMPLETED = "Spark processing completed";

    private final Object entity;

    @JsonCreator
    public ServerResponse(@JsonProperty("entity") Object entity) {
        this.entity = entity;
    }

    @JsonProperty("completedMessage")
    public String getCompletedMessage() {
        return COMPLETED;
    }

    @JsonProperty("entity")
    public Object getEntity() {
        return entity;
    }
}

