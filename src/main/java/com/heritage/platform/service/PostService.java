package com.heritage.platform.service;

import com.heritage.platform.common.BadRequestException;
import com.heritage.platform.common.ResourceNotFoundException;
import com.heritage.platform.dto.request.CommentCreateRequest;
import com.heritage.platform.dto.request.PostCreateRequest;
import com.heritage.platform.dto.request.PostUpdateRequest;
import com.heritage.platform.dto.response.CommentResponse;
import com.heritage.platform.dto.response.MyPostSummaryResponse;
import com.heritage.platform.dto.response.PostDetailResponse;
import com.heritage.platform.dto.response.PostSummaryResponse;
import com.heritage.platform.entity.Comment;
import com.heritage.platform.entity.Post;
import com.heritage.platform.entity.PostImage;
import com.heritage.platform.entity.User;
import com.heritage.platform.enums.PostStatus;
import com.heritage.platform.repository.CommentRepository;
import com.heritage.platform.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryService categoryService;
    private final AuthContextService authContextService;

    public PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            CategoryService categoryService,
            AuthContextService authContextService
    ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.categoryService = categoryService;
        this.authContextService = authContextService;
    }

    @Transactional
    public PostDetailResponse create(PostCreateRequest request) {
        User author = authContextService.requireContributor();

        Post post = Post.create(
                request.title(),
                request.content(),
                request.coverImageUrl(),
                request.heritageName(),
                request.region(),
                author,
                categoryService.getById(request.categoryId())
        );

        post.replaceImages(buildImages(post, request.imageUrls()));
        return toDetail(postRepository.save(post));
    }

    @Transactional
    public PostDetailResponse update(Long postId, PostUpdateRequest request) {
        User currentUser = authContextService.requireActiveUser();
        Post post = postRepository.findByIdAndAuthorId(postId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        if (post.getStatus() != PostStatus.DRAFT && post.getStatus() != PostStatus.REJECTED) {
            throw new BadRequestException("仅草稿或已驳回文章可编辑");
        }

        post.update(
                request.title(),
                request.content(),
                request.coverImageUrl(),
                request.heritageName(),
                request.region(),
                categoryService.getById(request.categoryId())
        );
        post.replaceImages(buildImages(post, request.imageUrls()));
        return toDetail(post);
    }

    @Transactional
    public PostDetailResponse submitForReview(Long postId) {
        User currentUser = authContextService.requireActiveUser();
        Post post = postRepository.findByIdAndAuthorId(postId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        if (post.getStatus() != PostStatus.DRAFT && post.getStatus() != PostStatus.REJECTED) {
            throw new BadRequestException("当前文章状态不允许提交审核");
        }

        post.submitForReview();
        return toDetail(post);
    }

    @Transactional(readOnly = true)
    public List<MyPostSummaryResponse> listMyPosts(PostStatus status) {
        User currentUser = authContextService.requireActiveUser();
        List<Post> posts = status == null
                ? postRepository.findAllByAuthorIdOrderByUpdatedAtDesc(currentUser.getId())
                : postRepository.findAllByAuthorIdAndStatusOrderByUpdatedAtDesc(currentUser.getId(), status);

        return posts.stream().map(this::toMySummary).toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getMyPostDetail(Long postId) {
        User currentUser = authContextService.requireActiveUser();
        Post post = postRepository.findByIdAndAuthorId(postId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        return toDetail(post);
    }

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> listAll() {
        return postRepository.findAllByStatusOrderByCreatedAtDesc(PostStatus.PUBLISHED)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getDetail(Long postId) {
        Post post = postRepository.findByIdAndStatus(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        return toDetail(post);
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findByIdAndStatus(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        User author = authContextService.requireActiveUser();

        Comment comment = commentRepository.save(Comment.create(request.content(), author, post));
        post.increaseCommentCount();

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                author.getId(),
                author.getNickname(),
                comment.getCreatedAt()
        );
    }

    private List<PostImage> buildImages(Post post, List<String> imageUrls) {
        List<String> urls = imageUrls == null ? List.of() : imageUrls;
        List<PostImage> images = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            images.add(PostImage.create(urls.get(i), null, i + 1, post));
        }
        return images;
    }

    private PostDetailResponse toDetail(Post post) {
        List<CommentResponse> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getId(),
                        comment.getAuthor().getNickname(),
                        comment.getCreatedAt()
                ))
                .toList();

        List<String> imageUrls = post.getImages() == null
                ? new ArrayList<>()
                : post.getImages().stream().map(PostImage::getImageUrl).toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCoverImageUrl(),
                post.getHeritageName(),
                post.getRegion(),
                post.getStatus(),
                post.getAuthor().getNickname(),
                post.getCategory().getName(),
                post.getLikeCount(),
                post.getFavoriteCount(),
                post.getCommentCount(),
                imageUrls,
                comments,
                post.getCreatedAt()
        );
    }

    private PostSummaryResponse toSummary(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getCoverImageUrl(),
                post.getHeritageName(),
                post.getRegion(),
                post.getStatus(),
                post.getAuthor().getNickname(),
                post.getCategory().getName(),
                post.getLikeCount(),
                post.getFavoriteCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }

    private MyPostSummaryResponse toMySummary(Post post) {
        return new MyPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getCoverImageUrl(),
                post.getHeritageName(),
                post.getRegion(),
                post.getStatus(),
                post.getCategory().getName(),
                post.getRejectReason(),
                post.getSubmittedAt(),
                post.getUpdatedAt(),
                post.getCreatedAt()
        );
    }
}
