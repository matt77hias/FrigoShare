package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.utils.Filter;

public class TextualFilter implements Filter<Leftover> {

    private final String text;

    public String getText() {
        return text;
    }

    public TextualFilter(String text) {
        if (text != null) {
            this.text = text.toLowerCase();
        } else {
            this.text = text;
        }
    }

    @Override
    public boolean accept(Leftover leftover) {
        if (getText() == null) {
            return true;
        }

        String name =  leftover.getDescription().getName().toLowerCase();
        String description = leftover.getDescription().getName().toLowerCase();
        return name.contains(getText()) || description.contains(getText());
    }
}
