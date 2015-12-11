/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp.library.parsing;

import com.mycompany.bfp.Arguments;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jake
 */
public class Tokenizer {

    public Tokenizer(Arguments args)
    {

    }

    public List<Token> tokenize(String input)
    {
        List<Token> output = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char current = input.charAt(i);
            char next;
            int tokenType = Token.NO_TOKEN;
            StringBuilder sb;
            switch (current)
            {
                case '(':
                    tokenType = Token.OPEN_PAREN;
                case ')':
                    tokenType = Token.CLOSE_PAREN;
                case '{':
                    tokenType = Token.OPEN_BRACE;
                case '}':
                    tokenType = Token.CLOSE_BRACE;
                    pushTokens(output, currentToken, new Token(tokenType, current));
                    currentToken = new StringBuilder();
                    break;
                case '\r':
                    next = input.charAt(i + 1);
                    sb = new StringBuilder();  //Assemble string for multicharacter token
                    sb.append(current);
                    sb.append(next);
                    if (next == '\n')
                    {
                        tokenType = Token.NEWLINE;

                        //skip next character because it has already been tokenized
                        i++;

                        //add tokens and reset currentToken
                        pushTokens(output, currentToken, new Token(tokenType, sb.toString()));
                        currentToken = new StringBuilder();
                    } else
                    {
                        currentToken.append(current);
                    }
                    break;
                case '\n':
                    tokenType = Token.NEWLINE;
                    pushTokens(output, currentToken, new Token(tokenType, current));
                    currentToken = new StringBuilder();
                    break;
                case '/':
                    next = input.charAt(i + 1);
                    sb = new StringBuilder();  //Assemble string for multicharacter token
                    sb.append(current);
                    sb.append(next);
                    switch (next)
                    {
                        case '/':
                            tokenType = Token.SINGLE_LINE_COMMENT;
                            break;
                        case '*':
                            tokenType = Token.MULTILINE_OPEN_COMMENT;
                            break;
                        default:
                            currentToken.append(current);
                            break;
                    }
                    if (tokenType != Token.NO_TOKEN)
                    {
                        //skip next character because it has already been tokenized
                        i++;

                        //add tokens and reset currentToken
                        pushTokens(output, currentToken, new Token(tokenType, sb.toString()));
                        currentToken = new StringBuilder();
                    }
                    break;
                case '*':
                    next = input.charAt(i + 1);
                    if (next == '/')
                    {
                        tokenType = Token.MULTILINE_CLOSE_COMMENT;
                        //skip next character because it has already been tokenized
                        i++;

                        //Assemble string for multicharacter token
                        sb = new StringBuilder();
                        sb.append(current);
                        sb.append(next);

                        //add tokens and reset currentToken
                        pushTokens(output, currentToken, new Token(tokenType, sb.toString()));
                        currentToken = new StringBuilder();
                    } else
                    {
                        currentToken.append(current);
                    }
                    break;
                case ' ':
                case '\t':
                    tokenType = Token.WHITESPACE;
                    pushTokens(output, currentToken, new Token(tokenType, current));
                    currentToken = new StringBuilder();
                    break;
                default:
                    currentToken.append(current);
            }
        }
        if (currentToken.length() != 0)
        {
            output.add(new Token(Token.DATA, currentToken.toString()));
        }
        return output;
    }

    public void pushTokens(List<Token> tokens, StringBuilder current, Token token)
    {
        if (current.length() != 0)
        {
            tokens.add(new Token(Token.DATA, current.toString()));
        }
        tokens.add(token);
    }
}
