package org.samo_lego.golfiv.mixin_checks.accessors;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Enables getting the entity ID from packet.
 */
@Mixin(PlayerInteractEntityC2SPacket.class)
public interface PlayerInteractEntityC2SPacketAccessor {
    @Accessor("entityId")
    int getEntityId();
}
