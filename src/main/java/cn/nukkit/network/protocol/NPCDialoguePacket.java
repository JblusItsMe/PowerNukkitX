/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.network.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-07-06
 */


public class NPCDialoguePacket extends DataPacket {


    public static final byte NETWORK_ID = ProtocolInfo.NPC_DIALOGUE_PACKET;
    
    private static final NPCDialogAction[] ACTIONS = NPCDialogAction.values();
    
    private long runtimeEntityId;
    private NPCDialogAction action = NPCDialogAction.OPEN;
    private String dialogue = "";//content
    private String sceneName = "";
    private String npcName = "";
    private String actionJson = "";


    public NPCDialoguePacket() {
        // Indicates when this public constructor were accessible
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        runtimeEntityId = getLLong();
        action = ACTIONS[getVarInt()];
        dialogue = getString();
        sceneName = getString();
        npcName = getString();
        actionJson = getString();
    }

    @Override
    public void encode() {
        reset();
        putLLong(runtimeEntityId);
        putVarInt(action.ordinal());
        putString(dialogue);
        putString(sceneName);
        putString(npcName);
        putString(actionJson);
    }


    public long getRuntimeEntityId() {
        return runtimeEntityId;
    }


    public void setRuntimeEntityId(long runtimeEntityId) {
        this.runtimeEntityId = runtimeEntityId;
    }


    @NotNull
    public NPCDialogAction getAction() {
        return action;
    }


    public void setAction(@NotNull NPCDialogAction action) {
        this.action = action;
    }


    @NotNull
    public String getDialogue() {
        return dialogue;
    }


    public void setDialogue(@NotNull String dialogue) {
        this.dialogue = dialogue;
    }


    @NotNull
    public String getSceneName() {
        return sceneName;
    }


    public void setSceneName(@NotNull String sceneName) {
        this.sceneName = sceneName;
    }


    @NotNull
    public String getNpcName() {
        return npcName;
    }


    public void setNpcName(@NotNull String npcName) {
        this.npcName = npcName;
    }


    @NotNull
    public String getActionJson() {
        return actionJson;
    }


    public void setActionJson(@NotNull String actionJson) {
        this.actionJson = actionJson;
    }


    public enum NPCDialogAction {
        OPEN,
        CLOSE
    }
}
