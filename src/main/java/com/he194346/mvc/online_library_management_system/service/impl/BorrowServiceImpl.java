package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.BorrowService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional(readOnly = true)
    @Override
    public List<BorrowRecord> getBorrowHistoryByReader(Long readerId) {
        return borrowRecordRepository.findBorrowHistoryByReaderId(readerId);
    }
}
