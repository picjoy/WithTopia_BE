package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByReportById(Long reportById);

    List<Report> findByReportToId(Long reportToId);
}
