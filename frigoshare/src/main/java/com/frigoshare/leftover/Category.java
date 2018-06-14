package com.frigoshare.leftover;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.frigoshare.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public enum Category {
    ALL {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c123;
        }
    },
    BREAD {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c130;
        }
    },
    CANDY {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c83;
        }
    },
    DAIRY {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c59;
        }
    },
    DRINK {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c93;
        }
    },
    FISH {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c127;
        }
    },
    FRUIT {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c3;
        }
    },
    MEAL {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c132;
        }
    },
    MEAT {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c126;
        }
    },
    SOUP {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c136;
        }
    },
    VEGGIE {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c37;
        }
    },
    OTHER {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c79;
        }
    },
    TEST {
        @Override
        public int getDrawableId(Context context) {
            return R.drawable.c170;
        }
    };

    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(getDrawableId(context));
    }

    public abstract int getDrawableId(Context context);

    private Category() {
    }

    public static Category convert(String s) {
        for (Category c : values()) {
            if (c.toString().equals(s)) {
                return c;
            }
        }
        return null;
    }

    public static List<Category> getAllCategories() {
        return new LinkedList<Category>(Arrays.asList(values()));
    }

    public static List<Category> getAllCategoriesExceptAll() {
        List<Category> list = getAllCategories();
        list.remove(ALL);
        return list;
    }
}
