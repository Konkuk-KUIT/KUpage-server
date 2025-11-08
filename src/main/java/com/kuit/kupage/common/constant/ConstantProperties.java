package com.kuit.kupage.common.constant;

import com.kuit.kupage.domain.common.Batch;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "const")
@Component
public class ConstantProperties {
    private String currentBatch;

    public Batch getCurrentBatch() {
        return Batch.valueOf(currentBatch);
    }

    public void setCurrentBatch(String currentBatch) {
        this.currentBatch = currentBatch;
    }

}
