package com.example.BillingService.service.factory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.ProviderEnum;
import com.example.BillingService.service.ProviderHandler;
import com.example.BillingService.service.provider.handler.TrustlyProviderHandler;

@Component
public class ProviderHandlerFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ProviderHandlerFactory.class);
    private final ApplicationContext springContext;
    private final Map<ProviderEnum, Class<? extends ProviderHandler>> handlerMap;
    
    @Autowired
    public ProviderHandlerFactory(ApplicationContext springContext) {
        this.springContext = springContext;
        this.handlerMap = initializeHandlerMap();
    }
    
    private Map<ProviderEnum, Class<? extends ProviderHandler>> initializeHandlerMap() {
        Map<ProviderEnum, Class<? extends ProviderHandler>> map = new ConcurrentHashMap<>();
        map.put(ProviderEnum.TRUSTLY, TrustlyProviderHandler.class);
        return map;
    }
    
    public Optional<ProviderHandler> getHandler(Integer providerId) {
        if (providerId == null) {
            log.debug("Null provider ID received");
            return Optional.empty();
        }
        
        ProviderEnum provider = ProviderEnum.getProviderEnumById(providerId);
        if (provider == null) {
            log.debug("Unknown provider ID: {}", providerId);
            return Optional.empty();
        }
        
        return instantiateHandler(provider);
    }
    
    private Optional<ProviderHandler> instantiateHandler(ProviderEnum provider) {
        Class<? extends ProviderHandler> handlerClass = handlerMap.get(provider);
        
        if (handlerClass == null) {
            log.warn("No handler implementation found for provider: {}", provider);
            return Optional.empty();
        }
        
        try {
            return Optional.of(springContext.getBean(handlerClass));
        } catch (Exception e) {
            log.error("Failed to create handler for {}: {}", provider, e.getMessage());
            return Optional.empty();
        }
    }
}