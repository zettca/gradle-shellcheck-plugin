package com.felipefzdz.gradle.shellcheck;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.VerificationTask;
import org.gradle.util.ClosureBackedAction;

import javax.inject.Inject;
import java.io.File;

@CacheableTask
public class Shellcheck extends ConventionTask implements VerificationTask, Reporting<ShellcheckReports> {

    private FileCollection sources;

    private final ShellcheckReports reports;
    private boolean showViolations = true;
    private boolean ignoreFailures = false;
    private boolean useDocker = true;
    private String shellcheckVersion;
    private String severity;
    private String shellcheckBinary;
    private String installer;
    private File projectDir;

    public Shellcheck() {
        this.reports = (ShellcheckReports) getObjectFactory().newInstance(ShellcheckReportsImpl.class, this);
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    public void run() {
        ShellcheckInvoker.invoke(this);
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileCollection getSources() {
        return sources;
    }

    public void setSources(FileCollection sources) {
        this.sources = sources;
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

    @Input
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Internal
    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }

    @Input
    public boolean isUseDocker() {
        return useDocker;
    }

    public void setUseDocker(boolean useDocker) {
        this.useDocker = useDocker;
    }

    @Input
    public String getShellcheckBinary() {
        return shellcheckBinary;
    }

    public void setShellcheckBinary(String shellcheckBinary) {
        this.shellcheckBinary = shellcheckBinary;
    }

    @Input
    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }
}
