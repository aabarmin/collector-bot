package dev.abarmin.telegram.collector.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessPermissions {
    OWNER("Владелец", true, true),
    READER("Читатель", false, true),
    FULL_ACCESS("Полный доступ", true, true),
    NONE("Нет доступа", false, false);

    private final String value;
    private final boolean canEdit;
    private final boolean canRead;
}
