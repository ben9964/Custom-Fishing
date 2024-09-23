/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customfishing.fishing.competition.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customfishing.CustomFishing;
import net.momirealms.customfishing.fishing.competition.Competition;
import net.momirealms.customfishing.object.DynamicText;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BossBarSender {

    private final Player player;
    private int timer_1;
    private int timer_2;
    private int counter;
    private final int size;
    private final DynamicText[] texts;
    private DynamicText text;
    private ScheduledFuture<?> senderTask;
    private final UUID uuid;
    private boolean force;
    private final BossBarConfig config;
    private boolean isShown;
    private boolean hasClaimedJoin;

    private transient BossBar activeBar;

    public void setText(int position) {
        this.text = texts[position];
        this.force = true;
    }

    public BossBarSender(Player player, BossBarConfig config) {
        String[] str = config.getText();
        this.size = str.length;
        texts = new DynamicText[str.length];
        for (int i = 0; i < str.length; i++) {
            texts[i] = new DynamicText(player, str[i]);
        }
        text = texts[0];
        this.player = player;
        this.uuid = UUID.randomUUID();
        this.config = config;
        this.isShown = false;
    }

    public void show() {
        this.isShown = true;
        createBossBar();
        senderTask = CustomFishing.getInstance().getScheduler().runTaskTimerAsync(() -> {
            if (size != 1) {
                timer_2++;
                if (timer_2 > config.getInterval()) {
                    timer_2 = 0;
                    counter++;
                    if (counter == size) {
                        counter = 0;
                    }
                    setText(counter);
                }
            }
            if (timer_1 < config.getRate()){
                timer_1++;
            } else {
                timer_1 = 0;
                if (text.update() || force) {
                    force = false;
                    updateName();
                    updateProgress();
                }
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    public boolean isVisible() {
        return this.isShown;
    }

    public BossBarConfig getConfig() {
        return config;
    }

    public void hide() {
        player.hideBossBar(activeBar);
        if (senderTask != null && !senderTask.isCancelled()) senderTask.cancel(false);
        this.isShown = false;
        this.activeBar = null;
    }

    private void updateName() {
        this.activeBar.name(MiniMessage.miniMessage().deserialize(text.getLatestValue()));
    }

    private void updateProgress() {
        this.activeBar.progress(Competition.currentCompetition.getProgress());
    }

    private void createBossBar() {
        activeBar = BossBar.bossBar(MiniMessage.miniMessage().deserialize(text.getLatestValue()), Competition.currentCompetition.getProgress(), BossBar.Color.valueOf(config.getColor().name()), BossBar.Overlay.valueOf(config.getOverlay().name()));
        player.showBossBar(activeBar);
    }

    public boolean hasClaimedJoin() {
        return hasClaimedJoin;
    }

    public void setHasClaimedJoinReward(boolean hasClaimedJoin) {
        this.hasClaimedJoin = hasClaimedJoin;
    }
}
