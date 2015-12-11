/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp.library.parsing;

import com.mycompany.bfp.Arguments;

/**
 *
 * @author jake
 */
public class Token {

    public static final int NO_TOKEN = -1;
    public static final int SINGLE_LINE_COMMENT = 0;
    public static final int MULTILINE_OPEN_COMMENT = 1;
    public static final int MULTILINE_CLOSE_COMMENT = 2;
    public static final int NEWLINE = 3;
    public static final int OPEN_BRACE = 4;
    public static final int CLOSE_BRACE = 5;
    public static final int OPEN_PAREN = 6;
    public static final int CLOSE_PAREN = 7;
    public static final int DATA = 8;
    public static final int WHITESPACE = 9;
    public int type;
    public String data;

    public Token(int type, String data)
    {
        this.type = type;
        this.data = data;
    }

    public Token(int type, char current)
    {
        this(type, Character.toString(current));
    }
}
