package dev.abarmin.telegram.collector.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table("COLLECTIONS")
public class CollectionEntity {

    @Id
    @Column("ID")
    private Integer id;

    @Column("USER_ID")
    private int userId;

    @Column("NAME")
    private String name;

    @Column("DELETED")
    private boolean deleted;

    @Builder.Default
    @Column("CREATED_AT")
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column("UPDATED_AT")
    private Instant updatedAt = Instant.now();

}
