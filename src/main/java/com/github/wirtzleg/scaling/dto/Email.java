package com.github.wirtzleg.scaling.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("emails")
public class Email {
    @Id
    private Long id;

    private Long recipientId;
    private EmailType type;
    private EmailStatus status;
}
