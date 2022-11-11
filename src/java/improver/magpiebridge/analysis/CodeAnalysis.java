package org.improver.magpiebridge.analysis;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.improver.analysis.utils.Warning;
import org.extendj.ast.CompilationUnit;

import org.improver.magpiebridge.utils.Result;

public abstract class CodeAnalysis {
  private Collection<AnalysisResult> results = new HashSet<>();
  public void doAnalysis(CompilationUnit cu, URL url) {
    results.clear();
    for (Warning wm : getWarnings(cu)) {
      results.add(new Result(Kind.Diagnostic, wm.getPosition(), wm.getErrMsg(),
                             wm.getRelatedInfo(), getKind(), wm.getRepair(),
                             wm.getCode()));
    }
  }

  public Collection<AnalysisResult> getResult() { return results; }
  public abstract String getName();
  public DiagnosticSeverity getKind() { return DiagnosticSeverity.Warning; }

  protected abstract Set<Warning> getWarnings(CompilationUnit cu);
}
