package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.SlaveFs

class PhaseContext extends AbstractContext {
    protected final Item item

    String phaseName
    String continuationCondition
    String executionType
    Boolean enableGroovyScript
    Boolean isUseScriptFile
    Boolean isScriptOnSlave
    String scriptText
    String scriptPath
    String bindings
    Boolean isRunOnSlave
    String ignorePhaseResult

    List<PhaseJobContext> jobsInPhase = []

    PhaseContext(JobManagement jobManagement, Item item, String phaseName, String continuationCondition,
                 String executionType, Boolean enableGroovyScript, String scriptText, Boolean isUseScriptFile,
                 String scriptPath, String bindings, Boolean isScriptOnSlave, Boolean isRunOnSlave, String ignorePhaseResult) {
        super(jobManagement)
        this.item = item
        this.phaseName = phaseName
        this.continuationCondition = continuationCondition
        this.executionType = executionType
        this.enableGroovyScript = enableGroovyScript
        this.scriptText = scriptText
        this.isUseScriptFile = isUseScriptFile
        this.scriptPath = scriptPath
        this.bindings = bindings
        this.isScriptOnSlave = isScriptOnSlave
        this.isRunOnSlave = isRunOnSlave
        this.ignorePhaseResult = ignorePhaseResult
    }

    /**
     * Defines the name of the MultiJob phase.
     */
    void phaseName(String phaseName) {
        this.phaseName = phaseName
    }

    /**
     * Defines how to decide the status of the whole MultiJob phase.
     */
    void continuationCondition(String continuationCondition) {
        this.continuationCondition = continuationCondition
    }

    /**
     * Defines how to run jobs in a phase: parallel or sequentially
     */
    void executionType(String executionType) {
        this.executionType = executionType
    }

    /**
     * Define how to decide the status of the whole MultiJob Project, based on the status of current phase.
     * @param ignorePhaseResult
     */
    void ignorePhaseResult(String ignorePhaseResult) {
        this.ignorePhaseResult = ignorePhaseResult
    }

    void enableGroovyScript(boolean enableGroovyScript) {
        this.enableGroovyScript = enableGroovyScript
    }

    /**
     * Defines where's run groovy script (master or slave)
     * @param isRunOnSlave
     */
    void runGroovyOnSlave(boolean isRunOnSlave) {
        this.isRunOnSlave = isRunOnSlave
    }

    /**
     * Defines groovy script
     * @param source
     * @param script
     */
    void groovyScript(String source, String script) {
        if (null == enableGroovyScript) {
            this.enableGroovyScript = true
        }
        if (null == isRunOnSlave) {
            this.isRunOnSlave = false
        }
        if ('FILE' == source) {
            this.scriptPath = script
            this.isUseScriptFile = true
        } else if ('SCRIPT' == source) {
            this.scriptText = script
            this.isUseScriptFile = false
        } else {
            this.enableGroovyScript = false
            this.isUseScriptFile = false
        }
    }

    /**
     * Defines groovy script
     * @param source
     * @param slaveFs
     */
    void groovyScript(String source, SlaveFs slaveFs) {
        if (null == enableGroovyScript) {
            this.enableGroovyScript = true
        }
        if ('FILE' == source) {
            this.scriptPath = slaveFs.path
            this.isUseScriptFile = true
            this.isScriptOnSlave = true
        } else {
            this.enableGroovyScript = false
            this.isUseScriptFile = false
            this.isScriptOnSlave = false
        }
    }

    SlaveFs slaveFs(String path) {
        new SlaveFs(path)
    }

    void bindVar(String key, String value) {
        this.bindings.concat(key).concat('=').concat(value).concat('\n')
    }

    void bindVarMap(Map<String, String> map) {
        map.each { k, v ->
            this.bindings.concat(k).concat('=').concat(v).concat('\n')
        }
    }

    /**
     * Adds a job to the phase.
     *
     * @since 1.39
     */
    void phaseJob(String jobName, @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        PhaseJobContext phaseJobContext = new PhaseJobContext(jobManagement, item, jobName)
        ContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext
    }
}
