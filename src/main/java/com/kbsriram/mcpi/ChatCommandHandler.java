package com.kbsriram.mcpi;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class ChatCommandHandler
{
    public final static class Post
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            StringBuilder sb = new StringBuilder();
            String[] args = cmd.getArgs();
            for (int i=0; i<args.length; i++) {
                if (i > 0) { sb.append(","); }
                sb.append(args[i]);
            }
            MinecraftServer.getServer().getConfigurationManager()
                .sendChatMsg(new ChatComponentText(sb.toString()));
            return VOID;
        }
    }
}
