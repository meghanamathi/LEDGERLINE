
package com.ledgerline.service;

import com.ledgerline.entity.AuditLog;
import com.ledgerline.repository.AuditLogRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            String userEmail,
            String action,
            String details,
            String status) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUserEmail(userEmail);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setStatus(status);

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getUserLogs(String email) {
        return auditLogRepository
                .findByUserEmailOrderByCreatedAtDesc(email);
    }
}
