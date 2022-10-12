package collaborate.api.helper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import collaborate.api.mail.EMailDTO;
import collaborate.api.user.SetRolesNotificationEmailHelper;
import org.junit.jupiter.api.Test;

class SetRolesNotificationEmailHelperTest {

  @Test
  void buildRolesSetNotificationEmailTest() {
    SetRolesNotificationEmailHelper util = new SetRolesNotificationEmailHelper("idpAdmin");
    String[] roles = {"idpAdmin", "service_provider_operator"};

    EMailDTO mailWithFullName = util.buildRolesSetNotificationEmail(
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
    assertEquals("<p>Hello<b> Alice Wu</b>,</p>",
        mailWithFullName.getContextVariable("greeting"));
    assertEquals(mailWithFullName.getContextVariable("content"),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Identity Provider Admin, Operator.");

    String[] rolesWithUnexpectedRole = {"idpAdmins", "service_provider_operator"};
    EMailDTO mailWithFirstname = util.buildRolesSetNotificationEmail(
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
    assertEquals("<p>Hello<b> Alice</b>,</p>", mailWithFirstname.getContextVariable("greeting"));
    assertEquals(mailWithFirstname.getContextVariable("content"),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Operator.");

    EMailDTO mailWithLastName = util.buildRolesSetNotificationEmail(
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
    assertEquals("<p>Hello<b> Wu</b>,</p>", mailWithLastName.getContextVariable("greeting"));
    assertEquals(mailWithLastName.getContextVariable("content"),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Operator.");

    EMailDTO mailWithNoName = util.buildRolesSetNotificationEmail(
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
    assertEquals("<p>Hello<b></b>,</p>", mailWithNoName.getContextVariable("greeting"));
    assertEquals(mailWithNoName.getContextVariable("content"),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have the following role(s): Identity Provider Admin, Operator.");

    EMailDTO mailWithNoRoles = util.buildRolesSetNotificationEmail(
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
    assertEquals("<p>Hello<b></b>,</p>", mailWithNoRoles.getContextVariable("greeting"));
    assertEquals(mailWithNoRoles.getContextVariable("content"),
        "Your account has been modified by <b>DSPConsortium1 administrator</b>. " +
            "You now have no roles.");
  }
}
