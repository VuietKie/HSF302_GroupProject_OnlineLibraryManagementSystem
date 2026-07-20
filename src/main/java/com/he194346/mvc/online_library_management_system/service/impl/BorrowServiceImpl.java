package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.repository.ReaderBorrowHistoryRepository;
import com.he194346.mvc.online_library_management_system.service.BorrowService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final ReaderBorrowHistoryRepository readerBorrowHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> getBorrowHistoryByReader(Long readerId) {
        return readerBorrowHistoryRepository.findBorrowHistoryByReaderId(readerId);
    }
}
