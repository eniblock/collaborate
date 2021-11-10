package collaborate.api.helper;

import static org.junit.Assert.assertEquals;

import collaborate.api.mail.MailDTO;
import collaborate.api.user.SetRolesNotificationEmailHelper;
import org.junit.Test;

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
        "DSPConsortium1"
    );

    assertEquals("Your roles have been modified", mailWithFullName.getSubject());
    assertEquals("from@mail.com", mailWithFullName.getFrom());
    assertEquals("to@mail.com", mailWithFullName.getTo());
    assertEquals("<p>Hello<b> Alice Wu</b>,</p>", mailWithFullName.getGreeting());
    assertEquals(mailWithFullName.getContent(),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Identity Provider Admin, Operator.");

    String[] rolesWithUnexpectedRole = {"idpAdmins", "service_provider_operator"};
    MailDTO mailWithFirstname = util.buildRolesSetNotificationEmail(
        "from@mail.com",
        "to@mail.com",
        "Alice",
        null,
        rolesWithUnexpectedRole,
        "DSPConsortium1"
    );

    assertEquals("Your roles have been modified", mailWithFirstname.getSubject());
    assertEquals("from@mail.com", mailWithFirstname.getFrom());
    assertEquals("to@mail.com", mailWithFirstname.getTo());
    assertEquals("<p>Hello<b> Alice</b>,</p>", mailWithFirstname.getGreeting());
    assertEquals(mailWithFirstname.getContent(),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Operator.");

    MailDTO mailWithLastName = util.buildRolesSetNotificationEmail(
        "from@mail.com",
        "to@mail.com",
        null,
        "Wu",
        rolesWithUnexpectedRole,
        "DSPConsortium1"
    );

    assertEquals("Your roles have been modified", mailWithLastName.getSubject());
    assertEquals("from@mail.com", mailWithLastName.getFrom());
    assertEquals("to@mail.com", mailWithLastName.getTo());
    assertEquals("<p>Hello<b> Wu</b>,</p>", mailWithLastName.getGreeting());
    assertEquals(mailWithLastName.getContent(),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Operator.");

    MailDTO mailWithNoName = util.buildRolesSetNotificationEmail(
        "from@mail.com",
        "to@mail.com",
        null,
        null,
        roles,
        "DSPConsortium1"
    );

    assertEquals("Your roles have been modified", mailWithNoName.getSubject());
    assertEquals("from@mail.com", mailWithNoName.getFrom());
    assertEquals("to@mail.com", mailWithNoName.getTo());
    assertEquals("<p>Hello<b></b>,</p>", mailWithNoName.getGreeting());
    assertEquals(mailWithNoName.getContent(),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Identity Provider Admin, Operator.");

    MailDTO mailWithNoRoles = util.buildRolesSetNotificationEmail(
        "from@mail.com",
        "to@mail.com",
        null,
        null,
        new String[0],
        "DSPConsortium1"
    );

    assertEquals("Your roles have been modified", mailWithNoRoles.getSubject());
    assertEquals("from@mail.com", mailWithNoRoles.getFrom());
    assertEquals("to@mail.com", mailWithNoRoles.getTo());
    assertEquals("<p>Hello<b></b>,</p>", mailWithNoRoles.getGreeting());
    assertEquals(mailWithNoRoles.getContent(),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have no roles.");
  }
}
