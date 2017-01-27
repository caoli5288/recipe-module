package com.i5mc.recipe.module;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 17-1-27.
 */
public class RecipeLimiter extends HashMap<String, Integer> {

    static class MXLimiter extends HashMap<String, RecipeLimiter> {

        static final MXLimiter INSTANCE = new MXLimiter();

        @Override
        public RecipeLimiter get(Object key) {
            return computeIfAbsent(String.valueOf(key), i -> new RecipeLimiter());
        }
    }

    @Override
    public Integer get(Object key) {
        return computeIfAbsent(String.valueOf(key), l -> 0);
    }

    public static void init(List<RecipeLimit> l) {
        for (RecipeLimit i : l) {
            RecipeLimiter limiter = getLimiter(i.getRecipe());
            limiter.put(i.getPlayer(), -(i.getLi()));
        }
    }

    public static RecipeLimiter getLimiter(String i) {
        return MXLimiter.INSTANCE.get(i);
    }

}
