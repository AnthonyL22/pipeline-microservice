package com.pwc.notification

/**
 * Send email to a group from a Pipeline
 * @param subject Email subject
 * @param body Email body
 * @param recipients Email recipients
 */
def sendEmailNotification(subject, body, recipients) {

    try {

        mail subject: subject,
                body: body,
                to: recipients,
                replyTo: recipients,
                from: recipients

    } catch (Exception exception) {
        return "sendEmail - Unstable"
    }

}