package com.indah.sandboxingserver.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"headers", "content"})
public class GetTableResponse {
    private final String[] headers;

    private final List<String> contents;

    @JsonCreator
    public GetTableResponse(@JsonProperty("headers") String[] headers, @JsonProperty("content")  List<String> contents) {
        this.headers = headers;
        this.contents = contents;
    }

    @JsonProperty("headers")
    public String[] getHeaders() {
        return headers;
    }

    @JsonProperty("contents")
    public  List<String> getContents() {
        return contents;
    }
}
