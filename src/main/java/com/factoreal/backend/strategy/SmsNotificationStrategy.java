package com.factoreal.backend.strategy;

import com.factoreal.backend.entity.Worker;
import com.factoreal.backend.entity.enums.RiskLevel;
import com.factoreal.backend.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Optional;

@Slf4j
@Component("SMS")
@RequiredArgsConstructor
public class SmsNotificationStrategy implements NotificationStrategy {
    // AWS SNS 서비스 사용을 위한 객체
    private final SnsClient snsClient;
    private final WorkerRepository workerRepository;
    @Override
    public void send(String userId, String message) {
        Optional<Worker> workerOptional = workerRepository.findById(userId);
        if  (workerOptional.isEmpty()) {
            log.error("❌해당 아이디를 가진 직원이 없습니다.{}", userId);
            return;
        }
        try{
            PublishRequest publishRequest = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(workerOptional.get().getPhoneNumber()) // 형식 (국가번호)전화번호 => +82 10-1234-1234
                    .build();

            PublishResponse publishResponse = snsClient.publish(publishRequest);
            log.info("✅ SMS Publish Response: {}", publishResponse);
        }catch (Exception e){
            log.error("❌ SMS Publish Exception: {}", e.getMessage());
        }
    }

    @Override
    public RiskLevel getSupportedLevel() {
        return RiskLevel.DANGER;
    }
}
