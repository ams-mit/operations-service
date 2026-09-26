package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateWorkLogDTO;
import com.apartmentsystem.operations.entity.WorkLogEntry;
import com.apartmentsystem.operations.repository.WorkLogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkLogService {

    private final WorkLogEntryRepository workLogEntryRepository;

    public WorkLogService(WorkLogEntryRepository workLogEntryRepository) {
        this.workLogEntryRepository = workLogEntryRepository;
    }

    public WorkLogEntry createWorkLog(CreateWorkLogDTO dto) {

        WorkLogEntry workLogEntry = new WorkLogEntry();

        workLogEntry.setWorkOrderId(dto.getWorkOrderId());
        workLogEntry.setTechnicianId(dto.getTechnicianId());
        workLogEntry.setNote(dto.getNote());
        workLogEntry.setLoggedAt(LocalDateTime.now());

        return workLogEntryRepository.save(workLogEntry);
    }

    public List<WorkLogEntry> getWorkLogs(Long workOrderId) {
        return workLogEntryRepository
                .findByWorkOrderIdOrderByLoggedAtAsc(workOrderId);
    }
}