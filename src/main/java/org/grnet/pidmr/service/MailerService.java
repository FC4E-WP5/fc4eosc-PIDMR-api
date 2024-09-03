package org.grnet.pidmr.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.EmailContextForStatusUpdate;
import org.grnet.pidmr.enums.MailType;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The UserService provides operations for managing User entities.
 */

@ApplicationScoped
@ActivateRequestContext
public class MailerService {
    @Inject
    Mailer mailer;

    @Inject
    UserService userService;

    @Inject
    KeycloakAdminService keycloakAdminService;

    @ConfigProperty(name = "api.ui.url")
    String uiBaseUrl;
    @ConfigProperty(name = "api.server.url")
    String serviceUrl;

    @ConfigProperty(name = "quarkus.smallrye-openapi.info-contact-email")
    String contactMail;
    @Inject
    @Location("user_change_role_request_created.html")
    Template userChangeRoleRequestCreatedTemplate;

    @Inject
    @Location("user_alert_updated_role_request_status.html")
    Template userChangeRoleRequestStatusUpdateTemplate;

    @Inject
    @Location("admin_new_change_role_request.html")
    Template adminNewChangeRoleTemplate;

    @Inject
    @Location("admin_new_pid_type_entry_request.html")
    Template adminNewPidTypeEntryRequestTemplate;
    @Inject
    @Location("provider_admin_alert_updated_pid_type_entry_status.html")
    Template providerAdminAlertUpdatedPidTypeEntryStatusTemplate;

    @Inject
    @Location("provider_admin_new_pid_type_entry_created.html")
    Template providerAdminPidTypeEntryCreatedTemplate;

    private static final Logger LOG = Logger.getLogger(MailerService.class);

    @ConfigProperty(name = "api.keycloak.user.id")
    String attribute;

    @ConfigProperty(name = "api.name")
    String apiName;

    public List<String> retrieveAdminEmails() {

        var admins = keycloakAdminService.fetchRolesMembers("admin");
        var vopersonIds = admins.stream().map(admin -> admin.getAttributes().get(attribute)).flatMap(Collection::stream).collect(Collectors.toList());
        var users = userService.getUsers();

        return users.stream()
                .filter(user -> vopersonIds.contains(user.id))
                .map(adminU -> adminU.email)
                .collect(Collectors.toList());
    }

    private Template getTemplate(MailType type) {
        switch (type) {
            case ADMIN_ALERT_NEW_CHANGE_ROLE_REQUEST:
                return adminNewChangeRoleTemplate;
            case USER_ALERT_CHANGE_ROLE_REQUEST_STATUS:
                return userChangeRoleRequestStatusUpdateTemplate;
            case USER_ROLE_CHANGE_REQUEST_CREATION:
                return userChangeRoleRequestCreatedTemplate;
            case ADMIN_ALERT_NEW_PID_TYPE_ENTRY_CREATION:
                return adminNewPidTypeEntryRequestTemplate;
            case PROVIDER_ADMIN_NEW_PID_TYPE_ENTRY_CREATION:
                return providerAdminPidTypeEntryCreatedTemplate;
            case PROVIDER_ADMIN_ALERT_CHANGE_PID_TYPE_ENTRY_REQUEST_STATUS:
                return providerAdminAlertUpdatedPidTypeEntryStatusTemplate;
            default:
                throw new IllegalArgumentException("Unsupported mail type");
        }
    }


