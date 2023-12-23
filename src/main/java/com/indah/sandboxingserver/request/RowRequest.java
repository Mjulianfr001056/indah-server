package com.indah.sandboxingserver.request;

import lombok.Data;

import java.util.List;

@Data
public class RowRequest {
    String tableId;
    List<String> RowNames;
}
