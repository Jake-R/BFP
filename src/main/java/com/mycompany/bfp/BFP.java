/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp;

import com.beust.jcommander.JCommander;
import com.mycompany.bfp.library.Library;

/**
 *
 * @author jake
 */
public class BFP {

    public static void main(String[] args)
    {
        Arguments argParser = new Arguments();
        new JCommander(argParser, args);
        Library library = new Library(argParser.libraries, argParser);
    }
}
