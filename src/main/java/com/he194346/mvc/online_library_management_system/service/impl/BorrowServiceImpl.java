package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Override
    public List<BorrowRecord> getBorrowHistoryByReader(Long readerId) {
        return borrowRecordRepository.findByReader_UserIdOrderByRequestDateDesc(readerId);
    }
}
