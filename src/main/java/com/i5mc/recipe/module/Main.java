package com.i5mc.recipe.module;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.simpleorm.DatabaseException;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

/**
 * Created on 17-1-27.
 */
public class Main extends JavaPlugin implements Listener {

    protected static EbeanServer db;
    protected static Plugin pl;
    protected static Messenger messenger;

    @Override
    public void onEnable() {
        pl = this;
        messenger = new Messenger(this);

        EbeanHandler handler = EbeanManager.DEFAULT.getHandler(this);
        if (handler.isNotInitialized()) {
            handler.define(RecipeLimit.class);
            try {
                handler.initialize();
            } catch (DatabaseException e) {
                throw new RuntimeException(e.toString());
            }
        }
        handler.reflect();
        handler.install();

        db = getDatabase();

        Plugin pr = getServer().getPluginManager().getPlugin("ProRecipes");
        if (!nil(pr)) {
            getLogger().log(Level.INFO, "Hook into ProRecipes...done.");
            getServer().getPluginManager().registerEvents(new PRListener(), this);
        }

        RegisteredListener l = new RegisteredListener(this, this::handle, EventPriority.HIGH, this, false);
        PlayerJoinEvent.getHandlerList().register(l);

        try {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap map = (CommandMap) f.get(getServer().getPluginManager());
            map.register("recipe-module", new MainCommand());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void handle(Listener l, Event event) {
        exec(() -> {
            Player p = ((PlayerEvent) event).getPlayer();
            List<RecipeLimit> list = db.find(RecipeLimit.class).where().eq("player", p.getName()).findList();
            if (!list.isEmpty()) {
                getServer().getScheduler().runTask(this, () -> RecipeLimiter.init(list));
            }
        });
    }

    public static void exec(Runnable r) {
        pl.getServer().getScheduler().runTaskAsynchronously(pl, r);
    }

    public static int addLimit(HumanEntity p, ItemStack item, int value) {
        NBTCompound compound = NBTManager.getInstance().read(item);
        NBTCompound recipe = compound.getCompound("recipe");
        if (!nil(recipe)) {
            RecipeLimiter limiter = RecipeLimiter.getLimiter(recipe.getString("id"));
            int i = limiter.get(p.getName()) + value;
            limiter.put(p.getName(), i);
            int limit = recipe.getInt("limit");
            return limit - i;
        }
        return -1;
    }

    public static int getLimit(HumanEntity p, ItemStack item) {
        // recipe.id : string
        // recipe.limit : int
        NBTCompound compound = NBTManager.getInstance().read(item);
        NBTCompound recipe = compound.getCompound("recipe");
        if (!nil(recipe)) {
            RecipeLimiter limiter = RecipeLimiter.getLimiter(recipe.getString("id"));
            int limit = recipe.getInt("limit");
            return limit - limiter.get(p.getName());
        }
        return -1;
    }

    public static boolean nil(Object i) {
        return i == null;
    }

    public static void set(String p, String recipe, int limit) {
        RecipeLimit li = db.find(RecipeLimit.class).where().eq("player", p).eq("recipe", recipe).findUnique();
        if (nil(li)) {
            li = new RecipeLimit();
            li.setPlayer(p);
            li.setRecipe(recipe);
        }
        li.setLi(limit);
        li.store(db);
    }

}
