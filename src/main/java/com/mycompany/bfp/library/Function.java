/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp.library;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author jake
 */
public class Function {

    private String[] args;
    private String name;
    private String code;

    public Function(String name, String args, String code)
    {
        this.name = name;
        this.code = code;
        this.args = args.split(",");

    }

    public Function()
    {
    }

    public String expand(String args)
    {
        String[] values = args.split(",");
        int[] parsedValues = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Integer.parseInt(values[i]);
        }
        StringBuilder output = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();
        for (int i = 0; i < code.length(); i++)
        {
            char current = code.charAt(i);
            switch (current)
            {
                case '<':
                case '>':
                case '[':
                case ']':
                case '-':
                case '+':
                case ',':
                case '.':
                    if (currentToken.length() != 0)
                    {
                        String tok = currentToken.toString();
                        currentToken = new StringBuilder();
                        for (int j = 0; j < this.args.length; j++)
                        {
                            if (tok.equals(this.args[j]))
                            {
                                int numIterations = parsedValues[j];
                                char finalChar = current;
                                if (numIterations < 0)
                                {
                                    switch (finalChar)
                                    {
                                        case '<':
                                            finalChar = '>';
                                            break;
                                        case '>':
                                            finalChar = '<';
                                            break;
                                        case '-':
                                            finalChar = '+';
                                            break;
                                        case '+':
                                            finalChar = '-';
                                    }
                                    numIterations *= -1;
                                }
                                for (int k = numIterations; k > 0; k--)
                                {
                                    output.append(finalChar);
                                }
                            }
                        }
                        break;
                    } else
                    {
                        output.append(current);
                    }
                    break;
                default:
                    currentToken.append(current);

            }
        }
        return output.toString();
    }

    public String getName()
    {
        return name;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    public void setArgs(String args)
    {
        this.args = args.split(",");
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}
