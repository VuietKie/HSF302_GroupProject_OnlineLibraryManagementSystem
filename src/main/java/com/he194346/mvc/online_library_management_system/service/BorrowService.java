package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;

import java.util.List;

public interface BorrowService {
    List<BorrowRecord> getBorrowHistoryByReader(Long readerId);
}
