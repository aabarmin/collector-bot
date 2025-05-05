package dev.abarmin.telegram.collector.handler.command.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentItemContext {
    private Integer collectionId;
    private Integer itemId;
}
