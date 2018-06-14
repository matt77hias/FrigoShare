package com.frigoshare.data;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class Description {

    private String name;
    private String mainDescription;

    public Description() {

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }
}
