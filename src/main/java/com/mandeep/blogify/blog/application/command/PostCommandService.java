package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.entity.Post;
import com.mandeep.blogify.blog.domain.model.valueObject.*;
import com.mandeep.blogify.blog.domain.repository.BlogIdGenerator;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.blog.domain.repository.PostRepository;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final BlogIdGenerator blogIdGenerator;
    private final UserFacade userFacade;
    private final CategoryRepository categoryRepository;

    @Transactional
    public UUID createPost(PostRequest postRequest) {

        log.debug("post.create.attempt title='{}' categoryIds={} requestedBy={}",
                postRequest.title(),
                postRequest.categoryIds().toString(),
                postRequest.authorId());

        PostTitle postTitle = new PostTitle(postRequest.title());
        PostContent postContent = new PostContent(postRequest.content());

        Set<CategoryId> categoryIds = postRequest.categoryIds()
                .stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());

        PostCategories postCategories = new PostCategories(categoryIds);

        UserId userId = new UserId(postRequest.authorId());

        validateAuthor(userId);

        validateCategoriesExists(categoryIds);

        PostId postId = blogIdGenerator.nextPostId();

        PostSlug postSlug = generateSlug(postTitle);

        Post post = Post.create(
                postId,
                postTitle,
                postSlug,
                postContent,
                userId,
                postCategories
        );

        postRepository.save(post);

        log.info("post.created id={} title='{}' categoryIds={} createdBy={}",
                post.getPostId().value(),
                post.getPostTitle().value(),
                categoryIds,
                post.getAuthorId().value()
        );

        return post.getPostId().value();
    }

    @Transactional
    public void updatePost(UUID id, PostRequest postRequest) {
        log.debug("post.update.attempt id={} title='{}' requestedBy={}",
                id,
                postRequest.title(),
                postRequest.authorId());

        PostId postId = new PostId(id);
        UserId userId = new UserId(postRequest.authorId());

        UserView author = getActiveAuthor(userId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> PostException.postNotFound(postId)
        );

        if (!post.getAuthorId().equals(userId) && author.role() != Role.ADMIN) {
            throw CommonException.accessDenied("You are not authorized to edit this post");
        }

        PostTitle newTitle = new PostTitle(postRequest.title());
        PostContent newContent = new PostContent(postRequest.content());
        Set<CategoryId> newCategoryIds = postRequest.categoryIds().stream().map(
                (CategoryId::new)
        ).collect(Collectors.toSet());
        PostCategories newCategories = new PostCategories(newCategoryIds);

        // checking if categories are exists or not
        validateCategoriesExists(newCategoryIds);

        // updating the post...
        post.updateContent(newTitle, newContent, newCategories);

        postRepository.save(post);

        log.info("post.updated id={} title='{}' updatedBy={}",
                id,
                postRequest.title(),
                postRequest.authorId());

    }

    @Transactional
    public void deletePost(UUID id, UUID authorId) {

        log.info("post.delete.attempt id={} requestedBy={}",
                id,
                authorId);

        PostId postId = new PostId(id);
        UserId postAuthorId = new UserId(authorId);

        UserView author = getActiveAuthor(postAuthorId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> PostException.postNotFound(postId)
        );

        if (!post.getAuthorId().equals(postAuthorId) && author.role() != Role.ADMIN) {
            throw CommonException.accessDenied("You are not authorized to edit this post");
        }

        post.delete();

        postRepository.save(post);

        log.info("post.deleted id={} deletedBy={}",
                postId.value(),
                postAuthorId.value());
    }

    @Transactional
    public void publishPost(UUID id, UUID authorId) {

        log.debug("post.publish.attempt id={} requestedBy={}", id, authorId);

        UserId postAuthorId = new UserId(authorId);

        UserView author = getActiveAuthor(postAuthorId);

        PostId postId = new PostId(id);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> PostException.postNotFound(postId)
        );

        if (!post.getAuthorId().equals(postAuthorId) && author.role() != Role.ADMIN) {
            throw CommonException.accessDenied("You are not authorized to publish a post you did not author");
        }

        post.publish();

        postRepository.save(post);

        log.info("post.published id={} publishedBy={}", postId.value(), postAuthorId.value());

    }


    private void validateCategoriesExists(Set<CategoryId> categoryIds) {
        Set<CategoryId> existingCategoryIds = categoryRepository.findExistingIds(categoryIds);

        if (existingCategoryIds.size() != categoryIds.size()) {

            Set<CategoryId> missingIds = new HashSet<>(categoryIds);
            missingIds.removeAll(existingCategoryIds);

            throw PostException.postCategoriesNotFound(missingIds);
        }
        log.debug("post.categories.validated ids={}", categoryIds);
    }

    private UserView getActiveAuthor(UserId userId) {
        UserView userView = userFacade.getUserById(userId.value()).orElseThrow(
                AccountException::accountNotFound
        );
        if (!userView.isActive()) {
            throw AccountException.accountNotActive();
        }
        return userView;
    }

    private void validateAuthor(UserId userId) {

        UserView userView = getActiveAuthor(userId);
        log.debug("post.author.validated id={}", userView.id());
    }

    private PostSlug generateSlug(PostTitle title) {

        // generate base slug, not unique till now
        String base = Normalizer.normalize(title.value(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        // Ensure a unique slug is generated
        Set<String> existingSlugs = postRepository.findSlugsByPrefix(base);

        if (!existingSlugs.contains(base)) {
            return new PostSlug(base);
        }

        int counter = 2;
        String uniqueSlug;
        do {
            uniqueSlug = base + "-" + counter;
            counter++;
        } while (existingSlugs.contains(uniqueSlug));

        return new PostSlug(uniqueSlug);
    }


}
