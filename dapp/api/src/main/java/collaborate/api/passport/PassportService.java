package collaborate.api.passport;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;
import static collaborate.api.user.security.Authorizations.Roles.SERVICE_PROVIDER_ADMIN;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.passport.create.CreatePassportDAO;
import collaborate.api.passport.create.CreatePassportDTO;
import collaborate.api.passport.find.DigitalPassportDTO;
import collaborate.api.passport.find.FindPassportDAO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.security.ConnectedUserDAO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PassportService {

  private final ConnectedUserDAO connectedUserDAO;
  private final CreatePassportDAO createPassportDAO;
  private final FindPassportDAO findPassportDAO;
  private final TagUserDAO tagUserDAO;

  public Job create(CreatePassportDTO createPassportDTO) {
    return createPassportDAO.create(createPassportDTO);
  }

  public Collection<DigitalPassportDTO> getByVehicleOwner(String vehicleOwnerEmail) {
    var voAccountAddress = tagUserDAO.findOneByUserId(vehicleOwnerEmail)
        .map(UserWalletDTO::getAddress)
        .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR,
            "No vehicleOwner account found for current user=" + vehicleOwnerEmail));
    var passportIds = findPassportDAO.findPassportsIdByVo(voAccountAddress);
    return findPassportDAO.findPassportsByIds(passportIds);
  }

  public Collection<DigitalPassportDTO> getByCurrentDsp() {
    var dspAccountAddress = tagUserDAO.getOrganizationAccountAddress();
    return getByDsp(dspAccountAddress);
  }

  public Collection<DigitalPassportDTO> getByDsp(String dspPublicHashKey) {
    var passportIds = findPassportDAO.findPassportsIdByDsp(dspPublicHashKey);
    return findPassportDAO.findPassportsByIds(passportIds);
  }

  public Collection<DigitalPassportDTO> getByConnectedUser() {
    Collection<DigitalPassportDTO> digitalPassports;

    Set<String> roles = connectedUserDAO.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      digitalPassports = getByVehicleOwner(connectedUserDAO.getEmailOrThrow());
    } else if (roles.contains(SERVICE_PROVIDER_ADMIN)) {
      digitalPassports = getByCurrentDsp();
    } else {
      throw new ResponseStatusException(FORBIDDEN,
          "User not allowed for roles=" + String.join(",", roles));
    }

    return digitalPassports;
  }
}
