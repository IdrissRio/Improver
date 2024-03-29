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
package org.improver.magpiebridge;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import java.io.*;
import java.io.File;
import java.net.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import magpiebridge.core.AnalysisConsumer;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.analysis.configuration.ConfigurationOption;
import magpiebridge.core.analysis.configuration.OptionType;
import magpiebridge.projectservice.java.JavaProjectService;
import org.improver.frontend.Improver;
import org.improver.magpiebridge.analysis.CodeAnalysis;
import org.extendj.magpiebridge.analysis.*;


public class ImproverServer implements ServerAnalysis {

  public Set<Path> srcPath = null;
  public Set<Path> libPath = null;
  public Set<Path> classPath = null;
  public Optional<Path> rootPath = null;

  public ExecutorService exeService;
  public Collection<Future<?>> last;
  

  public Improver framework = Improver.getInstance();

  public Map<CodeAnalysis, Boolean> activeAnalyses;

  public ImproverServer() {
    exeService = Executors.newSingleThreadExecutor();
    activeAnalyses = new LinkedHashMap<CodeAnalysis, Boolean>();
    // Add your analysis here
    activeAnalyses.put(new StringEqAnalysis(), true);
    last = new ArrayList<>(activeAnalyses.size());
  }

  @Override
  public String source() {
    return "Improver";
  }

  @Override
  public void analyze(Collection<? extends Module> files,
                      AnalysisConsumer consumer, boolean rerun) {
    MagpieServer server = (MagpieServer)consumer;
    JavaProjectService ps =
        (JavaProjectService)server.getProjectService("java").get();
    if (classPath == null || libPath == null || rootPath == null ||
        srcPath == null) {
      classPath = ps.getClassPath();
      libPath = ps.getLibraryPath();
      rootPath = ps.getRootPath();
      srcPath = ps.getSourcePath();
    }
    System.err.println("Running analysis");
    // Setup analysis framework and run
    framework.setup(files, srcPath, classPath, libPath, rootPath);
    if (rerun) {
      doSingleAnalysisIteration(files, consumer);
    }
  }

  private boolean doSingleAnalysisIteration(Collection<? extends Module> files,
                                            AnalysisConsumer consumer) {
    MagpieServer server = (MagpieServer)consumer;
    // Construct the AST
    int exitCode = framework.run();
    System.err.println("Improver finished with exit code " + exitCode);

    // Clean up previous analysis results and ongoing analyses
    server.cleanUp();
    for (Future<?> f : last) {
      if (f != null && !f.isDone()) {
        f.cancel(true);
      }
    }
    last.clear();

    // Initiate analyses on seperate threads and set them running
    for (CodeAnalysis analysis : activeAnalyses.keySet()) {
      if (!activeAnalyses.get(analysis))
        continue;
      last.add(exeService.submit(new Runnable() {
        @Override
        public void run() {
          doAnalysisThread(files, server, analysis);
        }
      }));
    }
    return true;
  }

  public void doAnalysisThread(Collection<? extends Module> files,
                               MagpieServer server, CodeAnalysis analysis) {
    Collection<AnalysisResult> results = new ArrayList<>();
    for (Module file : files) {
      if (file instanceof SourceFileModule) {
        SourceFileModule sourceFile = (SourceFileModule)file;
        try {
          final URL clientURL =
              new URL(server.getClientUri(sourceFile.getURL().toString()));
          results.addAll(framework.analyze(sourceFile, clientURL, analysis));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      server.consume(results, source());
    }
  }

  @Override
  public List<ConfigurationOption> getConfigurationOptions() {
    List<ConfigurationOption> options = new ArrayList<>();
    ConfigurationOption analyses =
        new ConfigurationOption("Analyses", OptionType.container);
    for (CodeAnalysis a : activeAnalyses.keySet()) {
      analyses.addChild(
          new ConfigurationOption(a.getName(), OptionType.checkbox, "true"));
    }
    options.add(analyses);
    return options;
  }

  @Override
  public void configure(List<ConfigurationOption> configuration) {
    for (ConfigurationOption o : configuration) {
      if (o.getName().equals("Analyses")) {
        for (ConfigurationOption c : o.getChildren()) {
          for (CodeAnalysis a : activeAnalyses.keySet()) {
            if (a.getName().equals(c.getName())) {
              activeAnalyses.put(a, c.getValueAsBoolean());
            }
          }
        }
      }
    }
  }
}
