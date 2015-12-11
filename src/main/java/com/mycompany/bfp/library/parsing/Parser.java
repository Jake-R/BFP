/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp.library.parsing;

import com.mycompany.bfp.Arguments;
import com.mycompany.bfp.library.Function;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jake
 */
public class Parser {

    public static final int MAIN = 0;
    public static final int[] MAIN_TOKENS =
    {
        Token.OPEN_BRACE,
        Token.CLOSE_BRACE,
        Token.OPEN_PAREN,
        Token.CLOSE_PAREN,
        Token.MULTILINE_CLOSE_COMMENT
    };
    public static final String MAIN_ERROR = "unexpected token";

    public static final int FUNCTION_DECLARATION = 1;
    public static final int[] FUNCTION_DECLARATION_TOKENS =
    {
        Token.OPEN_BRACE,
        Token.CLOSE_BRACE,
        Token.CLOSE_PAREN,
        Token.DATA,
        Token.MULTILINE_CLOSE_COMMENT
    };
    public static final String FUNCTION_DECLARATION_ERROR = "unexpected token";

    public static final int FUNCTION_ARGUMENTS = 2;
    public static final int[] FUNCTION_ARGUMENTS_TOKENS =
    {
        Token.CLOSE_PAREN,
        Token.MULTILINE_CLOSE_COMMENT
    };
    public static final String FUNCTION_ARGUMENTS_ERROR = "unexpected token";

    public static final int FUNCTION_BODY = 3;
    public static int[] FUNCTION_BODY_TOKENS =
    {
        Token.OPEN_PAREN,
        Token.CLOSE_PAREN,
        Token.CLOSE_BRACE,
        Token.DATA,
        Token.MULTILINE_CLOSE_COMMENT
    };
    public static final String FUNCTION_BODY_ERROR = "unexpected token";

    private String filename;

    public Parser(Arguments args, String filename)
    {
        this.filename = filename;
    }

    public List<Function> parse(List<Token> tokens)
    {
        List<Function> output = new ArrayList<>();
        Function currentFunction = new Function();
        int lineCount = 0;
        int MODE = MAIN;
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++)
        {
            Token current = tokens.get(i);
            switch (current.type)
            {
                case Token.SINGLE_LINE_COMMENT:
                    i = indexOfNext(tokens, Token.NEWLINE, i);
                    continue;
                case Token.MULTILINE_OPEN_COMMENT:
                    i = indexOfNext(tokens, Token.MULTILINE_CLOSE_COMMENT, i);
                    continue;
                case Token.WHITESPACE:
                    continue;
            }
            switch (MODE)
            {
                case MAIN:
                    if (assertNot(MAIN_TOKENS, current))
                    {
                        errorHandler(tokens, i, lineCount, current, MAIN_ERROR);
                    }
                    //convert nested for loops into switch with modes
                    switch (current.type)
                    {
                        case Token.DATA:
                            currentFunction.setName(current.data);
                            MODE = FUNCTION_DECLARATION;
                            break;

                    }
                    break;
                case FUNCTION_DECLARATION:
                    if (assertNot(FUNCTION_DECLARATION_TOKENS, current))
                    {
                        errorHandler(tokens, i, lineCount, current, FUNCTION_DECLARATION_ERROR);
                    }
                    switch (current.type)
                    {
                        case Token.OPEN_PAREN:
                            MODE = FUNCTION_ARGUMENTS;
                            strBuilder = new StringBuilder();

                    }
                    break;
                case FUNCTION_ARGUMENTS:
                    if (assertNot(FUNCTION_ARGUMENTS_TOKENS, current))
                    {
                        errorHandler(tokens, i, lineCount, current, FUNCTION_ARGUMENTS_ERROR);
                    }
                    switch (current.type)
                    {
                        case Token.CLOSE_PAREN:
                            currentFunction.setArgs(strBuilder.toString());
                            MODE = FUNCTION_BODY;
                        default:
                            strBuilder.append(current.data);
                            break;
                    }
                    break;
                case FUNCTION_BODY:
                    if (assertNot(FUNCTION_BODY_TOKENS, current))
                    {
                        errorHandler(tokens, i, lineCount, current, FUNCTION_BODY_ERROR);
                    }
                    switch (current.type)
                    {
                        case Token.OPEN_BRACE:
                            int index = indexOfNext(tokens, Token.CLOSE_BRACE, i);
                            currentFunction.setCode(tokensUntil(tokens, i + 1, index));
                            i = index;
                            MODE = MAIN;
                    }

            }
        }
        return output;
    }

    private int indexOfNext(List<Token> tokens, int type, int index)
    {
        for (index += 1; index < tokens.size(); index++)
        {
            if (tokens.get(index).type == type)
            {
                return index;
            }
        }
        return tokens.size();
    }

    private String tokensUntil(List<Token> tokens, int start, int end)
    {
        StringBuilder sb = new StringBuilder();
        for (; start < end; start++)
        {
            sb.append(tokens.get(start).data);
        }
        return sb.toString();
    }

    public boolean assertNot(int[] types, Token token)
    {
        for (int type : types)
        {
            if (token.type == type)
            {
                return true;
            }
        }
        return false;
    }

    public void errorHandler(List<Token> tokens, int index, int line, Token token, String errorText)
    {
        StringBuilder out = new StringBuilder();
        for (int i = index; i < tokens.size(); i++)
        {
            Token tok = tokens.get(i);
            if (tok.type != Token.NEWLINE)
            {
                out.append(tok.data);
            } else
            {
                break;
            }
        }
        int characterCount = 0;
        for (int i = index; i > 0; i--)
        {
            Token tok = tokens.get(i);
            if (tok.type != Token.NEWLINE)
            {
                characterCount += tok.data.length();
                out.insert(0, tok.data);
            }
        }
        StringBuilder output = new StringBuilder();
        output.append("Error: ");
        output.append(errorText);
        output.append(" \"");
        output.append(token.data);
        output.append("\" at ");
        output.append(Integer.toString(line));
        output.append(':');
        output.append(Integer.toString(characterCount));
        output.append(" in ");
        output.append(filename);
        output.append('\n');
        output.append(out);
        output.append('\n');
        for (int i = 0; i < characterCount - 1; i++)
        {
            output.append(' ');
        }
        output.append('^');

        System.out.println(output);
        System.out.flush();
        System.exit(1);
    }
}
