package com.pwc.nexus

/**
 *
 * Get all versions from a nexus repository for a groupId and artifactId
 * @param baseUrl base URL to query
 * @param repositoryName repository name
 * @param groupId
 * @param artifactId
 * @return new-line separated list of version numbers
 */
def getNexusVersionsByRepo(baseUrl, repositoryName, groupId, artifactId) {

    def allVersions = ""
    try {

        def groupName = groupId.replace(".", "/")
        def rawXml = sh script: "curl -X GET -K ${baseUrl}/${repositoryName}/content/${groupName}/${artifactId}/maven-metadata.xml", returnStdout: true
        def startParseTag = "<version>"
        def endParseTag = "</version>"
        int i = 0

        while (rawXml.indexOf(startParseTag) > 0) {
            int start = rawXml.indexOf(startParseTag) + startParseTag.length()
            int end = rawXml.indexOf(endParseTag)
            def version = rawXml.substring(start, end)
            allVersions = version + "\n" + version
            rawXml = rawXml.substring(end + 1)
            i++
        }

    } catch (Exception exception) {
        return "getNexusVersionsByRepo - Unstable"
    }

    return allVersions

}