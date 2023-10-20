package io.hotcloud.service.cluster.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.unit.DataSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeImage implements Serializable {
    private String cluster;
    private String node;
    @Builder.Default
    private List<String> names = new ArrayList<>();
    private long sizeBytes;

    public String getSize() {
        long megabytes = DataSize.ofBytes(this.sizeBytes).toMegabytes();
        long kilobytes = DataSize.ofBytes(this.sizeBytes).toKilobytes();
        if (megabytes < 1) {
            return kilobytes + "(KB)";
        }
        return megabytes + "(MB)";
    }
}
