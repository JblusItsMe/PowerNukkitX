package cn.nukkit.network.protocol;

import lombok.ToString;


@ToString
public class UpdateAdventureSettingsPacket extends DataPacket {
    public boolean noPvM;
    public boolean noMvP;
    public boolean immutableWorld;
    public boolean showNameTags;
    public boolean autoJump;

    @Override
    public void decode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(noPvM);
        this.putBoolean(noMvP);
        this.putBoolean(immutableWorld);
        this.putBoolean(showNameTags);
        this.putBoolean(autoJump);
    }

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_ADVENTURE_SETTINGS_PACKET;
    }
}
