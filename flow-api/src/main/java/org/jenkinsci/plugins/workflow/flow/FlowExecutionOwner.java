/*
 * The MIT License
 *
 * Copyright (c) 2013-2014, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.workflow.flow;

import hudson.model.Queue.Executable;
import hudson.model.Run;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * We need something that's serializable in small moniker that helps us find THE instance
 * of {@link FlowExecution}.
 *
 * @author Jesse Glick
 * @author Kohsuke Kawaguchi
 */
// this gets implemented by whoever that owns FlowExecution, like WorfklowRun
public abstract class FlowExecutionOwner implements Serializable {
    /**
     * @throws IOException
     *      if fails to find {@link FlowExecution}.
     */
    @Nonnull
    public abstract FlowExecution get() throws IOException;

    /**
     * A directory (on the master) where information may be persisted.
     * @see Run#getRootDir
     */
    public abstract File getRootDir() throws IOException;

    /**
     * The executor slot running this flow, such as a {@link Run}.
     * The conceptual "owner" of {@link FlowExecution}.
     *
     * (For anything that runs for a long enough time that demands flow, it better occupies an executor.
     * So this type restriction should still enable scriptler to use this.)
     */
    public abstract Executable getExecutable() throws IOException;

    /**
     * Returns the stream where the output from the flow execution would go.
     *
     * <p>
     * Scripting languages often have ability to write to standard output. Returning a value
     * here would allow those output to the location that the owner controls.
     *
     * <p>
     * If the host has no suitable place to send such output to, just return {@code System.out}
     *
     * TODO: on 2nd thought this is broken, in that logs are supposed to be divided per FlowNode.
     * So the question for me is that if the script decides to print some output at random point in
     * execution, where should I send it to?
     */
    public abstract PrintStream getConsole();

    /**
     * Returns the URL of {@link FlowExecution}, relative to the context root of Jenkins.
     *
     * @return
     *      String like "/job/foo/32/execution" with leading slash but no trailing slash.
     */
    public abstract String getUrl();
}
