package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.improver.analysis.utils.Warning;
import org.extendj.ast.CompilationUnit;
import org.improver.magpiebridge.analysis.CodeAnalysis;

public class StringEqAnalysis extends CodeAnalysis {
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.STREQ(); }

  public String getName() { return "StringEqAnalysis"; }
}
