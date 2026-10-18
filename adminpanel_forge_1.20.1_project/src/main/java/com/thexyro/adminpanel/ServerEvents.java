package com.thexyro.adminpanel;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thexyro.adminpanel.net.OpenPanelPacket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = AdminPanelMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(tree("adminpanel"));
        event.getDispatcher().register(tree("cs2ap"));
        event.getDispatcher().register(tree("cs2adminpanel"));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> tree(String name) {
        return Commands.literal(name)
                .executes(ServerEvents::open)
                .then(Commands.literal("give").then(Commands.literal("op").then(
                        Commands.argument("targets", GameProfileArgument.gameProfile()).executes(ServerEvents::give))))
                .then(Commands.literal("un").then(Commands.literal("op").then(
                        Commands.argument("targets", GameProfileArgument.gameProfile()).executes(ServerEvents::unop))));
    }

    private static int open(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (!Perms.isPermitted(p)) {
            ctx.getSource().sendFailure(Component.literal("Admin Panel'i kullanma yetkin yok."));
            return 0;
        }
        Net.toPlayer(p, new OpenPanelPacket());
        return 1;
    }

    private static int give(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (!Perms.isOwner(p)) {
            ctx.getSource().sendFailure(Component.literal("Sadece sahip yetki verebilir."));
            return 0;
        }
        MinecraftServer server = p.server;
        Perms perms = Perms.get(server);
        List<String> names = new ArrayList<>();
        for (GameProfile gp : GameProfileArgument.getGameProfiles(ctx, "targets")) {
            perms.add(gp.getId());
            names.add(gp.getName());
            ServerPlayer t = server.getPlayerList().getPlayer(gp.getId());
            if (t != null) Sync.one(t);
        }
        final String msg = "Admin Panel yetkisi verildi: " + String.join(", ", names);
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return names.size();
    }

    private static int unop(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        if (!Perms.isOwner(p)) {
            ctx.getSource().sendFailure(Component.literal("Sadece sahip yetkiyi alabilir."));
            return 0;
        }
        MinecraftServer server = p.server;
        Perms perms = Perms.get(server);
        List<String> names = new ArrayList<>();
        for (GameProfile gp : GameProfileArgument.getGameProfiles(ctx, "targets")) {
            perms.remove(gp.getId());
            SpinManager.set(server, gp.getId(), 0); // yetkisi giden kisinin ayarladigi spin de dursun
            names.add(gp.getName());
        }
        Sync.all(server);
        final String msg = "Admin Panel yetkisi alindi: " + String.join(", ", names);
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return names.size();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) SpinManager.tick(server);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) {
            Sync.one(p);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) {
            SpinManager.remove(p.getUUID());
            Sync.all(p.server);
        }
    }
}
