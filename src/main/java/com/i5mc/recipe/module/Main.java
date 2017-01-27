package com.i5mc.recipe.module;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.simpleorm.DatabaseException;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

/**
 * Created on 17-1-27.
 */
public class Main extends JavaPlugin implements Listener {

    protected static EbeanServer db;

    @Override
    public void onEnable() {
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
        if (!$.nil(pr)) {
            getLogger().log(Level.INFO, "Hook into ProRecipes...done.");
            getServer().getPluginManager().registerEvents(new PRListener(), this);
        }

        RegisteredListener l = new RegisteredListener(this, this::handle, EventPriority.HIGH, this, false);
        PlayerJoinEvent.getHandlerList().register(l);
    }

    public void handle(Listener l, Event event) {
        Player p = ((PlayerEvent) event).getPlayer();
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            List<RecipeLimit> list = db.find(RecipeLimit.class).where().eq("player", p.getName()).findList();
            if (!list.isEmpty()) {
                getServer().getScheduler().runTask(this, () -> RecipeLimiter.init(list));
            }
        });
    }

    public static boolean limit(Player p, ItemStack result) {
        // recipe.id : string
        // recipe.limit : int
        NBTCompound compound = NBTManager.getInstance().read(result);
        NBTCompound recipe = compound.getCompound("recipe");
        if (!$.nil(recipe)) {
            RecipeLimiter limiter = RecipeLimiter.getLimiter(recipe.getString("id"));
            int limit = recipe.getInt("limit");
            int i = limiter.get(p.getName());
            if (i < limit) {
                limiter.put(p.getName(), i + 1);
            } else {
                return true;
            }
        }
        return false;
    }

}
