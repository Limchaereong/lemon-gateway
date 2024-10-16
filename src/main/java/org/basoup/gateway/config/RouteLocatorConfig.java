package org.basoup.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RouteLocatorConfig {

	@Bean
	public RouteLocator myRoute(RouteLocatorBuilder builder) {
		//개발환경이라 info레벨로 찍어둠 -> 추후 배포환경 로그레벨 낮추기
		log.info("Configuring routes for API Gateway");

		return builder.routes()
			.route("auth-server",
				p -> p.path("/auth/**")
					.uri("lb://auth-server")
			)
			.route("user-server",
				p -> p.path("/user/**")
					.uri("lb://user-server")
			)
			.route("notification-server",
				p -> p.path("/notification/**")
					.uri("lb://notification-server")
			)
			.route("stock-server",
				p -> p.path("/stock/**")
					.uri("lb://stock-server")
			)
			.route("ledger-server",
				p -> p.path("/ledger/**")
					.uri("lb://ledger-server")
			)
			.route("search-server",
				p -> p.path("/search/**")
					.uri("lb://search-server"))
			.build();
	}
}