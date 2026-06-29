package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    public long totalBorrows() {
        return borrowRecordRepository.count();
    }

    @Override
    public List<Object[]> countByStatus() {
        return borrowRecordRepository.countByStatus();
    }

    @Override
    public List<Object[]> countByMonth() {
        return borrowRecordRepository.countByMonth();
    }
}
