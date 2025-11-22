package com.kuit.kupage.common.constant;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.teamMatch.ApplicantStatus;
import com.kuit.kupage.exception.KupageException;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.kuit.kupage.common.response.ResponseCode.NOT_TEAM_MATCH_APPLY_PERIOD;
import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.ROUND1_APPLYING;
import static com.kuit.kupage.domain.teamMatch.ApplicantStatus.ROUND2_APPLYING;

@Setter
@ConfigurationProperties(prefix = "const")
@Component
public class ConstantProperties {
    private String currentBatch;
    private LocalDateTime firstRoundResultTime;
    private LocalDateTime secondRoundResultTime;

    public Batch getCurrentBatch() {
        return Batch.valueOf(currentBatch);
    }

    public ApplicantStatus getApplicantStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(firstRoundResultTime)) {
            return ROUND1_APPLYING;
        }
        if (now.isAfter(firstRoundResultTime) && now.isBefore(secondRoundResultTime)) {
            return ROUND2_APPLYING;
        }
        throw new KupageException(NOT_TEAM_MATCH_APPLY_PERIOD);
    }

}
