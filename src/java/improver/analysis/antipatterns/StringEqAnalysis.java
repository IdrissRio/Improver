import java.util.Set;
import org.improver.analysis.utils.Warning;
import org.improver.analysis.utils.Analysis;
import org.improver.magpiebridge.analysis.CodeAnalysis;
import org.extendj.ast.CompilationUnit; 

public class StringEqAnalysis extends CodeAnalysis {
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.STREQ(); }

  public String getName() { return "StringEqAnalysis"; }
}