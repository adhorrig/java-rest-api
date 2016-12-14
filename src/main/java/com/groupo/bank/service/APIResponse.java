package com.groupo.bank.service;

/**
 *
 * @author anthony
 */
public class APIResponse {
    
    String status;
    String message;

    public APIResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    
    
}
