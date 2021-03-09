package org.samo_lego.golfiv.mixin_checks.combat;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.samo_lego.golfiv.casts.NetworkHandlerCombatData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.samo_lego.golfiv.GolfIV.golfConfig;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixinCast_NetworkHandlerCombatData implements NetworkHandlerCombatData {
    @Unique
    private boolean wasLastHit;
    @Unique
    private int handSwings;
    @Unique
    private int entityHits;

    @Override
    public void setWasLastHit(boolean wasLastHit) {
        this.wasLastHit = wasLastHit;
    }

    @Override
    public boolean wasLastHit() {
        return this.wasLastHit;
    }

    @Override
    public void setHandSwings(int swings) {
        this.handSwings = swings;
    }

    @Override
    public int getHandSwings() {
        return this.handSwings;
    }

    @Override
    public void setEntityHits(int hits) {
        this.entityHits = hits;
    }

    @Override
    public int getEntityHits() {
        return this.entityHits;
    }


    @Inject(
            method = "onHandSwing(Lnet/minecraft/network/packet/c2s/play/HandSwingC2SPacket;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"
            )
    )
    private void onHandSwing(HandSwingC2SPacket packet, CallbackInfo ci) {
        if(golfConfig.combat.checkKillaura) {
            this.wasLastHit = false;
            ++this.handSwings;
        }
    }
}
