package com.hdfcUW.api.test;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class) 
@CucumberOptions(
		plugin = { "html:target/cucumber-html-report",
		        "json:target/cucumber.json", "pretty",
		        },
				features = {"features"},
				tags = {"@TC_055_ResidentialRisk"},
				monochrome = true
		) 
public class UWRunTest { 
	}

