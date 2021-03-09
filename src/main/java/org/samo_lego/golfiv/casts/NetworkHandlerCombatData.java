package org.samo_lego.golfiv.casts;

public interface NetworkHandlerCombatData {
    void setWasLastHit(boolean wasLastHit);
    boolean wasLastHit();

    void setHandSwings(int swings);
    int getHandSwings();

    void setEntityHits(int hits);
    int getEntityHits();
}
