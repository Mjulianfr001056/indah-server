package com.indah.sandboxingserver.request;

import lombok.Data;

import java.util.List;

@Data
public class ColumnRequest {
    String tableName;
    List<String> columnNames;
}
