package com.pwc.git

/**
 * Get all GitHub branch names for the active GitHub project
 * @return space-seperated list of active branches
 */
def getGitBranchNames() {

    try {

        def branches = sh(script: 'git branch -r', returnStdout: true).trim()
        return branches

    } catch (Exception exception) {
        return "getGitBranchNames - Unstable"
    }

}