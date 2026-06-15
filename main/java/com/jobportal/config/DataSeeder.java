package com.jobportal.config;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only seed if no jobs exist
        if (jobRepository.count() > 0) {
            System.out.println("[DataSeeder] Database already has data. Skipping seed.");
            return;
        }

        System.out.println("[DataSeeder] Seeding sample data...");

        // Create a sample employer
        User employer = new User();
        employer.setName("TechHire Recruiter");
        employer.setEmail("recruiter@techhire.com");
        employer.setPassword(passwordEncoder.encode("password123"));
        employer.setAccountType(com.jobportal.dto.AccountType.EMPLOYER);
        employer.setIsActive(true);
        employer.setIsEmailVerified(true);
        employer.setCreatedAt(LocalDateTime.now());
        employer.setUpdatedAt(LocalDateTime.now());
        employer = userRepository.save(employer);

        // Create sample jobs
        List<Job> jobs = Arrays.asList(
            createJob("Senior React Developer", "TechNova Solutions", "Build and maintain modern web applications using React, TypeScript, and Next.js. Collaborate with cross-functional teams to deliver high-quality products. You will work on our flagship SaaS platform serving 50,000+ users.", "Remote", "full-time", "senior", 120000.0, 150000.0, "Technology", "Software Development", Arrays.asList("React", "TypeScript", "Next.js", "Redux", "GraphQL"), employer),
            createJob("Backend Engineer - Spring Boot", "FinTech Group", "Design and develop scalable microservices using Spring Boot and Java. Work with PostgreSQL, Redis, and Kafka to build robust financial technology solutions. Join a team of 20+ engineers building the future of payments.", "San Francisco, CA", "full-time", "mid", 130000.0, 160000.0, "Technology", "Software Development", Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Microservices", "Docker", "Kubernetes"), employer),
            createJob("UX/UI Designer", "CreativeSolutions", "Create beautiful, intuitive user interfaces for web and mobile applications. Conduct user research, build wireframes and prototypes, and collaborate with developers to bring designs to life. Experience with Figma and design systems required.", "New York, NY", "contract", "mid", 90000.0, 120000.0, "Design", "UX Design", Arrays.asList("Figma", "Adobe XD", "User Research", "Prototyping", "Design Systems"), employer),
            createJob("Full Stack Developer", "Innovate LLC", "Build end-to-end web applications using React and Node.js. Work across the entire stack from database design to frontend implementation. Strong problem-solving skills and attention to detail are essential.", "Austin, TX", "full-time", "entry", 80000.0, 100000.0, "Technology", "Software Development", Arrays.asList("JavaScript", "React", "Node.js", "MongoDB", "REST APIs"), employer),
            createJob("DevOps Engineer", "CloudFirst Inc.", "Manage and optimize cloud infrastructure on AWS and GCP. Implement CI/CD pipelines, container orchestration, and infrastructure as code. Experience with Terraform, Docker, and Kubernetes required.", "Remote", "full-time", "senior", 140000.0, 175000.0, "Technology", "DevOps", Arrays.asList("AWS", "Docker", "Kubernetes", "Terraform", "CI/CD", "Linux"), employer),
            createJob("Data Analyst", "DataDriven Co.", "Analyze large datasets to uncover business insights and drive decision-making. Build dashboards and reports using SQL, Python, and Tableau. Collaborate with stakeholders to define KPIs and track performance metrics.", "Chicago, IL", "full-time", "entry", 70000.0, 90000.0, "Analytics", "Data Science", Arrays.asList("SQL", "Python", "Tableau", "Excel", "Statistics"), employer),
            createJob("Mobile Developer - React Native", "AppWorks Studio", "Develop cross-platform mobile applications using React Native. Work on iOS and Android apps simultaneously, integrating native modules when needed. Experience with app store deployment and push notifications is a plus.", "Los Angeles, CA", "full-time", "mid", 100000.0, 130000.0, "Technology", "Mobile Development", Arrays.asList("React Native", "JavaScript", "iOS", "Android", "REST APIs"), employer),
            createJob("Product Manager", "GrowthTech", "Lead product strategy and roadmap for our B2B SaaS platform. Work closely with engineering, design, and sales teams. Define user stories, prioritize features, and drive product launches from concept to delivery.", "Seattle, WA", "full-time", "senior", 130000.0, 160000.0, "Management", "Product Management", Arrays.asList("Product Strategy", "Agile", "Scrum", "Data Analysis", "User Research"), employer)
        );

        jobRepository.saveAll(jobs);
        System.out.println("[DataSeeder] Seeded " + jobs.size() + " sample jobs and 1 employer account.");
        System.out.println("[DataSeeder] Employer login: recruiter@techhire.com / password123");
    }

    private Job createJob(String title, String company, String description, String location, String jobType, String experienceLevel, Double salaryMin, Double salaryMax, String category, String industry, List<String> skills, User postedBy) {
        Job job = new Job();
        job.setTitle(title);
        job.setCompany(company);
        job.setDescription(description);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setExperienceLevel(experienceLevel);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setSalaryCurrency("USD");
        job.setCategory(category);
        job.setIndustry(industry);
        job.setSkills(skills);
        job.setPostedBy(postedBy);
        job.setStatus("active");
        job.setIsFeatured(title.contains("Senior") || title.contains("Product"));
        job.setApplicationCount(0);
        job.setViews(0);
        job.setRequirements(Arrays.asList("2+ years of relevant experience", "Strong communication skills", "Team player with attention to detail"));
        job.setResponsibilities(Arrays.asList("Collaborate with cross-functional teams", "Write clean, maintainable code", "Participate in code reviews and sprint planning"));
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return job;
    }
}
