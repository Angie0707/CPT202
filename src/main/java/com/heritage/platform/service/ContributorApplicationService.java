package com.heritage.platform.service;

import com.heritage.platform.common.BadRequestException;
import com.heritage.platform.common.ResourceNotFoundException;
import com.heritage.platform.dto.request.AdminContributorApplicationRejectRequest;
import com.heritage.platform.dto.response.AdminContributorApplicationResponse;
import com.heritage.platform.dto.response.MyContributorApplicationResponse;
import com.heritage.platform.entity.ContributorApplication;
import com.heritage.platform.entity.User;
import com.heritage.platform.enums.ContributorApplicationStatus;
import com.heritage.platform.enums.UserRole;
import com.heritage.platform.repository.ContributorApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContributorApplicationService {

    private final ContributorApplicationRepository contributorApplicationRepository;
    private final AuthContextService authContextService;

    public ContributorApplicationService(
            ContributorApplicationRepository contributorApplicationRepository,
            AuthContextService authContextService
    ) {
        this.contributorApplicationRepository = contributorApplicationRepository;
        this.authContextService = authContextService;
    }

    @Transactional(readOnly = true)
    public List<MyContributorApplicationResponse> listMyApplications() {
        User currentUser = authContextService.requireActiveUser();
        return contributorApplicationRepository.findAllByApplicantIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toMyResponse)
                .toList();
    }

    @Transactional
    public MyContributorApplicationResponse createApplication(String applicationReason, String attachmentPath) {
        User currentUser = authContextService.requireActiveUser();

        if (currentUser.getRole() != UserRole.USER) {
            throw new BadRequestException("当前角色无需申请贡献者权限");
        }

        if (contributorApplicationRepository.existsByApplicantIdAndStatus(currentUser.getId(), ContributorApplicationStatus.PENDING)) {
            throw new BadRequestException("已有待处理的贡献者申请");
        }

        ContributorApplication application = contributorApplicationRepository.save(ContributorApplication.create(currentUser, applicationReason, attachmentPath));
        return toMyResponse(application);
    }

    @Transactional(readOnly = true)
    public List<AdminContributorApplicationResponse> listAdminApplications(ContributorApplicationStatus status, String sortBy) {
        authContextService.requireAdmin();

        List<ContributorApplication> applications;
        if (status == null) {
            if ("applicant_username_asc".equals(sortBy)) {
                applications = contributorApplicationRepository.findAllByOrderByApplicantUsernameAsc();
            } else {
                applications = contributorApplicationRepository.findAllByOrderByCreatedAtDesc();
            }
        } else {
            if ("applicant_username_asc".equals(sortBy)) {
                applications = contributorApplicationRepository.findAllByStatusOrderByApplicantUsernameAsc(status);
            } else {
                applications = contributorApplicationRepository.findAllByStatusOrderByCreatedAtDesc(status);
            }
        }

        return applications.stream().map(this::toAdminResponse).toList();
    }

    @Transactional
    public AdminContributorApplicationResponse approve(Long applicationId) {
        User admin = authContextService.requireAdmin();
        ContributorApplication application = contributorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("申请不存在"));

        if (application.getStatus() != ContributorApplicationStatus.PENDING) {
            throw new BadRequestException("仅待处理申请可审批");
        }
        if (application.getApplicant().getRole() != UserRole.USER) {
            throw new BadRequestException("申请人当前角色不允许审批此申请");
        }

        application.getApplicant().changeRole(UserRole.CONTRIBUTOR);
        application.approve(admin);
        return toAdminResponse(application);
    }

    @Transactional
    public AdminContributorApplicationResponse reject(Long applicationId, AdminContributorApplicationRejectRequest request) {
        User admin = authContextService.requireAdmin();
        ContributorApplication application = contributorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("申请不存在"));

        if (application.getStatus() != ContributorApplicationStatus.PENDING) {
            throw new BadRequestException("仅待处理申请可审批");
        }
        if (application.getApplicant().getRole() != UserRole.USER) {
            throw new BadRequestException("申请人当前角色不允许审批此申请");
        }

        application.reject(admin, request.reason().trim());
        return toAdminResponse(application);
    }

    private MyContributorApplicationResponse toMyResponse(ContributorApplication application) {
        return new MyContributorApplicationResponse(
                application.getId(),
                application.getApplicationReason(),
                application.getAttachmentPath(),
                application.getStatus(),
                application.getReviewedBy() == null ? null : application.getReviewedBy().getNickname(),
                application.getRejectReason(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getUpdatedAt()
        );
    }

    private AdminContributorApplicationResponse toAdminResponse(ContributorApplication application) {
        return new AdminContributorApplicationResponse(
                application.getId(),
                application.getApplicant().getId(),
                application.getApplicant().getUsername(),
                application.getApplicant().getNickname(),
                application.getApplicant().getRole(),
                application.getApplicationReason(),
                application.getAttachmentPath(),
                application.getStatus(),
                application.getReviewedBy() == null ? null : application.getReviewedBy().getId(),
                application.getReviewedBy() == null ? null : application.getReviewedBy().getNickname(),
                application.getRejectReason(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getUpdatedAt()
        );
    }
}
