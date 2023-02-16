package com.learnkafka.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FailureRecord {

    @Id
    private Integer id;
    private String topic;
    private Integer key;
    private String errorRecord;
    private Integer partition;
    private Long offsetValue;
    private String exception;
    private String status;
}
