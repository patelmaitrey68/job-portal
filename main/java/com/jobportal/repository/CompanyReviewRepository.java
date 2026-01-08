package com.jobportal.repository;

import com.jobportal.entity.CompanyReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CompanyReviewRepository extends MongoRepository<CompanyReview, String> {
    List<CompanyReview> findByCompanyIdOrderByCreatedAtDesc(String companyId);
}
