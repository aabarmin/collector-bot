package dev.abarmin.telegram.collector.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("COLLECTION_SHARE_CODES")
public class CollectionShareCodeEntity {

    @Id
    @Column("ID")
    private Integer id;

    @Column("COLLECTION_ID")
    private int collectionId;

    @Column("USER_ID")
    private int userId;

    @Column("CODE")
    private String code;

    @Column("ACCESS_LEVEL")
    private AccessPermissions accessLevel;

    @Column("ACCESS_LINK")
    private String accessLink;

    @Column("IS_VALID")
    private boolean isValid;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("VALID_BEFORE")
    private Instant validBefore;

    public boolean isValid(Instant now) {
        if (!isValid) {
            return false;
        }
        final boolean validByTime = validBefore.isAfter(now);
        if (!validByTime) {
            return false;
        }
        return accessLevel != AccessPermissions.NONE;
    }
}
