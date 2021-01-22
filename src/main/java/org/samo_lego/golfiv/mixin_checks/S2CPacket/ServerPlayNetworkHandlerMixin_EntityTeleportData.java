package org.samo_lego.golfiv.mixin_checks.S2CPacket;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.samo_lego.golfiv.mixin_checks.accessors.EntityPositionS2CPacketAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

import static org.samo_lego.golfiv.GolfIV.golfConfig;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin_EntityTeleportData {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet);

    /**
     * If player teleports out of render distance, we sned the destroy entity
     * packet instead of teleport one, in order to hide player's coordinates.
     *
     * @param packet
     * @param ci
     */
    @Inject(
            method = "sendPacket(Lnet/minecraft/network/Packet;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeTeleportData(Packet<?> packet, CallbackInfo ci) {
        if(golfConfig.packet.removeTeleportData && packet instanceof EntityPositionS2CPacket) {
            ServerWorld world = this.player.getServerWorld();

            int entityId = ((EntityPositionS2CPacketAccessor) packet).getId();
            Entity teleporter = world.getEntityById(entityId);

            if(teleporter instanceof ServerPlayerEntity) {
                Collection<ServerPlayerEntity> trackers = PlayerLookup.tracking(this.player);

                if(!trackers.contains(teleporter)) {
                    // Can not track this player (teleporter), why send data?
                    this.sendPacket(new EntitiesDestroyS2CPacket(entityId));
                    ci.cancel();
                }
            }
        }
    }
}
