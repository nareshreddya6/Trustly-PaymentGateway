package com.example.BillingService.service.factory;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.TransactionStatusEnum;
import com.example.BillingService.service.TransactionStatusHandler;
import com.example.BillingService.service.status.handler.ApprovedTransactionStatusHandler;
import com.example.BillingService.service.status.handler.CreatedTransactionStatusHandler;
import com.example.BillingService.service.status.handler.FailedTransactionStatusHandler;
import com.example.BillingService.service.status.handler.PendingTransactionStatusHandler;

@Component
public class TransactionStatusFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionStatusFactory.class);
    
    private final Map<TransactionStatusEnum, Class<? extends TransactionStatusHandler>> handlerMap = new EnumMap<>(TransactionStatusEnum.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @PostConstruct
    private void initializeHandlerMap() {
        handlerMap.put(TransactionStatusEnum.CREATED, CreatedTransactionStatusHandler.class);
        handlerMap.put(TransactionStatusEnum.APPROVED, ApprovedTransactionStatusHandler.class);
        handlerMap.put(TransactionStatusEnum.FAILED, FailedTransactionStatusHandler.class);
        handlerMap.put(TransactionStatusEnum.PENDING, PendingTransactionStatusHandler.class);
    }
    
    public TransactionStatusHandler getStatusHandler(TransactionStatusEnum status) {
        logger.debug("Retrieving handler for transaction status: {}", status);
        
        Class<? extends TransactionStatusHandler> handlerClass = handlerMap.get(status);
        if (handlerClass == null) {
            logger.warn("No handler found for status: {}", status);
            return null;
        }
        
        return applicationContext.getBean(handlerClass);
    }
}