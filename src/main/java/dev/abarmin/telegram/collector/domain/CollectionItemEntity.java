package dev.abarmin.telegram.collector.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table("COLLECTION_ITEMS")
public class CollectionItemEntity {

    @Id
    @Column("ID")
    private Integer id;

    @Column("COLLECTION_ID")
    private int collectionId;

    @Column("NAME")
    private String name;

    @Column("IMAGE_FILE_ID")
    private String imageFileId;

    @Column("DELETED")
    private boolean deleted;

    @Builder.Default
    @Column("CREATED_AT")
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column("UPDATED_AT")
    private Instant updatedAt = Instant.now();

    public boolean hasPhoto() {
        return StringUtils.isNoneEmpty(imageFileId);
    }

}
