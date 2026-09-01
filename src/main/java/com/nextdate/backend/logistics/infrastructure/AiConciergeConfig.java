package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
public class AiConciergeConfig {

  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(name = "aws.bedrock.mock", havingValue = "false")
  public BedrockRuntimeClient bedrockRuntimeClient(
      @Value("${aws.bedrock.region}") String region,
      @Value("${aws.credentials.access-key}") String accessKey,
      @Value("${aws.credentials.secret-key}") String secretKey) {
    StaticCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

    return BedrockRuntimeClient.builder()
        .region(Region.of(region))
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Bean
  @Primary
  public AiConciergeClient aiConciergeClient(
      @Value("${aws.bedrock.mock:true}") boolean useMock,
      BedrockAiConciergeClient bedrockClient,
      PlaceRepository placeRepository) {
    if (useMock) {
      return new MockAiConciergeClient(placeRepository);
    }
    return bedrockClient;
  }
}
