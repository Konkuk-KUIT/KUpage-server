package com.kuit.kupage.common.constant;

import com.kuit.kupage.domain.common.Batch;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@ConfigurationProperties(prefix = "const")
@Component
public class ConstantProperties {
    private String currentBatch;

    public Batch getCurrentBatch() {
        return Batch.valueOf(currentBatch);
    }

}