    @Transactional
    public void sendEmails(EmailContextForStatusUpdate emailContext, MailType type, List<String> mailAddrs) {

        HashMap<String, String> templateParams = new HashMap<>();
        templateParams.put("contactMail", contactMail);
        templateParams.put("image", serviceUrl + "/v1/images/logo.png");
        templateParams.put("image1", serviceUrl + "/v1/images/logo-dans.png");
        templateParams.put("image2", serviceUrl + "/v1/images/logo-grnet.png");
        templateParams.put("image3", serviceUrl + "/v1/images/logo-datacite.png");
        templateParams.put("image4", serviceUrl + "/v1/images/logo-gwdg.png");
        templateParams.put("pidmr", uiBaseUrl);
        templateParams.put("title", apiName.toUpperCase());

        templateParams.put("id", String.valueOf(emailContext.getRequestID()));
        templateParams.put("status", emailContext.getRequestStatus());

        switch (type) {
            case ADMIN_ALERT_NEW_CHANGE_ROLE_REQUEST:
                templateParams.put("userrole", "Administrator");
                templateParams.put("urlpath", uiBaseUrl + "/user-role-requests/");
                notifyAdmins(adminNewChangeRoleTemplate, templateParams, mailAddrs, type);
                break;
            case USER_ROLE_CHANGE_REQUEST_CREATION:
                templateParams.put("userrole", "User");
                notifyUser(userChangeRoleRequestCreatedTemplate, templateParams, mailAddrs, type);
                break;
            case USER_ALERT_CHANGE_ROLE_REQUEST_STATUS:
                templateParams.put("userrole", "User");
                notifyUser(userChangeRoleRequestStatusUpdateTemplate, templateParams, Collections.singletonList(emailContext.getEmail()), type);
                break;
            case ADMIN_ALERT_NEW_PID_TYPE_ENTRY_CREATION:
                templateParams.put("userrole", "Administrator");
                templateParams.put("urlpath", uiBaseUrl + "/managed-pids/");
                templateParams.put("usermail", emailContext.getEmail());
                templateParams.put("pidtype", emailContext.getPidType());
                templateParams.put("timestamp", emailContext.getTimestamp());
                notifyAdmins(adminNewPidTypeEntryRequestTemplate, templateParams, mailAddrs, type);
                break;
            case PROVIDER_ADMIN_ALERT_CHANGE_PID_TYPE_ENTRY_REQUEST_STATUS:
                templateParams.put("userrole", "User");
                templateParams.put("urlpath", uiBaseUrl + "/managed-pids/view/" + String.valueOf(emailContext.getRequestID()));
                templateParams.put("timestamp", emailContext.getTimestamp());
                templateParams.put("pidtype", emailContext.getPidType());
                notifyUser(providerAdminAlertUpdatedPidTypeEntryStatusTemplate, templateParams, mailAddrs, type);
                break;
            case PROVIDER_ADMIN_NEW_PID_TYPE_ENTRY_CREATION:
                templateParams.put("userrole", "User");
                templateParams.put("urlpath", uiBaseUrl + "/managed-pids/view/" + String.valueOf(emailContext.getRequestID()));
                templateParams.put("pidtype", emailContext.getPidType());
                templateParams.put("timestamp", emailContext.getTimestamp());
                notifyUser(providerAdminPidTypeEntryCreatedTemplate, templateParams, Collections.singletonList(emailContext.getEmail()), type);
                break;
            default:
                throw new IllegalArgumentException("Unsupported mail type");
        }
    }

    @Transactional
    public void sendEmailsWithContext(EmailContextForStatusUpdate emailContext, MailType adminType, MailType userType) {
        CompletableFuture.supplyAsync(this::retrieveAdminEmails)
                .thenAccept(adminEmails -> {

                    if (adminType != userType) {
                        sendEmails(emailContext, adminType, adminEmails);
                        if (!adminEmails.contains(emailContext.getEmail())) {
                            sendEmails(emailContext, userType, Collections.singletonList(emailContext.getEmail()));
                        }
                    } else {
                        sendEmails(emailContext, userType, Collections.singletonList(emailContext.getEmail()));
                    }

                });
    }

    public Mail buildEmail(Template emailTemplate, HashMap<String, String> templateParams, MailType mailType) {

        MailType.MailTemplate mailTemplate = mailType.execute(emailTemplate, templateParams);
        Mail mail = new Mail();
        mail.setHtml(mailTemplate.getBody());
        mail.setSubject(mailTemplate.getSubject());
        return mail;
    }

    private void notifyUser(Template emailTemplate, HashMap<String, String> templateParams, List<String> mailAddrs, MailType mailType) {

        var mail = buildEmail(emailTemplate, templateParams, mailType);
        mail.setBcc(mailAddrs);

        try {
            mailer.send(mail);
            LOG.info("RECIPIENTS : " + Arrays.toString(mail.getBcc().toArray()));
        } catch (Exception e) {
            LOG.error("Cannot send the email because of : " + e.getMessage());
        }
    }

    private void notifyAdmins(Template emailTemplate, HashMap<String, String> templateParams, List<String> mailAddrs, MailType mailType) {

        var mail = buildEmail(emailTemplate, templateParams, mailType);
        mail.setBcc(mailAddrs);
        try {
            LOG.info("EMAIL INFO " + "from: " + mail.getFrom() + " to: " + Arrays.toString(mail.getTo().toArray()) + " subject: " + mail.getSubject() + " message:" + mail.getText());
            mailer.send(mail);
            LOG.info("RECIPIENTS : " + Arrays.toString(mail.getBcc().toArray()));
        } catch (Exception e) {
            LOG.error("Cannot send the email because of : " + e.getMessage());
        }

    }

    public static class CustomCompletableFuture<T> extends CompletableFuture<T> {
        static final Executor EXEC = Executors.newCachedThreadPool();

        @Override
        public Executor defaultExecutor() {
            return EXEC;

        }

        @Override
        public <U> CompletableFuture<U> newIncompleteFuture() {
            return new CustomCompletableFuture<>();
        }


        public static CompletableFuture<Void> runAsync(Runnable runnable) {
            return supplyAsync(() -> {
                runnable.run();
                return null;
            });
        }

        public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
            return new CompletableFuture<U>().completeAsync(supplier);
        }

    }
}

