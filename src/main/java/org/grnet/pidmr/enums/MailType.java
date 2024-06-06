package org.grnet.pidmr.enums;

import io.quarkus.qute.Template;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public enum MailType {


    ADMIN_ALERT_NEW_CHANGE_ROLE_REQUEST() {
        public MailTemplate execute(Template emailTemplate, HashMap<String, String> templateParams) {
            String body = emailTemplate
                    .data("id", templateParams.get("id"))
                    .data("image", templateParams.get("image"))
                    .data("image1", templateParams.get("image1"))
                    .data("image2", templateParams.get("image2"))
                    .data("image3", templateParams.get("image3"))
                    .data("image4", templateParams.get("image4"))
                    .data("pidmr", templateParams.get("pidmr"))
                    .data("userrole", templateParams.get("userrole"))
                    .data("contactMail", templateParams.get("contactMail"))
                    .render();
            return new MailTemplate("["+templateParams.get("title")+"] - Change Role Request Created with id: "+templateParams.get("id"), body);
        }
    },

    USER_ALERT_CHANGE_ROLE_REQUEST_STATUS {
        public MailTemplate execute(Template emailTemplate, HashMap<String, String> templateParams) {
             String body = emailTemplate
                    .data("id", templateParams.get("id"))
                    .data("image", templateParams.get("image"))
                    .data("image1", templateParams.get("image1"))
                    .data("image2", templateParams.get("image2"))
                    .data("image3", templateParams.get("image3"))
                    .data("image4", templateParams.get("image4"))
                    .data("pidmr", templateParams.get("pidmr"))
                    .data("userrole", templateParams.get("userrole"))
                    .data("contactMail", templateParams.get("contactMail"))
                    .data("status", templateParams.get("status")).render();
            return new MailTemplate("["+templateParams.get("title")+"] - Change Role request updated status with id: "+templateParams.get("id"), body);
        }

    },
    USER_ROLE_CHANGE_REQUEST_CREATION {
        public MailTemplate execute(Template emailTemplate, HashMap<String, String> templateParams) {
            String body = emailTemplate
                    .data("image", templateParams.get("image"))
                    .data("image1", templateParams.get("image1"))
                    .data("image2", templateParams.get("image2"))
                    .data("image3", templateParams.get("image3"))
                    .data("image4", templateParams.get("image4"))
                    .data("pidmr", templateParams.get("pidmr"))
                    .data("userrole", templateParams.get("userrole"))
                    .data("contactMail", templateParams.get("contactMail"))
                    .render();
            return new MailTemplate("["+templateParams.get("title")+"] - Change Role Request Created with id: "+templateParams.get("id"), body);
        }

    };

    public abstract MailTemplate execute(Template mailTemplate, HashMap<String, String> templateParams);


    public class MailTemplate {
        String subject;
        String body;

        public MailTemplate(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}

