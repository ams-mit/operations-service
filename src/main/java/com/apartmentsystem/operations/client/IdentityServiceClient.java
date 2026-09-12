package com.apartmentsystem.operations.client;

import org.springframework.stereotype.Component;

@Component
public class IdentityServiceClient {

    public boolean validateUser(String userId) {
        return true;
    }
}