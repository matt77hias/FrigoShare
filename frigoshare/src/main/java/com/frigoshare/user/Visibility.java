package com.frigoshare.user;

public enum Visibility {
    ALL {
        @Override
        public String toPrettyString() {
            return "All";
        }
    }, FRIENDS {
        @Override
        public String toPrettyString() {
            return "Friends";
        }
    }, FRIENDS_OF_FRIENDS {
        @Override
        public String toPrettyString() {
            return "Friends-of-friends";
        }
    };

    private Visibility() {

    }

    public abstract String toPrettyString();

    public static Visibility convert(String s) {
        for (Visibility c : values()) {
            if (c.toString().equals(s)) {
                return c;
            }
        }
        return null;
    }
}

