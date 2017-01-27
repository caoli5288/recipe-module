package com.i5mc.recipe.module;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created on 17-1-27.
 */
@Entity
public class RecipeLimit {

    @Id
    private int id;

    @NotNull
    private String player;

    @NotNull
    private String recipe;

    @NotNull
    private int li;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public int getLi() {
        return li;
    }

    public void setLi(int li) {
        this.li = li;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object i) {
        if ($.nil(i)) return false;
        return i.getClass() == getClass() && i.hashCode() == hashCode();
    }

}
