package com.despegar.demo_back.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobData {
    private int id;
    private Status status;
    private String title;
}
