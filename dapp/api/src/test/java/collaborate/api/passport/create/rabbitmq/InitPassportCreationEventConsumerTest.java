package collaborate.api.passport.create.rabbitmq;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.mail.MailDTO;
import collaborate.api.mail.MailService;
import collaborate.api.tag.model.job.TransactionsEventMessage;
import collaborate.api.tag.model.job.TransactionsEventParameters;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.Optional;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InitPassportCreationEventConsumerTest {

  @Mock
  private MailService mailService;
  @Mock
  private TagUserDAO tagUserDAO;

  @InjectMocks
  private InitPassportCreationEventConsumer initPassportCreationEventConsumer;

  @Test
  void listenPassportCreate_should_send_mail_when_receive_message_for_this_organisation()
      throws MessagingException {
    //GIVEN
    UserWalletDTO mockedWallet = initWallet();

    when(tagUserDAO.getOrganizationAccountAddress()).thenReturn(mockedWallet.getAddress());
    when(tagUserDAO.findOneByAddress(anyString())).thenReturn(Optional.of(mockedWallet));
    Mockito.doNothing().when(mailService).sendMail(any(MailDTO.class), anyString(), anyString());

    //WHEN
    TransactionsEventMessage<InitPassportCreationValue> receivedMessage = initMessage();
    initPassportCreationEventConsumer.listenPassportCreate(receivedMessage);
    //THEN
    verify(mailService, times(1))
        .sendMail(any(MailDTO.class), anyString(), anyString());
  }

  @Test
  void listenPassportCreate_should_not_send_mail_when_receive_message_for_other_organisation()
      throws MessagingException {
    //GIVEN
    UserWalletDTO mockedWallet = initWallet();

    when(tagUserDAO.getOrganizationAccountAddress()).thenReturn(mockedWallet.getAddress());
    //WHEN
    TransactionsEventMessage<InitPassportCreationValue> receivedMessage = initForeignMessage();
    initPassportCreationEventConsumer.listenPassportCreate(receivedMessage);
    //THEN
    verify(mailService, times(0))
        .sendMail(any(MailDTO.class), anyString(), anyString());
  }

  private TransactionsEventMessage<InitPassportCreationValue> initMessage() {
    InitPassportCreationParams passportParams = InitPassportCreationParams.builder()
        .vehicleOwnerAddress("ownerAddress")
        .dspAddress("tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG")
        .vin("vin")
        .datasourceUUID("UUID")
        .build();

    InitPassportCreationValue initPassportCreationValue = InitPassportCreationValue.builder()
        .initPassportCreation(passportParams)
        .build();

    TransactionsEventParameters<InitPassportCreationValue> messageParams = new TransactionsEventParameters<>();
    messageParams.setEntrypoint("initPassportCreation");
    messageParams.setValue(initPassportCreationValue);

    TransactionsEventMessage<InitPassportCreationValue> mockMessage =
        new TransactionsEventMessage<>();
    mockMessage.setEntrypoint("initPassportCreation");
    mockMessage.setContractAddress("KT1NheTPU2A2wSL8UsHmd8xwTDhe1x9Xn7Xf");
    mockMessage.setParameters(messageParams);
    return mockMessage;
  }

  private TransactionsEventMessage<InitPassportCreationValue> initForeignMessage() {
    InitPassportCreationParams passportParams = InitPassportCreationParams.builder()
        .vehicleOwnerAddress("otherAddress")
        .dspAddress("otheDspAddress")
        .vin("vin")
        .datasourceUUID("UUID")
        .build();

    InitPassportCreationValue initPassportCreationValue = InitPassportCreationValue.builder()
        .initPassportCreation(passportParams)
        .build();

    TransactionsEventParameters<InitPassportCreationValue> messageParams = new TransactionsEventParameters<>();
    messageParams.setEntrypoint("initPassportCreation");
    messageParams.setValue(initPassportCreationValue);

    TransactionsEventMessage<InitPassportCreationValue> mockMessage =
        new TransactionsEventMessage<>();
    mockMessage.setEntrypoint("initPassportCreation");
    mockMessage.setContractAddress("KT1NheTPU2A2wSL8UsHmd8xwTDhe1x9Xn7Xf");
    mockMessage.setParameters(messageParams);
    return mockMessage;
  }

  private UserWalletDTO initWallet() {
    return UserWalletDTO.builder()
        .userId("admin")
        .address("tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG")
        .build();
  }

}