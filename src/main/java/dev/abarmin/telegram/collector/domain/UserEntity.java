package dev.abarmin.telegram.collector.domain;

import dev.abarmin.telegram.collector.service.ChatState;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("USERS")
public class UserEntity {

    @Id
    @Column("ID")
    private Integer id;

    @Column("CHAT_ID")
    private long chatId;

    @Column("USERNAME")
    private String username;

    @Column("STATE")
    private ChatState state;

    @Column("CONTEXT")
    private String context;
}
