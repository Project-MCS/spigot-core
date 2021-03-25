package org.playuniverse.minecraft.mcs.spigot.utils.log;

import java.awt.Color;

import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

import com.syntaxphoenix.syntaxapi.logging.color.ColorProcessor;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;

public class BukkitLogType extends LogType {

    public static final ColorProcessor PROCESSOR = (flag, type) -> type.asColorString(flag);
    public static final BukkitLogType DEFAULT = new BukkitLogType("info", '7');

    /*
     * 
     */

    private String color;

    /*
     * 
     */

    public BukkitLogType(String id) {
        this(id, '0');
    }

    public BukkitLogType(String id, char color) {
        super(id);
        setColor(color);
    }

    public BukkitLogType(String id, String name, char color) {
        super(id, name);
        setColor(color);
    }

    public BukkitLogType(String id, String color) {
        super(id);
        setColor(color);
    }

    public BukkitLogType(String id, String name, String color) {
        super(id, name);
        setColor(color);
    }

    /*
     * 
     */

    public BukkitLogType setColor(char color) {
        this.color = "&" + color;
        return this;
    }

    public BukkitLogType setColor(String color) {
        this.color = ColorHelper.hexToMinecraftColor(color);
        return this;
    }

    /*
     * 
     */

    @Override
    public ColorProcessor getColorProcessor() {
        return PROCESSOR;
    }

    @Override
    public Color asColor() {
        return new Color(0, 0, 0);
    }

    @Override
    public String asColorString() {
        return color;
    }

    @Override
    public String asColorString(boolean stream) {
        return stream ? asColorString() : "";
    }

}
