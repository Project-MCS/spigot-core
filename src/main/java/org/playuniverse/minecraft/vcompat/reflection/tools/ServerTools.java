package org.playuniverse.minecraft.vcompat.reflection.tools;

import jline.console.ConsoleReader;

public abstract class ServerTools {

    public abstract void setMotd(String text);

    public abstract String getMotd();
    
    public abstract ConsoleReader getConsole();

}