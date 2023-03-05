package net.momirealms.customfishing.fishing.mode;

import net.momirealms.customfishing.CustomFishing;
import net.momirealms.customfishing.fishing.bar.ModeThreeBar;
import net.momirealms.customfishing.manager.FishingManager;
import net.momirealms.customfishing.util.AdventureUtil;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;

public class ModeThreeGame extends FishingGame {

    private final ModeThreeBar modeThreeBar;
    private int fish_position;
    private boolean success;
    private int timer;
    private final int timer_max;
    private double strain;
    private int struggling_time;

    public ModeThreeGame(CustomFishing plugin, FishingManager fishingManager, long deadline, Player player, int difficulty, ModeThreeBar modeThreeBar) {
        super(plugin, fishingManager, deadline, player, difficulty, modeThreeBar);
        this.fish_position = modeThreeBar.getFish_start_position();
        this.success = false;
        this.modeThreeBar = modeThreeBar;
        this.timer_max = modeThreeBar.getStruggling_fish_image().length;
    }

    @Override
    public void run() {
        if (timeOut() || switchItem()) return;
        timer++;
        if (timer >= timer_max) {
            timer = 0;
        }
        if (struggling_time <= 0) {
            if (Math.random() < ((double) difficulty / 200)) {
                struggling_time = (int) (20 + Math.random() * difficulty * 3);
            }
        }
        else {
            struggling_time--;
        }
        if (player.isSneaking()) {
            if (struggling_time > 0) {
                strain += (2 + ((double) difficulty / 5));
                fish_position -= 1;
            }
            else {
                strain += 1;
                fish_position -= 2;
            }
        }
        else {
            fish_position++;
            strain -= 2;
        }
        if (fish_position < modeThreeBar.getSuccess_position() - modeThreeBar.getFish_icon_width() - 1) {
            cancel();
            success = true;
            FishHook fishHook = fishingManager.getBobber(player);
            if (fishHook != null) {
                fishingManager.proceedReelIn(fishHook.getLocation(), player, this);
                fishingManager.removeBobber(player);
            }
            fishingManager.removeFishingPlayer(player);
            return;
        }
        if (fish_position + modeThreeBar.getFish_icon_width() > modeThreeBar.getBar_effective_width() || strain > 50) {
            cancel();
            FishHook fishHook = fishingManager.getBobber(player);
            if (fishHook != null) {
                fishingManager.proceedReelIn(fishHook.getLocation(), player, this);
                fishingManager.removeBobber(player);
            }
            fishingManager.removeFishingPlayer(player);
            return;
        }
        showBar();
    }

    @Override
    public void showBar() {
        String bar = "<font:" + modeThreeBar.getFont() + ">" + modeThreeBar.getBarImage()
                + "<font:" + offsetManager.getFont() + ">" + offsetManager.getOffsetChars(modeThreeBar.getFish_offset() + fish_position) + "</font>"
                + (struggling_time > 0 ? modeThreeBar.getStruggling_fish_image()[timer] : modeThreeBar.getFish_image())
                + "<font:" + offsetManager.getFont() + ">" + offsetManager.getOffsetChars(modeThreeBar.getBar_effective_width() - fish_position - modeThreeBar.getFish_icon_width()) + "</font>"
                + "</font>";
        if (strain > 50) strain = 50;
        if (strain < 0) strain = 0;
        AdventureUtil.playerTitle(player,
                title.replace("{strain}", modeThreeBar.getStrain()[(int) ((strain / 50) * modeThreeBar.getStrain().length)])
                , bar,0,500,0
        );
    }

    @Override
    public boolean isSuccess() {
        return success;
    }
}
