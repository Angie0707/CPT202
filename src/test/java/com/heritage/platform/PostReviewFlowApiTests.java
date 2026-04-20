package com.heritage.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heritage.platform.entity.Category;
import com.heritage.platform.entity.ContributorApplication;
import com.heritage.platform.entity.Post;
import com.heritage.platform.entity.User;
import com.heritage.platform.enums.ContributorApplicationStatus;
import com.heritage.platform.enums.PostStatus;
import com.heritage.platform.enums.UserRole;
import com.heritage.platform.repository.CategoryRepository;
import com.heritage.platform.repository.CommentRepository;
import com.heritage.platform.repository.ContributorApplicationRepository;
import com.heritage.platform.repository.PostRepository;
import com.heritage.platform.repository.UserRepository;
import com.heritage.platform.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PostReviewFlowApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ContributorApplicationRepository contributorApplicationRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private User authorA;
    private User authorB;
    private User admin;
    private Category category;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        contributorApplicationRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        authorA = userRepository.save(new User("author-a", "hash", "Author A", null, UserRole.USER, true));
        authorB = userRepository.save(new User("author-b", "hash", "Author B", null, UserRole.USER, true));
        admin = userRepository.save(new User("admin-a", "hash", "Admin A", null, UserRole.ADMIN, true));
        category = categoryRepository.save(Category.create("测试分类", "Test category"));
    }

    @Test
    void publicList_onlyReturnsPublishedPosts() throws Exception {
        createPost(authorA, PostStatus.DRAFT);
        createPost(authorA, PostStatus.PENDING_REVIEW);
        Post published = createPost(authorA, PostStatus.PUBLISHED);
        createPost(authorA, PostStatus.ARCHIVED);
        createPost(authorA, PostStatus.REJECTED);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(published.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PUBLISHED"));
    }

    @Test
    void publicDetail_onlyAllowsPublishedPosts() throws Exception {
        Post published = createPost(authorA, PostStatus.PUBLISHED);
        Post draft = createPost(authorA, PostStatus.DRAFT);

        mockMvc.perform(get("/api/posts/{postId}", published.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(published.getId()))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        mockMvc.perform(get("/api/posts/{postId}", draft.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void currentUserCanViewOwnPostDetailAcrossAllStatuses() throws Exception {
        List<Post> posts = List.of(
                createPost(authorA, PostStatus.DRAFT),
                createPost(authorA, PostStatus.REJECTED),
                createPost(authorA, PostStatus.PENDING_REVIEW),
                createPost(authorA, PostStatus.PUBLISHED),
                createPost(authorA, PostStatus.ARCHIVED)
        );

        for (Post post : posts) {
            mockMvc.perform(authorRequest(get("/api/my/posts/{postId}", post.getId()), authorA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(post.getId()))
                    .andExpect(jsonPath("$.data.title").value(post.getTitle()))
                    .andExpect(jsonPath("$.data.content").value(post.getContent()))
                    .andExpect(jsonPath("$.data.coverImageUrl").value(post.getCoverImageUrl()))
                    .andExpect(jsonPath("$.data.status").value(post.getStatus().name()))
                    .andExpect(jsonPath("$.data.imageUrls").isArray());
        }

        Post draft = posts.getFirst();
        mockMvc.perform(get("/api/posts/{postId}", draft.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void currentUserCannotViewOtherUsersPostDetail() throws Exception {
        Post otherUsersPost = createPost(authorB, PostStatus.DRAFT);

        mockMvc.perform(authorRequest(get("/api/my/posts/{postId}", otherUsersPost.getId()), authorA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("文章不存在"));
    }

    @Test
    void comment_onlyWorksForPublishedPosts() throws Exception {
        Post published = createPost(authorA, PostStatus.PUBLISHED);
        Post archived = createPost(authorA, PostStatus.ARCHIVED);

        mockMvc.perform(post("/api/posts/{postId}/comments", published.getId())
                        .header("Authorization", "Bearer " + jwtUtils.generateToken(authorB.getId(), authorB.getUsername(), authorB.getRole().name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPayload("A published comment"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("A published comment"));

        Post updatedPost = postRepository.findById(published.getId()).orElseThrow();
        assertThat(updatedPost.getCommentCount()).isEqualTo(1);
        assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(published.getId())).hasSize(1);

        mockMvc.perform(post("/api/posts/{postId}/comments", archived.getId())
                        .header("Authorization", "Bearer " + jwtUtils.generateToken(authorB.getId(), authorB.getUsername(), authorB.getRole().name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPayload("Should fail"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void unauthenticatedWriteEndpointsAreRejectedBySecurityConfig() throws Exception {
        Post draft = createPost(authorA, PostStatus.DRAFT);
        Post published = createPost(authorA, PostStatus.PUBLISHED);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload("JWT Draft"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/posts/{postId}", draft.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload("Should not reach service"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/posts/{postId}/submit-review", draft.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/posts/{postId}/comments", published.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPayload("Anonymous comment"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadEndpointsRequireAuthenticationAndContributorRole() throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "cover.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        mockMvc.perform(multipart("/api/uploads/images").file(image))
                .andExpect(status().isForbidden());

        mockMvc.perform(authorRequest(multipart("/api/uploads/images").file(image), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅投稿用户可执行该操作"));
    }

    @Test
    void userCanOnlyEditOwnDraftOrRejectedPosts() throws Exception {
        Post draft = createPost(authorA, PostStatus.DRAFT);
        Post rejected = createPost(authorA, PostStatus.REJECTED);
        Post pending = createPost(authorA, PostStatus.PENDING_REVIEW);
        Post otherUserDraft = createPost(authorB, PostStatus.DRAFT);

        mockMvc.perform(authorRequest(put("/api/posts/{postId}", draft.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload("Updated draft title"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated draft title"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));

        mockMvc.perform(authorRequest(put("/api/posts/{postId}", rejected.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload("Updated rejected title"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated rejected title"))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        mockMvc.perform(authorRequest(put("/api/posts/{postId}", pending.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload("Should fail"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅草稿或已驳回文章可编辑"));

        mockMvc.perform(authorRequest(put("/api/posts/{postId}", otherUserDraft.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload("Should be hidden"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void submitReview_onlyAllowsDraftOrRejectedOwnedPosts() throws Exception {
        Post draft = createPost(authorA, PostStatus.DRAFT);
        Post rejected = createPost(authorA, PostStatus.REJECTED);
        Post published = createPost(authorA, PostStatus.PUBLISHED);
        Post otherUserDraft = createPost(authorB, PostStatus.DRAFT);

        mockMvc.perform(authorRequest(post("/api/posts/{postId}/submit-review", draft.getId()), authorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"));
        Post draftAfterSubmit = postRepository.findById(draft.getId()).orElseThrow();
        assertThat(draftAfterSubmit.getStatus()).isEqualTo(PostStatus.PENDING_REVIEW);
        assertThat(draftAfterSubmit.getSubmittedAt()).isNotNull();
        assertThat(draftAfterSubmit.getRejectReason()).isNull();
        assertThat(draftAfterSubmit.getReviewedAt()).isNull();
        assertThat(draftAfterSubmit.getReviewedBy()).isNull();

        mockMvc.perform(authorRequest(post("/api/posts/{postId}/submit-review", rejected.getId()), authorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"));

        mockMvc.perform(authorRequest(post("/api/posts/{postId}/submit-review", published.getId()), authorA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("当前文章状态不允许提交审核"));

        mockMvc.perform(authorRequest(post("/api/posts/{postId}/submit-review", otherUserDraft.getId()), authorA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void adminReview_onlyAllowsPendingReviewToPublishedOrRejected() throws Exception {
        Post pending = createPost(authorA, PostStatus.PENDING_REVIEW);
        Post draft = createPost(authorA, PostStatus.DRAFT);

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/review", pending.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload("APPROVE", ""))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        Post approved = postRepository.findById(pending.getId()).orElseThrow();
        assertThat(approved.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(approved.getReviewedAt()).isNotNull();
        assertThat(approved.getReviewedBy().getId()).isEqualTo(admin.getId());
        assertThat(approved.getRejectReason()).isNull();

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/review", draft.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload("APPROVE", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅待审核文章可执行审核操作"));

        Post anotherPending = createPost(authorA, PostStatus.PENDING_REVIEW);
        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/review", anotherPending.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload("REJECT", "Need more evidence"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectReason").value("Need more evidence"));

        Post rejected = postRepository.findById(anotherPending.getId()).orElseThrow();
        assertThat(rejected.getStatus()).isEqualTo(PostStatus.REJECTED);
        assertThat(rejected.getRejectReason()).isEqualTo("Need more evidence");
        assertThat(rejected.getReviewedAt()).isNotNull();
        assertThat(rejected.getReviewedBy().getId()).isEqualTo(admin.getId());

        Post thirdPending = createPost(authorA, PostStatus.PENDING_REVIEW);
        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/review", thirdPending.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload("REJECT", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("驳回原因不能为空"));
    }

    @Test
    void archive_onlyAllowsPublishedToArchived() throws Exception {
        Post published = createPost(authorA, PostStatus.PUBLISHED);
        Post rejected = createPost(authorA, PostStatus.REJECTED);

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/archive", published.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ARCHIVED"));

        Post archived = postRepository.findById(published.getId()).orElseThrow();
        assertThat(archived.getStatus()).isEqualTo(PostStatus.ARCHIVED);
        assertThat(archived.getReviewedAt()).isNotNull();
        assertThat(archived.getReviewedBy().getId()).isEqualTo(admin.getId());

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/archive", rejected.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅已发布文章可归档"));
    }

    @Test
    void restore_onlyAllowsArchivedToPublished() throws Exception {
        Post archived = createPost(authorA, PostStatus.ARCHIVED);
        Post draft = createPost(authorA, PostStatus.DRAFT);

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/restore", archived.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        Post restored = postRepository.findById(archived.getId()).orElseThrow();
        assertThat(restored.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(restored.getReviewedAt()).isNotNull();
        assertThat(restored.getReviewedBy().getId()).isEqualTo(admin.getId());
        assertThat(restored.getRejectReason()).isNull();

        mockMvc.perform(adminRequest(post("/api/admin/posts/{postId}/restore", draft.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅已归档文章可恢复发布"));
    }

    @Test
    void nonAdminCannotAccessAdminEndpoints() throws Exception {
        Post pending = createPost(authorA, PostStatus.PENDING_REVIEW);

        mockMvc.perform(authorRequest(get("/api/admin/posts"), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(get("/api/admin/posts/{postId}", pending.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/posts/{postId}/review", pending.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload("APPROVE", ""))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/posts/{postId}/archive", pending.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/posts/{postId}/restore", pending.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));
    }

    @Test
    void adminCanSearchPostsByTitleAndCombineWithStatus() throws Exception {
        Post pendingEmbroidery = createPost(authorA, PostStatus.PENDING_REVIEW, "Suzhou Embroidery Review");
        createPost(authorA, PostStatus.PUBLISHED, "Suzhou Embroidery Published");
        createPost(authorA, PostStatus.PENDING_REVIEW, "Kunqu Opera Notes");

        mockMvc.perform(adminRequest(get("/api/admin/posts").param("title", "embroidery")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").exists())
                .andExpect(jsonPath("$.data[1].title").exists());

        mockMvc.perform(adminRequest(get("/api/admin/posts")
                        .param("status", "PENDING_REVIEW")
                        .param("title", "embroidery")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(pendingEmbroidery.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.data[0].title").value("Suzhou Embroidery Review"));
    }

    @Test
    void pendingReviewQueueIsOrderedBySubmittedAtDesc() throws Exception {
        Post olderPending = createPost(authorA, PostStatus.PENDING_REVIEW, "Older Pending Article");
        Thread.sleep(5L);
        Post newerPending = createPost(authorA, PostStatus.PENDING_REVIEW, "Newer Pending Article");

        mockMvc.perform(adminRequest(get("/api/admin/posts").param("status", "PENDING_REVIEW")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(newerPending.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.data[1].id").value(olderPending.getId()))
                .andExpect(jsonPath("$.data[1].status").value("PENDING_REVIEW"));
    }

    @Test
    void adminCanSortPostsBySubmittedAtAndReviewedAtWithoutBreakingFilters() throws Exception {
        Post alphaPendingOlder = createPost(authorA, PostStatus.PENDING_REVIEW, "Alpha Pending Older");
        Thread.sleep(5L);
        Post alphaPendingNewer = createPost(authorA, PostStatus.PENDING_REVIEW, "Alpha Pending Newer");

        mockMvc.perform(adminRequest(get("/api/admin/posts")
                        .param("status", "PENDING_REVIEW")
                        .param("title", "Alpha")
                        .param("sort", "SUBMITTED_DESC")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(alphaPendingNewer.getId()))
                .andExpect(jsonPath("$.data[1].id").value(alphaPendingOlder.getId()));

        Post reviewedOlder = createPost(authorA, PostStatus.PUBLISHED, "Reviewed Older");
        Thread.sleep(5L);
        Post reviewedNewer = createPost(authorA, PostStatus.REJECTED, "Reviewed Newer");
        Post unreviewedDraft = createPost(authorA, PostStatus.DRAFT, "Unreviewed Draft");

        mockMvc.perform(adminRequest(get("/api/admin/posts").param("sort", "REVIEWED_DESC")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(reviewedNewer.getId()))
                .andExpect(jsonPath("$.data[1].id").value(reviewedOlder.getId()))
                .andExpect(jsonPath("$.data[2].id").value(unreviewedDraft.getId()))
                .andExpect(jsonPath("$.data[2].reviewedAt").isEmpty());
    }

    @Test
    void adminPostListSupportsPaginationWithFiltersAndSort() throws Exception {
        Post alphaPendingOlder = createPost(authorA, PostStatus.PENDING_REVIEW, "Paged Alpha Older");
        Thread.sleep(5L);
        Post alphaPendingNewer = createPost(authorA, PostStatus.PENDING_REVIEW, "Paged Alpha Newer");
        createPost(authorA, PostStatus.PUBLISHED, "Paged Alpha Published");

        mockMvc.perform(adminRequest(get("/api/admin/posts")
                        .param("status", "PENDING_REVIEW")
                        .param("title", "Paged Alpha")
                        .param("sort", "SUBMITTED_DESC")
                        .param("page", "0")
                        .param("size", "1")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Page", "0"))
                .andExpect(header().string("X-Size", "1"))
                .andExpect(header().string("X-Total-Elements", "2"))
                .andExpect(header().string("X-Total-Pages", "2"))
                .andExpect(header().string("X-Has-Previous", "false"))
                .andExpect(header().string("X-Has-Next", "true"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(alphaPendingNewer.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING_REVIEW"));

        mockMvc.perform(adminRequest(get("/api/admin/posts")
                        .param("status", "PENDING_REVIEW")
                        .param("title", "Paged Alpha")
                        .param("sort", "SUBMITTED_DESC")
                        .param("page", "1")
                        .param("size", "1")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Page", "1"))
                .andExpect(header().string("X-Size", "1"))
                .andExpect(header().string("X-Total-Elements", "2"))
                .andExpect(header().string("X-Total-Pages", "2"))
                .andExpect(header().string("X-Has-Previous", "true"))
                .andExpect(header().string("X-Has-Next", "false"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(alphaPendingOlder.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING_REVIEW"));
    }

    @Test
    void adminCanListUsersByUsernameAndRole() throws Exception {
        User contributor = userRepository.save(new User("contributor-a", "hash", "Contributor A", null, UserRole.CONTRIBUTOR, true));
        User inactiveUser = userRepository.save(new User("inactive-user", "hash", "Inactive User", null, UserRole.USER, false));

        mockMvc.perform(adminRequest(get("/api/admin/users").param("username", "author")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].username").exists());

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("username", "contributor")
                        .param("role", "CONTRIBUTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(contributor.getId()))
                .andExpect(jsonPath("$.data[0].role").value("CONTRIBUTOR"))
                .andExpect(jsonPath("$.data[0].active").value(true));

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("username", "inactive-user")
                        .param("role", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(inactiveUser.getId()))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].active").value(false));
    }

    @Test
    void adminCanUpdateUserRoleBetweenUserAndContributorOnly() throws Exception {
        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/role", authorA.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "CONTRIBUTOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("CONTRIBUTOR"));
        assertThat(userRepository.findById(authorA.getId()).orElseThrow().getRole()).isEqualTo(UserRole.CONTRIBUTOR);

        User contributor = userRepository.save(new User("role-switch", "hash", "Role Switch", null, UserRole.CONTRIBUTOR, true));
        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/role", contributor.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("USER"));
        assertThat(userRepository.findById(contributor.getId()).orElseThrow().getRole()).isEqualTo(UserRole.USER);

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/role", admin.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("不能修改管理员角色"));

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/role", authorB.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("不支持将用户设置为管理员"));
    }

    @Test
    void downgradedContributorDisappearsFromContributorFilterAndAppearsUnderUserFilter() throws Exception {
        User contributor = userRepository.save(new User("filter-role-switch", "hash", "Filter Role Switch", null, UserRole.CONTRIBUTOR, true));

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/role", contributor.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(contributor.getId()))
                .andExpect(jsonPath("$.data.role").value("USER"));

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("role", "CONTRIBUTOR")
                        .param("username", "filter-role-switch")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("role", "USER")
                        .param("username", "filter-role-switch")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(contributor.getId()))
                .andExpect(jsonPath("$.data[0].username").value("filter-role-switch"))
                .andExpect(jsonPath("$.data[0].role").value("USER"));
    }

    @Test
    void adminCanDeactivateAndReactivateManageableUsers() throws Exception {
        User contributor = userRepository.save(new User("active-role-switch", "hash", "Active Role Switch", null, UserRole.CONTRIBUTOR, true));

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/deactivate", authorA.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorA.getId()))
                .andExpect(jsonPath("$.data.active").value(false));
        assertThat(userRepository.findById(authorA.getId()).orElseThrow().getActive()).isFalse();

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/deactivate", contributor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(contributor.getId()))
                .andExpect(jsonPath("$.data.active").value(false));
        assertThat(userRepository.findById(contributor.getId()).orElseThrow().getActive()).isFalse();

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/activate", contributor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(contributor.getId()))
                .andExpect(jsonPath("$.data.active").value(true));
        assertThat(userRepository.findById(contributor.getId()).orElseThrow().getActive()).isTrue();
    }

    @Test
    void deactivatedUsersDisappearFromActiveStateAndCannotUseProtectedWriteFlow() throws Exception {
        Post published = createPost(authorB, PostStatus.PUBLISHED);

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/deactivate", authorA.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorA.getId()))
                .andExpect(jsonPath("$.data.active").value(false));

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("role", "USER")
                        .param("username", "author-a")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(authorA.getId()))
                .andExpect(jsonPath("$.data[0].active").value(false));

        mockMvc.perform(authorRequest(post("/api/posts/{postId}/comments", published.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPayload("Inactive users should be blocked"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("账号已被禁用"));

        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/activate", authorA.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorA.getId()))
                .andExpect(jsonPath("$.data.active").value(true));

        mockMvc.perform(adminRequest(get("/api/admin/users")
                        .param("role", "USER")
                        .param("username", "author-a")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(authorA.getId()))
                .andExpect(jsonPath("$.data[0].active").value(true));
    }

    @Test
    void adminCannotDeactivateProtectedAdminTargetsOrSelf() throws Exception {
        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/deactivate", admin.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("不能停用当前登录管理员"));

        User otherAdmin = userRepository.save(new User("admin-b", "hash", "Admin B", null, UserRole.ADMIN, true));
        mockMvc.perform(adminRequest(post("/api/admin/users/{userId}/deactivate", otherAdmin.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("不能停用管理员账号"));
    }

    @Test
    void nonAdminCannotAccessAdminUserManagement() throws Exception {
        mockMvc.perform(authorRequest(get("/api/admin/users"), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/users/{userId}/role", authorB.getId()), authorA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("role", "CONTRIBUTOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/users/{userId}/deactivate", authorB.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/users/{userId}/activate", authorB.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));
    }

    @Test
    void userCanCreateAndViewOwnContributorApplications() throws Exception {
        // 创建一个包含申请理由的请求体
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("applicationReason", "Test application reason");
        String requestJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(authorRequest(multipart("/api/my/contributor-applications").param("request", requestJson), authorA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(authorRequest(get("/api/my/contributor-applications"), authorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));

        mockMvc.perform(authorRequest(get("/api/my/contributor-applications"), authorB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 再次提交申请（应该失败）
        mockMvc.perform(authorRequest(multipart("/api/my/contributor-applications").param("request", requestJson), authorA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("已有待处理的贡献者申请"));
    }

    @Test
    void onlyUserCanCreateContributorApplications() throws Exception {
        User contributor = userRepository.save(new User("contributor-user", "hash", "Contributor User", null, UserRole.CONTRIBUTOR, true));

        // 创建一个包含申请理由的请求体
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("applicationReason", "Test application reason");
        String requestJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(authorRequest(multipart("/api/my/contributor-applications").param("request", requestJson), contributor))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("当前角色无需申请贡献者权限"));

        // 管理员申请（应该失败）
        mockMvc.perform(adminRequest(multipart("/api/my/contributor-applications").param("request", requestJson)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("当前角色无需申请贡献者权限"));
    }

    @Test
    void adminCanReviewContributorApplicationsAndPromoteUser() throws Exception {
        ContributorApplication pending = contributorApplicationRepository.save(ContributorApplication.create(authorA, "Test application reason", null));

        mockMvc.perform(adminRequest(get("/api/admin/contributor-applications").param("status", "PENDING")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(pending.getId()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));

        mockMvc.perform(adminRequest(post("/api/admin/contributor-applications/{applicationId}/approve", pending.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        assertThat(userRepository.findById(authorA.getId()).orElseThrow().getRole()).isEqualTo(UserRole.CONTRIBUTOR);

        mockMvc.perform(adminRequest(post("/api/admin/contributor-applications/{applicationId}/reject", pending.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("reason", "Application already approved"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅待处理申请可审批"));
    }

    @Test
    void adminCanRejectContributorApplicationsWithoutPromotingUser() throws Exception {
        ContributorApplication pending = contributorApplicationRepository.save(ContributorApplication.create(authorB, "Test application reason", null));

        mockMvc.perform(adminRequest(post("/api/admin/contributor-applications/{applicationId}/reject", pending.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload("reason", "Please provide stronger heritage contribution evidence"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectReason").value("Please provide stronger heritage contribution evidence"))
                .andExpect(jsonPath("$.data.reviewedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.reviewerName").value(admin.getNickname()));

        assertThat(userRepository.findById(authorB.getId()).orElseThrow().getRole()).isEqualTo(UserRole.USER);

        ContributorApplication rejected = contributorApplicationRepository.findById(pending.getId()).orElseThrow();
        assertThat(rejected.getStatus()).isEqualTo(ContributorApplicationStatus.REJECTED);
        assertThat(rejected.getRejectReason()).isEqualTo("Please provide stronger heritage contribution evidence");
        assertThat(rejected.getReviewedAt()).isNotNull();
        assertThat(rejected.getReviewedBy().getId()).isEqualTo(admin.getId());

        mockMvc.perform(authorRequest(get("/api/my/contributor-applications"), authorB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.data[0].rejectReason").value("Please provide stronger heritage contribution evidence"))
                .andExpect(jsonPath("$.data[0].reviewedAt").isNotEmpty())
                .andExpect(jsonPath("$.data[0].reviewerName").value(admin.getNickname()));
    }

    @Test
    void nonAdminCannotAccessAdminContributorApplications() throws Exception {
        ContributorApplication pending = contributorApplicationRepository.save(ContributorApplication.create(authorA, "Test application reason", null));

        mockMvc.perform(authorRequest(get("/api/admin/contributor-applications"), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));

        mockMvc.perform(authorRequest(post("/api/admin/contributor-applications/{applicationId}/approve", pending.getId()), authorA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可执行该操作"));
    }

    private MockHttpServletRequestBuilder authorRequest(MockHttpServletRequestBuilder request, User user) {
        return request.header("Authorization", "Bearer " + jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole().name()));
    }

    private MockHttpServletRequestBuilder adminRequest(MockHttpServletRequestBuilder request) {
        return authorRequest(request, admin);
    }

    private Post createPost(User author, PostStatus status) {
        return createPost(author, status, "Title " + status + " " + System.nanoTime());
    }

    private Post createPost(User author, PostStatus status, String title) {
        Post post = Post.create(
                title,
                "Content for " + status,
                "https://example.com/cover.jpg",
                "Heritage item",
                "Suzhou",
                author,
                category
        );

        switch (status) {
            case DRAFT -> {
            }
            case PENDING_REVIEW -> post.submitForReview();
            case PUBLISHED -> {
                post.submitForReview();
                post.approve(admin);
            }
            case ARCHIVED -> {
                post.submitForReview();
                post.approve(admin);
                post.archive(admin);
            }
            case REJECTED -> {
                post.submitForReview();
                post.reject(admin, "Needs revision");
            }
        }

        return postRepository.save(post);
    }

    private Object commentPayload(String content) {
        return payload(
                "content", content
        );
    }

    private Object updatePayload(String title) {
        return payload(
                "title", title,
                "content", "Updated content",
                "categoryId", category.getId(),
                "coverImageUrl", "https://example.com/new-cover.jpg",
                "heritageName", "Updated heritage",
                "region", "Updated region",
                "imageUrls", List.of("https://example.com/gallery-1.jpg")
        );
    }

    private Object createPayload(String title) {
        return payload(
                "title", title,
                "content", "Draft content",
                "categoryId", category.getId(),
                "coverImageUrl", "https://example.com/cover.jpg",
                "heritageName", "Heritage item",
                "region", "Suzhou",
                "imageUrls", List.of("https://example.com/gallery-1.jpg")
        );
    }

    private Object reviewPayload(String action, String reason) {
        return payload(
                "action", action,
                "reason", reason
        );
    }

    private Map<String, Object> payload(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }
        return map;
    }
}
