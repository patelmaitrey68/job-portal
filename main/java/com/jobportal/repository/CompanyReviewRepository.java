package com.jobportal.repository;

import com.jobportal.entity.CompanyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyReviewRepository extends JpaRepository<CompanyReview, String> {
    List<CompanyReview> findByCompanyIdOrderByCreatedAtDesc(String companyId);
}
