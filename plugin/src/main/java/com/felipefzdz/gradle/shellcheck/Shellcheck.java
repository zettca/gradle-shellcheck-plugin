package com.felipefzdz.gradle.shellcheck;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.tasks.*;
import org.gradle.util.ClosureBackedAction;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class Shellcheck extends ConventionTask implements VerificationTask, Reporting<ShellcheckReports> {

    private File shellScripts;

    private final ShellcheckReports reports;

    public Shellcheck() {
        this.reports = getObjectFactory().newInstance(ShellcheckReportsImpl.class, this);
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    public void run() throws IOException, InterruptedException, TransformerException, ParserConfigurationException, SAXException {
        ShellcheckInvoker.invoke(this);
    }

    @Override
    public void setIgnoreFailures(boolean ignoreFailures) {
    }

    @Override
    public boolean getIgnoreFailures() {
        return false;
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public File getShellScripts() {
        return shellScripts;
    }

    public void setShellScripts(File shellScripts) {
        this.shellScripts = shellScripts;
    }

    /**
     * The reports to be generated by this task.
     */
    @Override
    @Nested
    public final ShellcheckReports getReports() {
        return reports;

    }

    /**
     * Configures the reports to be generated by this task.
     * <p>
     * The contained reports can be configured by name and closures. Example:
     *
     * <pre>
     * shellcheck {
     *   reports {
     *     html {
     *       destination "build/shellcheck.html"
     *     }
     *   }
     * }
     * </pre>
     *
     * @param closure The configuration
     * @return The reports container
     */
    @Override
    public ShellcheckReports reports(@DelegatesTo(value = ShellcheckReports.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        return reports(new ClosureBackedAction<>(closure));
    }

    /**
     * Configures the reports to be generated by this task.
     * <p>
     * The contained reports can be configured by name and closures. Example:
     *
     * <pre>
     * shellcheck {
     *   reports {
     *     html {
     *       destination "build/shellcheck.html"
     *     }
     *   }
     * }
     * </pre>
     *
     * @param configureAction The configuration
     * @return The reports container
     * @since 3.0
     */
    @Override
    public ShellcheckReports reports(Action<? super ShellcheckReports> configureAction) {
        configureAction.execute(reports);
        return reports;
    }
}
