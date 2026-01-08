package com.jobportal.service.impl;

import com.jobportal.entity.CompanyReview;
import com.jobportal.repository.CompanyReviewRepository;
import com.jobportal.service.CompanyReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompanyReviewServiceImpl implements CompanyReviewService {
    @Autowired
    private CompanyReviewRepository reviewRepository;

    @Override
    public List<CompanyReview> getReviewsForCompany(String companyId) {
        return reviewRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    @Override
    public CompanyReview submitReview(String companyId, String authorId, String authorName, String title, String comment, int rating) {
        CompanyReview review = new CompanyReview();
        review.setCompanyId(companyId);
        review.setAuthorId(authorId);
        review.setAuthorName(authorName);
        review.setTitle(title);
        review.setComment(comment);
        review.setRating(rating);
        return reviewRepository.save(review);
    }
}
