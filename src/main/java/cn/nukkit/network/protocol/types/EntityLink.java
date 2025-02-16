package cn.nukkit.network.protocol.types;

public class EntityLink {


    public static final EntityLink[] EMPTY_ARRAY = new EntityLink[0];

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDER = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long fromEntityUniquieId;
    public long toEntityUniquieId;
    public byte type;
    public boolean immediate;


    public boolean riderInitiated;


    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, byte type, boolean immediate, boolean riderInitiated) {
        this.fromEntityUniquieId = fromEntityUniquieId;
        this.toEntityUniquieId = toEntityUniquieId;
        this.type = type;
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }
}
