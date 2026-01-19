package com.mandeep.blogify.category;

import com.mandeep.blogify.common.Base;
import com.mandeep.blogify.post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class Category extends Base {

    @Column(nullable = false, unique = true, length = 120)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Post> posts = new ArrayList<>();

    public Category(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


}
