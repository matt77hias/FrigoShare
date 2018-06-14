package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.leftover.Category;
import com.frigoshare.utils.Filter;

public class CategoryFilter implements Filter<Leftover> {

    private final Category category;

    public Category getCategory() {
        return category;
    }

    public CategoryFilter(Category category) {
        this.category = category;
    }

    @Override
    public boolean accept(Leftover leftover) {
        if (getCategory() == Category.ALL) {
            return true;
        }
        return leftover.getCategory().equals(getCategory().toString());
    }
}
