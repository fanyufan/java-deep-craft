package com.deepcraft;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "sayhi")
public class HelloMojo extends AbstractMojo {

    @Parameter(property = "name", defaultValue = "world")
    private String name;

    @Override
    public void execute() {
        getLog().info("Hello, " + name + "!");
    }
}