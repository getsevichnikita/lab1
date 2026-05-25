package com.library.cache;
import lombok.Getter;
import java.util.Objects;

@Getter
public class BookSearchKey {

    private final String authorName;
    private final int page;
    private final int size;

    public BookSearchKey(String authorName, int page, int size) {
        this.authorName = authorName;
        this.page = page;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BookSearchKey that = (BookSearchKey) o;

        return page == that.page
                && size == that.size
                && Objects.equals(authorName, that.authorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorName, page, size);
    }
}