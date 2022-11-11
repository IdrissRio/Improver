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

import beaver.Symbol;

import com.ibm.wala.classLoader.IMethod.SourcePosition;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.improver.frontend.Improver;
import org.extendj.ast.ASTNode;

public class Pos implements com.ibm.wala.cast.tree.CAstSourcePositionMap.Position {
  private int firstOffset;
  private int lastOffset;
  private int firstLine;
  private int lastLine;
  private int firstCol;
  private int lastCol;

  private URL urlToSourceFile;
  private String srcPath;

  public Pos(int firstLine, int lastLine, int firstCol, int lastCol,
                    String sourcePath) {
    this.firstOffset = Symbol.makePosition(firstLine, firstCol);
    this.lastOffset = Symbol.makePosition(lastLine, lastCol);

    this.firstLine = firstLine;
    this.lastLine = lastLine;

    this.firstCol = firstCol;
    this.lastCol = lastCol;
    try {
      urlToSourceFile =
          new URL(Improver.getInstance().getServer().getClientUri("file:" + sourcePath));
    } catch (MalformedURLException e) {
      // e.printStackTrace();
    }
  }

  @Override
  public int getFirstLine() {
    return firstLine;
  }

  @Override
  public int getLastLine() {
    return lastLine;
  }

  @Override
  public int getFirstCol() {
    return firstCol;
  }

  @Override
  public int getLastCol() {
    return lastCol;
  }

  @Override
  public int getFirstOffset() {
    return firstOffset;
  }

  @Override
  public int getLastOffset() {
    return lastOffset;
  }

  @Override
  public URL getURL() {
    return urlToSourceFile;
  }

  @Override
  public int compareTo(SourcePosition o) {
    return o.getFirstOffset() - firstOffset;
  }

  @Override
  public Reader getReader() throws IOException {
    return new FileReader(new File(srcPath));
  }
}
