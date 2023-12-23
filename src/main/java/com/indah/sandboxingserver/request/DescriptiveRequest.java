package com.indah.sandboxingserver.request;

import lombok.Data;

import java.util.List;

@Data
public class DescriptiveRequest {
    String tableId;
    List<String> columnNames;
    List<String> descriptiveMethods;
}
