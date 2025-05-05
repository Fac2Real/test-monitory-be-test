package com.factoreal.backend.strategy;

import com.factoreal.backend.entity.Worker;
import com.factoreal.backend.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
class SmsNotificationStrategyTest {
    @Container
    static LocalStackContainer localstack = new LocalStackContainer("latest")
            .withServices(LocalStackContainer.Service.SNS);

    private SmsNotificationStrategy smsNotificationStrategy;
    private WorkerRepository workerRepository;
    private SnsClient snsClient;

    @BeforeEach
    void setUp() {
        // LocalStack SNS 클라이언트 생성
        snsClient = SnsClient.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SNS))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .region(Region.of(localstack.getRegion()))
                .build();

        // Mock Repository
        workerRepository = mock(WorkerRepository.class);

        // 전략 객체 생성
        smsNotificationStrategy = new SmsNotificationStrategy(snsClient, workerRepository);
    }

    @Test
    void send_sms_successfully() {
        // given
        Worker worker = new Worker();
        worker.setPhoneNumber("+821012341234"); // SNS 포맷
        when(workerRepository.findById("user123")).thenReturn(Optional.of(worker));

        // when
        smsNotificationStrategy.send("user123", "위험 알림: 가스 누출이 감지되었습니다.");

        // then
        verify(workerRepository, times(1)).findById("user123");
    }

    @Test
    void send_sms_worker_not_found() {
        when(workerRepository.findById("not_found")).thenReturn(Optional.empty());
        smsNotificationStrategy.send("not_found", "메시지 전송 실패 테스트");
        verify(workerRepository, times(1)).findById("not_found");
    }
}