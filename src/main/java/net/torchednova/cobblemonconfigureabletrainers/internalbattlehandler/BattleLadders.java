package net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.UUID;

public class BattleLadders {
    BattleLadders(int id, String name, ArrayList<Vec3> bats)
    {
        this.index = 0;
        this.maxDist = 16;
        this.id = id;
        this.name = name;
        this.bats = bats;
        this.player = null;

    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public Vec3 getNext()
    {
        index++;
        if (index == bats.size() - 1)
        {
            this.player = null;
        }
        return bats.get(index);
    }
    public boolean delBattle(int id)
    {
        if (bats.size() <= id) return false;
        bats.remove(id);
        return true;
    }

    public Vec3 getExit() { return bats.getLast(); }
    public Vec3 getStart() { return bats.getFirst();}
    public Vec3 getCur() { return bats.get(index); }
    public int getBatSize() { return bats.size(); }
    public Vec3 getBatAtIndex(int ind) { return bats.get(ind); }
    public Boolean setPlayer(UUID uuid) {
        if (hasPlayer() == false)
        {
            this.player = uuid;
            return true;
        }
        else
        {
            return false;
        }
    }
    public Boolean hasPlayer() { if (player == null) { return false; } else { return false; }}
    public UUID getPlayer() { return player; }
    public void addBattle(Vec3 pos) { bats.add(pos); }
    public double getDist() { return this.maxDist; }
    public void setDist(double dist) { this.maxDist = dist; }


    private int id;
    private String name;
    private int index;
    private ArrayList<Vec3> bats;
    private UUID player;
    private double maxDist;

}
