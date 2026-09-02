package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConciergeConfig {

  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  @Bean
  @Primary
  public AiConciergeClient aiConciergeClient(
      @Value("${gemini.mock:true}") boolean useMock,
      GeminiAiConciergeClient geminiClient,
      PlaceRepository placeRepository) {
    if (useMock) {
      return new MockAiConciergeClient(placeRepository);
    }
    return geminiClient;
  }
}
