package com.mandeep.blogify.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public non-sealed class Post extends Base {

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT", length = 10000)
    private String content;

    @ManyToMany
    @JoinTable(
            name = "post_categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")

    )
    private Set<Category> categories;

    @OneToOne
    private User author;

    public Post(String title, String content, User author, Set<Category> categories) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.categories = categories;
    }
}
