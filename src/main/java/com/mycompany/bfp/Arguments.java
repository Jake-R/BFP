/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bfp;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jake
 */
public class Arguments {

    @Parameter
    List<String> parameters = new ArrayList<>();

    @Parameter(names =
    {
        "--libraries", "-l"
    }, description = "Space separated list of library files")
    List<String> libraries = new ArrayList<>();
}
