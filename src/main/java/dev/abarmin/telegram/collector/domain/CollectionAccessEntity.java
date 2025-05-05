package dev.abarmin.telegram.collector.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("COLLECTION_ACCESS")
public class CollectionAccessEntity {

    @Id
    @Column("ID")
    private Integer id;

    @Column("COLLECTION_ID")
    private int collectionId;

    @Column("OWNER_ID")
    private int ownerId;

    @Column("USER_ID")
    private int userId;

    @Column("PERMISSIONS")
    private AccessPermissions permissions;
}
