package com.myinsta.apigateway.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

/**
 * 
 * @author rima.devwork@gmail.com
 *
 */
@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, 
                                        WebProperties webProperties,
                                        ApplicationContext applicationContext,
                                        ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        
        Map<String, Object> errorPropertiesMap = new HashMap<>();
        errorPropertiesMap.put("timestamp", Instant.now());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";

        if (error instanceof JwtAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            message = error.getMessage();
        } else if (error instanceof RuntimeException) {
            status = HttpStatus.BAD_REQUEST;
            message = error.getMessage();
        }

        errorPropertiesMap.put("status", status.value());
        errorPropertiesMap.put("error", status.getReasonPhrase());
        errorPropertiesMap.put("message", message);
        errorPropertiesMap.put("path", request.path());

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
