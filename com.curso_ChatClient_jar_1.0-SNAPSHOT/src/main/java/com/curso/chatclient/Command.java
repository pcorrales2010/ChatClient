/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import java.util.Arrays;

/**
 *
 * @author pcorrale
 */
public enum Command {
    SECRET("/secret"),
    MENU("/menu"),
    HEADORTAILS("/headortails"),
    JOKE("/joke"),
    EVENT("/event"),
    JOIN("/join"),
    NOOP("");

    private String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // parse command
    public static Command parseCommand(String str) {
        //  [/join, sala2]
        for (Command cmd : values()) {
            var strLower = Arrays.asList(str.toLowerCase().split(" "));
            if (strLower.size() > 0) {
                if (strLower.get(0).equals(cmd.getValue())) {
                    return cmd;
                }
            }
        }
        return NOOP;
    }
}
