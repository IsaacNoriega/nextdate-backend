package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConciergeConfig {

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
