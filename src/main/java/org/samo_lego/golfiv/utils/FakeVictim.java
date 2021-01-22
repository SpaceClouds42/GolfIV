package org.samo_lego.golfiv.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings("EntityConstructor")
public class FakeVictim extends ServerPlayerEntity {

    private static final String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public FakeVictim(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile);
    }

    public static FakeVictim summonFake(ServerPlayerEntity player) {
        // Gets a random name for NPC victim
        Random rnd = new Random();
        int len = 3 + rnd.nextInt(14);
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            builder.append(allowedChars.charAt(rnd.nextInt(allowedChars.length())));
        String fakeName = builder.toString();

        // Creating fake ServerPlayerEntity
        GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), fakeName);
        ServerWorld world = player.getServerWorld();

        // Player's coordinates
        Vec3d movement = new Vec3d(
                player.getX(),
                player.getY(),
                player.getZ()
        );
        Vec2f rotations = new Vec2f(
                player.yaw,
                player.pitch
        );

        FakeVictim fakeVictim = new FakeVictim(player.getServer(), world, fakeProfile);
        fakeVictim.rotateAroundPlayer(movement, rotations);

        return fakeVictim;
    }

    public void rotateAroundPlayer(Vec3d movement, Vec2f rotation) {
        double phi = Math.toRadians(rotation.x - 90);

        double x = Math.cos(phi);
        double z = Math.sin(phi);

        this.refreshPositionAndAngles(x + movement.getX(), movement.getY() - 1.0D, z + movement.getZ(), rotation.x, rotation.y);
    }

    @Override
    protected void pushAway(Entity entity) {
        // Disables collision with entities
    }

    @Override
    protected void tickCramming() {

    }
}
