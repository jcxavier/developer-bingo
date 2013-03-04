package com.jcxavier.devbingo.logic;

import java.util.Arrays;

public final class DummyData {
	public static final String[] FRAMEWORKS_AND_PROG_LANGS = new String[] {
		"ABAP",
		"ActionScript",
		"Ada",
		"APL",
		"AppleScript",
		"AspectJ",
		"ASP.NET",
		"Assembly",
		"Awk",
		"Bash",
		"BASIC",
		"Boo",
		"C",
		"C++",
		"C#",
		"Clojure",
		"Cobra",
		"CoffeeScript",
		"ColdFusion",
		"COBOL",
		"Dart",
		"F#",
		"Forth",
		"Fortran",
		"Go",
		"Haskell",
		"Io",
		"J",
		"Java",
		"JavaScript",
		"Joy",
		"LaTeX",
		"Lisp",
		"Lua",
		"make",
		"MATLAB",
		"MOO",
		"OCaml",
		"Objective-C",
		"Pascal",
		"Perl",
		"PHP",
		"PL/SQL",
		"PostScript",
		"PowerShell",
		"Prolog",
		"Python",
		"Q",
		"Racket",
		"RPG",
		"Ruby",
		"Scala",
		"Scheme",
		"Smalltalk",
		"Tcl",
		"TeX",
		"UnrealScript",
		"VBScript",
		"Verilog",
		"VHDL",
		"Visual Basic",
		"XQuery",
		"XSLT",

		// frameworks
		"Android",
		"iOS",
		"Rails",
		"CakePHP",
		"XNA",
		"Ogre3D",
		"CodeIgnite",
		"jQuery"
	};

	public static boolean isValidValue(final String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}

		return Arrays.asList(FRAMEWORKS_AND_PROG_LANGS).contains(str);
	}
}
