package org.playuniverse.minecraft.mcs.spigot.utils.log;

import java.awt.Color;

import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

import com.syntaxphoenix.syntaxapi.logging.color.ColorProcessor;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;

public class McLogType extends LogType {

    public static final ColorProcessor PROCESSOR = (flag, type) -> type.asColorString(flag);

    /*
     * 
     */

    private String color;

    /*
     * 
     */

    public McLogType(String id) {
        this(id, '0');
    }

    public McLogType(String id, char color) {
        super(id);
        setColor(color);
    }

    public McLogType(String id, String name, char color) {
        super(id, name);
        setColor(color);
    }

    public McLogType(String id, String color) {
        super(id);
        this.color = color;
    }

    public McLogType(String id, String name, String color) {
        super(id, name);
        this.color = color;
    }

    /*
     * 
     */

    public McLogType setColor(char color) {
        this.color = "&" + color;
        return this;
    }

    public McLogType setColor(String color) {
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
        return stream ? "" : asColorString();
    }

}
