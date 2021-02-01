package collaborate.api.helper;

import collaborate.api.services.dto.MailDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetRolesNotificationEmailHelperTest {
    @Test
    public void buildRolesSetNotificationEmailTest() {
        SetRolesNotificationEmailHelper util = new SetRolesNotificationEmailHelper("idpAdmin");
        String[] roles = {"idpAdmin", "service_provider_operator"};

        MailDTO mailWithFullName = util.buildRolesSetNotificationEmail(
            "from@mail.com",
            "to@mail.com",
            "Alice",
            "Wu",
            roles,
            "PSA"
        );

        assertEquals(mailWithFullName.getSubject(), "Your roles have been modified");
        assertEquals(mailWithFullName.getFrom(), "from@mail.com");
        assertEquals(mailWithFullName.getTo(), "to@mail.com");
        assertEquals(mailWithFullName.getGreeting(), "<p>Hello<b> Alice Wu</b>,</p>");
        assertEquals(mailWithFullName.getContent(), "Your account has been modified by <b>PSA administrator</b>. " +
                "You now have the following role(s): Identity Provider Admin, Operator.");

        String[] rolesWithUnexpectedRole = {"idpAdmins", "service_provider_operator"};
        MailDTO mailWithFirstname = util.buildRolesSetNotificationEmail(
                "from@mail.com",
                "to@mail.com",
                "Alice",
                null,
                rolesWithUnexpectedRole,
                "PSA"
        );

        assertEquals(mailWithFirstname.getSubject(), "Your roles have been modified");
        assertEquals(mailWithFirstname.getFrom(), "from@mail.com");
        assertEquals(mailWithFirstname.getTo(), "to@mail.com");
        assertEquals(mailWithFirstname.getGreeting(), "<p>Hello<b> Alice</b>,</p>");
        assertEquals(mailWithFirstname.getContent(), "Your account has been modified by <b>PSA administrator</b>. " +
                "You now have the following role(s): Operator.");

        MailDTO mailWithLastName = util.buildRolesSetNotificationEmail(
                "from@mail.com",
                "to@mail.com",
                null,
                "Wu",
                rolesWithUnexpectedRole,
                "PSA"
        );

        assertEquals(mailWithLastName.getSubject(), "Your roles have been modified");
        assertEquals(mailWithLastName.getFrom(), "from@mail.com");
        assertEquals(mailWithLastName.getTo(), "to@mail.com");
        assertEquals(mailWithLastName.getGreeting(), "<p>Hello<b> Wu</b>,</p>");
        assertEquals(mailWithLastName.getContent(), "Your account has been modified by <b>PSA administrator</b>. " +
                "You now have the following role(s): Operator.");

        MailDTO mailWithNoName = util.buildRolesSetNotificationEmail(
                "from@mail.com",
                "to@mail.com",
                null,
                null,
                roles,
                "PSA"
        );

        assertEquals(mailWithNoName.getSubject(), "Your roles have been modified");
        assertEquals(mailWithNoName.getFrom(), "from@mail.com");
        assertEquals(mailWithNoName.getTo(), "to@mail.com");
        assertEquals(mailWithNoName.getGreeting(), "<p>Hello<b></b>,</p>");
        assertEquals(mailWithNoName.getContent(), "Your account has been modified by <b>PSA administrator</b>. " +
                "You now have the following role(s): Identity Provider Admin, Operator.");

        MailDTO mailWithNoRoles = util.buildRolesSetNotificationEmail(
                "from@mail.com",
                "to@mail.com",
                null,
                null,
                new String[0],
                "PSA"
        );

        assertEquals(mailWithNoRoles.getSubject(), "Your roles have been modified");
        assertEquals(mailWithNoRoles.getFrom(), "from@mail.com");
        assertEquals(mailWithNoRoles.getTo(), "to@mail.com");
        assertEquals(mailWithNoRoles.getGreeting(), "<p>Hello<b></b>,</p>");
        assertEquals(mailWithNoRoles.getContent(), "Your account has been modified by <b>PSA administrator</b>. " +
                "You now have no roles.");
    }
}
