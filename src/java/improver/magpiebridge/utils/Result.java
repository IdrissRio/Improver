/* Copyright (c) 2022, Idriss Riouak, riouakidriss@hotmail.it
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.improver.magpiebridge.utils;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;

public class Result implements AnalysisResult {

  private final Kind kind;
  private final Position position;
  private final String message;
  private final Iterable<Pair<Position, String>> related;
  private final DiagnosticSeverity severity;
  private final Pair<Position, String> repair;
  private final String code;

  public Result(Kind kind, Position pos, String msg,
                Iterable<Pair<Position, String>> relatedInfo,
                DiagnosticSeverity severity, Pair<Position, String> repair,
                String code) {
    this.kind = kind;
    this.position = pos;
    this.message = msg;
    this.related = relatedInfo;
    this.severity = severity;
    this.repair = repair;
    this.code = code;
  }

  public Kind kind() { return this.kind; }

  public Position position() { return position; }

  public Iterable<Pair<Position, String>> related() { return related; }

  public DiagnosticSeverity severity() { return severity; }

  public Pair<Position, String> repair() { return repair; }

  public String toString(boolean useMarkdown) { return message; }

  @Override // For Debugging
  public String toString() {
    return "Result [kind=" + kind + ", position=" + position +
        ", code=" + code + ", message=" + message + ", related=" + related +
        ", severity=" + severity + ", repair=" + repair + "]";
  }

  @Override
  public String code() {
    return code;
  }
}
