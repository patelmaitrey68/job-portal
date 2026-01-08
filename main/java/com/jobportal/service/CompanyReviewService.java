package com.jobportal.service;

import com.jobportal.entity.CompanyReview;
import java.util.List;

public interface CompanyReviewService {
    List<CompanyReview> getReviewsForCompany(String companyId);
    CompanyReview submitReview(String companyId, String authorId, String authorName, String title, String comment, int rating);
}
