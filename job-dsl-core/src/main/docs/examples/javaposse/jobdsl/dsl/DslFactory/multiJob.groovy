multiJob('example') {
    steps {
        phase('Second') {
            phaseJob('JobZ') {
                groovyScript('FILE', slaveFs('/Users/sshelomentsev/example.groovy'))
                runGroovyOnSlave(true)
                resumeGroovyScript('FILE', slaveFs('/Users/sshelomentsev/example.groovy'))
                parameters {
                    propertiesFile('my1.properties')
                }
            }
        }
        phase('Third') {
            enableGroovyScript(true)
            groovyScript('FILE', slaveFs('/Users/sshelomentsev/example.groovy'))
            bindVar('AAA', '100')
            bindVarMap(new HashMap<String, String>() { {
                put('BBB', '200')
                put('CCC', '300')
            } })
            phaseJob('JobA')
            phaseJob('JobB')
            phaseJob('JobC')
        }
        phase('Fourth') {
            phaseJob('JobD') {
                currentJobParameters(false)
                enableGroovyScript(false)
                runJobScriptOnSlave(true)
                runResumeScriptOnSlave(true)
                groovyScript('FILE', slaveFs('/Users/sshelomentsev/example.groovy'))
                resumeCondition('EXPRESSION')
                resumeGroovyScript('FILE', '/Users/sshelomentsev/example.groovy')
                bindVar('RESUME', 'DDD', '100')
                bindVarMap('JOB', new HashMap<String, String>() { {
                    put('EEE', '200')
                    put('FFF', '300')
                } })
                parameters {
                    booleanParam('cParam', true)
                    propertiesFile('my.properties')
                    sameNode()
                    matrixSubset('it.name=="hello"')
                    subversionRevision()
                    gitRevision()
                    predefinedProp('prop1', 'value1')
                    nodeLabel('lParam', 'my_nodes')
                }
                configure { phaseJobConfig ->
                    phaseJobConfig / enableCondition << 'true'
                    phaseJobConfig / condition << '${RUN_JOB} == "true"'
                }
            }
        }
    }
}
