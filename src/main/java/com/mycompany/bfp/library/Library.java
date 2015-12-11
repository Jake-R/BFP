/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp.library;

import com.mycompany.bfp.Arguments;
import com.mycompany.bfp.library.parsing.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jake
 */
public class Library {

    private List<File> libraryFiles = new ArrayList<>();
    private List<Function> functions = new ArrayList<>();

    public Library(List<String> libFiles, Arguments args)
    {
        for (String filePath : libFiles)
        {
            libraryFiles.add(new File(filePath));
        }
        for (File file : libraryFiles)
        {
            String source = "";
            try
            {
                source = stringify(file);
            } catch (IOException ex)
            {
                Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
            }
            Tokenizer tokenizer = new Tokenizer(args);
            tokenizer.tokenize(source);
            functions.addAll(parse(source));
        }
        System.out.println(expand("<>+-add(100,500,6)-+<>"));
    }

    public String expand(String code)
    {
        StringBuilder output = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();
        boolean inParen = false;
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
                    if (currentToken.length() != 0 && !inParen)
                    {
                        output.append(fetchExpanded(currentToken.toString()));
                        currentToken = new StringBuilder();
                    } else if (inParen)
                    {
                        currentToken.append(current);
                    } else
                    {
                        output.append(current);
                    }
                    break;
                case '(':
                    inParen = true;
                    currentToken.append(current);
                    break;
                case ')':
                    inParen = false;
                default:
                    currentToken.append(current);

            }
        }
        return output.toString();
    }

    public String fetchExpanded(String token)
    {
        token = token.replace(")", "");
        String[] split = token.split("[(]");

        for (Function func : functions)
        {
            if (func.getName().equals(split[0]))
            {
                return func.expand(split[1]);
            }
        }
        return null;
    }

    public String clean(File libraryFile) throws IOException
    {
        StringBuilder output = new StringBuilder();
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader(libraryFile));
        } catch (IOException ex)
        {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String line;
        boolean inComment = false;
        whileloop:
        while ((line = reader.readLine()) != null)
        {
            char lastChar = ' ';
            forloop:
            for (int i = 0; i < line.length(); i++)
            {
                char current = line.charAt(i);
                switch (current)
                {
                    case '/':
                        switch (lastChar)
                        {
                            case '/':
                                continue whileloop;
                            case '*':
                                inComment = false;
                                break;

                        }
                        break;
                    case ' ':
                    case '\r':
                    case '\n':
                        break;
                    case '*':
                        if (lastChar == '/')
                        {
                            inComment = true;
                        }
                        break;
                    default:
                        if (!inComment)
                        {
                            output.append(current);
                        }
                }
                lastChar = current;
            }
        }
        return output.toString();
    }

    public String stringify(File libraryFile) throws IOException
    {
        StringBuilder output = new StringBuilder();
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader(libraryFile));
        } catch (IOException ex)
        {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String line;
        while ((line = reader.readLine()) != null)
        {
            output.append(line);
            output.append('\n');
        }
        return output.toString();
    }

    public List<Function> parse(String input)
    {
        List<Function> functionList = new ArrayList<>();
        Deque<String> tokens = new ArrayDeque<>();
        StringBuffer currentToken = new StringBuffer();
        boolean inCode = false;
        for (int i = 0; i < input.length(); i++)
        {
            char current = input.charAt(i);
            switch (current)
            {
                case '(':
                case ')':
                    if (!inCode)
                    {
                        tokens.push(currentToken.toString());
                        currentToken = new StringBuffer();
                    } else
                    {
                        currentToken.append(current);
                    }
                    break;
                case '{':
                    inCode = true;
                    break;
                case '}':
                    inCode = false;
                    String code = currentToken.toString();
                    currentToken = new StringBuffer();
                    String args = tokens.pop();
                    String name = tokens.pop();
                    functionList.add(new Function(name, args, code));

                    break;
                default:
                    currentToken.append(current);

            }
        }
        return functionList;
    }
}
