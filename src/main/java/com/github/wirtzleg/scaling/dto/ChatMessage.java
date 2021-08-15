package com.github.wirtzleg.scaling.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("chat_messages")
public class ChatMessage implements Serializable {
    @Id
    private Long id;

    private String chatId;
    private Long authorId;
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();
}
