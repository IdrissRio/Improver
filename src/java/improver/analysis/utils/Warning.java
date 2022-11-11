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

package org.improver.analysis.utils;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import magpiebridge.util.SourceCodeReader;
import org.improver.magpiebridge.utils.Pos;



public class Warning implements Comparable<Warning> {
  private final Integer lineStart, columnStart, lineEnd, columnEnd;
  private final String errMsg;
  final Analysis.AvailableAnalysis analysis;
  final String sourceFilePath;
  private final Pair<Position, String> repair;
  private final java.util.List<Pair<Position, String>> relatedInfo;
  private org.improver.magpiebridge.utils.Pos position = null;
  private String code = "Error...";

  public Warning(String sourceFilePath, Analysis.AvailableAnalysis analysis,
                 Integer lineStart, Integer columnStart, Integer lineEnd,
                 Integer columnEnd, String errMsg,
                 Pair<Position, String> repair,
                 java.util.List<Pair<Position, String>> relatedInfo) {
    this.analysis = analysis;
    this.lineStart = lineStart;
    this.lineEnd = lineEnd;
    this.errMsg = errMsg;
    this.sourceFilePath = sourceFilePath;
    this.columnEnd = columnEnd;
    this.columnStart = columnStart;
    this.repair = repair;
    this.relatedInfo = relatedInfo;

    try{
        this.position = new Pos(lineStart, lineEnd, columnStart,
                                       columnEnd, sourceFilePath);
        this.code = SourceCodeReader.getLinesInString(position);
    }catch(Exception e){
        System.out.println("Error in Warning.java: " + e);
    }
  }

  public int compareTo(Warning other) {
    if (lineStart.equals(other.lineStart)) {
      if (columnStart != null) {
        if (columnStart.equals(other.columnStart))
          return errMsg.compareTo(other.errMsg);
        else {
          if (columnStart != null && other.columnStart != null)
            return Integer.compare(columnStart, other.columnStart);
          else {
            if (columnStart == null) {
              return -1;
            } else {
              return 1;
            }
          }
        }
      } else {
        return Integer.compare(columnEnd, other.columnEnd);
      }
    } else {
      return Integer.compare(lineStart, other.lineStart);
    }
  }

  public Integer getLineStart() { return lineStart; }

  public Integer getColumnStart() { return columnStart; }

  public Integer getLineEnd() { return lineEnd; }

  public Integer getColumnEnd() { return columnEnd; }

  public String getErrMsg() { return errMsg; }

  public Pair<Position, String> getRepair() { return repair; }

  public java.util.List<Pair<Position, String>> getRelatedInfo() {
    return relatedInfo;
  }

  public Pos getPosition() { return position; }

  public String getCode() { return code; }

  public String getSourceFilePath() { return sourceFilePath; }


  public String toString() {
    return analysis.name() + " - " + sourceFilePath + ":" + lineStart + "," +
        columnStart + " - " + errMsg;
  }

  public Analysis.AvailableAnalysis getAnalysisType() { return analysis; }
}
