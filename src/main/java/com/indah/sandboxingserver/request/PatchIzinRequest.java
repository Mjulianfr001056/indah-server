package com.indah.sandboxingserver.request;

import lombok.Data;

@Data
public class PatchIzinRequest {
    String requestId;
    String newStatus;
}
