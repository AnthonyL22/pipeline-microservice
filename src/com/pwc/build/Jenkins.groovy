package com.pwc.build

/**
 * Execute a Jenkins job with build parameters
 * @param jenkinsUrl Jenkins url
 * @param user Jenkins user
 * @param timeoutInSeconds job execution timeout in seconds
 * @param jobName Jenkins job name
 * @param parameters build parameters map
 */
def runJenkinsJob(jenkinsUrl, user, timeoutInSeconds, jobName, parameters) {

    try {

        timeout(time: timeoutInSeconds, unit: 'SECONDS') {

            def parameterList = ""
            for (param in parameters) {

                if (parameterList.contains("?")) {
                    parameterList = parameterList + "&"
                } else {
                    parameterList = parameterList + "?"
                }

                if ("${param.key}".contains("-")) {
                    def customKey = "${param.key}".replace("-", "\\n")
                    parameterList = parameterList + customKey + "=${param.value}"
                } else {
                    parameterList = parameterList + "${param.key}=${param.value}"
                }

            }

            withCredentials([usernamePassword(credentialsId: "${user}", passwordVariable: 'BUILD_PASSWORD', usernameVariable: 'BUILD_USER')]) {
                sh script: "curl -X POST -k --user ${BUILD_USER}:${BUILD_PASSWORD} '${jenkinsUrl}/${jobName}/buildWithParameters${parameterList}'"
            }

        }


    } catch (Exception exception) {
        return "runJenkinsJob - Unstable"
    }

    retryUntilJobIsSuccessful(jenkinsUrl, user, jobName)

}

/**
 * Execute a Jenkins job without Build Parameters
 * @param jenkinsUrl Jenkins url
 * @param user Jenkins user
 * @param timeoutInSeconds job execution timeout in seconds
 * @param jobName Jenkins job name
 */
def runJenkinsJob(jenkinsUrl, user, timeoutInSeconds, jobName) {

    try {

        timeout(time: timeoutInSeconds, unit: 'SECONDS') {

            withCredentials([usernamePassword(credentialsId: "${user}", passwordVariable: 'BUILD_PASSWORD', usernameVariable: 'BUILD_USER')]) {
                sh script: "curl -X POST -k --user ${BUILD_USER}:${BUILD_PASSWORD} ${jenkinsUrl}/${jobName}/build?delay=0sec"
            }

        }


    } catch (Exception exception) {
        return "runJenkinsJob - Unstable"
    }

    retryUntilJobIsSuccessful(jenkinsUrl, user, jobName)

}

/**
 * Retry a Jenkins job to wait for success within 30 minutes
 * @param jenkinsUrl Jenkins url
 * @param user Jenkins user
 * @param jobName Jenkins job name
 */
def retryUntilJobIsSuccessful(jenkinsUrl, user, jobName) {

    try {

        timeout(time: 30, unit: 'MINUTES') {

            def lastResponse = ""
            while (!lastResponse.contains("\"result\":\"SUCCESS\"")) {

                withCredentials([usernamePassword(credentialsId: "${user}", passwordVariable: 'BUILD_PASSWORD', usernameVariable: 'BUILD_USER')]) {
                    lastResponse = sh script: "curl -X POST -k --user ${BUILD_USER}:${BUILD_PASSWORD} ${jenkinsUrl}/${jobName}/lastBuild/api/json", returnStdout: true
                }
                sleep(10)
            }

        }


    } catch (Exception exception) {
        return "retryUntilJobIsSuccessful - Unstable"
    }

}