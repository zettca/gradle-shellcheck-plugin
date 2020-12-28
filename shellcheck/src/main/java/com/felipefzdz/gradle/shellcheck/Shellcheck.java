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

@CacheableTask
public class Shellcheck extends ConventionTask implements VerificationTask, Reporting<ShellcheckReports> {

    private File source;

    private final ShellcheckReports reports;
    private boolean showViolations = true;
    private boolean ignoreFailures = false;

    private String shellcheckVersion;

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

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
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
     */
    @Override
    public ShellcheckReports reports(Action<? super ShellcheckReports> configureAction) {
        configureAction.execute(reports);
        return reports;
    }

    /**
     * Whether rule violations are to be displayed on the console.
     *
     * @return true if violations should be displayed on console
     */
    @Console
    public boolean isShowViolations() {
        return showViolations;
    }

    /**
     * Whether rule violations are to be displayed on the console.
     *
     * @param showViolations
     */
    public void setShowViolations(boolean showViolations) {
        this.showViolations = showViolations;
    }

    @Input
    public String getShellcheckVersion() {
        return shellcheckVersion;
    }

    public void setShellcheckVersion(String shellcheckVersion) {
        this.shellcheckVersion = shellcheckVersion;
    }

    @Override
    public void setIgnoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures;
    }

    @Internal
    public boolean isIgnoreFailures() {
        return ignoreFailures;
    }

    @Override
    public boolean getIgnoreFailures() {
        return ignoreFailures;
    }
}
