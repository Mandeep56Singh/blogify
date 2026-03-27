package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.exceptions.enums.CategoryError;
import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryDescription;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.domain.model.valueObject.UserId;
import com.mandeep.blogify.blog.domain.repository.BlogIdGenerator;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final BlogIdGenerator blogIdGenerator;
    private final UserFacade userFacade;

    @Transactional
    public UUID createCategory(CategoryRequest categoryRequest) {

        log.debug("category.create.attempt title={} description={} requestedBy={}",
                categoryRequest.title(),
                categoryRequest.description(),
                categoryRequest.userId()
        );

        CategoryTitle title = new CategoryTitle(categoryRequest.title());
        CategoryDescription description = new CategoryDescription(categoryRequest.description());
        UserId userId = new UserId(categoryRequest.userId());

        validateAdmin(userId);

        if (categoryRepository.existsByTitle(title)) {
            log.debug("category.create.rejected reason='{}' title={}",
                    CategoryError.CATEGORY_ALREADY_EXISTS.errorCode(),
                    title.value()
            );
            throw CategoryException.categoryAlreadyExists(title);
        }

        CategoryId categoryId = blogIdGenerator.nextCategoryId();
        Category category = Category.create(categoryId, title, description);

        categoryRepository.save(category);

        log.info("category.created id={} title={} description={} createdBy={}", categoryId.value(), title.value(), description.value(), userId.value());

        return category.getCategoryId().value();
    }

    @Transactional
    public void updateCategory(UUID id, CategoryRequest categoryRequest) {

        log.debug("category.update.attempt id={} title={} description={} requestedBy={}",
                id,
                categoryRequest.title(),
                categoryRequest.description(),
                categoryRequest.userId()
        );

        CategoryTitle newTitle = new CategoryTitle(categoryRequest.title());
        CategoryDescription newDescription = new CategoryDescription(categoryRequest.description());
        UserId userId = new UserId(categoryRequest.userId());
        CategoryId categoryId = new CategoryId(id);

        validateAdmin(userId);

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> CategoryException.categoryNotFound(categoryId));

        if (!category.getTitle().equals(newTitle) && categoryRepository.existsByTitle(newTitle)) {
            throw CategoryException.categoryAlreadyExists(newTitle);
        }

        if (category.getCategoryStatus().isArchived()) {
            throw CategoryException.categoryArchived(categoryId);
        }

        category.update(newTitle, newDescription);

        categoryRepository.save(category);

        log.info("category.updated id={} title={} description={} updatedBy={}", categoryId.value(), newTitle.value(), newDescription.value(), userId.value());
    }

    @Transactional
    public void deleteCategory(UUID targetCategoryId, UUID adminId) {

        log.debug("category.delete.attempt id={} requestedBy={}",
                targetCategoryId, adminId);

        UserId userId = new UserId(adminId);
        CategoryId categoryId = new CategoryId(targetCategoryId);

        validateAdmin(userId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryException.categoryNotFound(categoryId));


        category.delete();

        categoryRepository.save(category);

        log.info("category.deleted id={} deletedBy={}", categoryId.value(), userId.value());
    }


    private void validateAdmin(UserId userId) {

        UserView userView = userFacade.getUserById(userId.value()).orElseThrow(AccountException::accountNotFound);

        if (!userView.isActive()) {
            throw AccountException.accountNotActive();
        }

        if (userView.role() != Role.ADMIN) {
            throw CommonException.accessDenied();
        }

    }


}
