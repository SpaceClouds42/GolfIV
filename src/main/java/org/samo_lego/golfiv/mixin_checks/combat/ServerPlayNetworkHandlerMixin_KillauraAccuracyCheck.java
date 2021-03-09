package org.samo_lego.golfiv.mixin_checks.combat;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.samo_lego.golfiv.casts.Golfer;
import org.samo_lego.golfiv.casts.NetworkHandlerCombatData;
import org.samo_lego.golfiv.utils.BallLogger;
import org.samo_lego.golfiv.utils.CheatType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.samo_lego.golfiv.GolfIV.golfConfig;

@Mixin(targets = "net.minecraft.server.network.ServerPlayNetworkHandler$1")
public class ServerPlayNetworkHandlerMixin_KillauraAccuracyCheck {
    @Unique
    private final NetworkHandlerCombatData data = (NetworkHandlerCombatData) this;

    @Shadow
    public ServerPlayerEntity player;

    @Inject(
            method = "method_34218()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"
            ),
            cancellable = true
    )
    private void onHitEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if(golfConfig.combat.checkKillaura) {
            if(data.wasLastHit()) {
                ((Golfer) player).report(CheatType.NO_HAND_SWING, golfConfig.sus.noHandSwing);
                ci.cancel();
            }
            data.setWasLastHit(true);
            data.setEntityHits(data.getEntityHits() + 1);

            if(data.getHandSwings() >= 50) {
                if(golfConfig.main.developerMode)
                    BallLogger.logInfo(data.getEntityHits() + " hits of " + data.getHandSwings() + " tries.");

                ((Golfer) player).setHitAccuracy(data.getEntityHits(), data.getHandSwings());
                if(golfConfig.main.developerMode)
                    BallLogger.logInfo(((Golfer) player).getHitAccuracy() + "% accuracy.");
                data.setHandSwings(0);
                data.setEntityHits(0);
            }
        }
    }
}
