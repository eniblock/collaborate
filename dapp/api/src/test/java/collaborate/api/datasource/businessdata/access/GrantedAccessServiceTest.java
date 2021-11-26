package collaborate.api.datasource.businessdata.access;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.test.TestResources;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrantedAccessServiceTest {

  ObjectMapper objectMapper = Mockito.spy(TestResources.objectMapper);
  @InjectMocks
  GrantedAccessService grantedAccessService;

  @Test
  void getAccessGrantParams_shouldResultInExpectedDeserializedAccessGrantParams() {
    // GIVEN
    var transaction = readContent(
        "/datasource/businessdata/access/grant/grant_access-transaction.json",
        Transaction.class
    );
    // WHEN
    var accessRequestResult = grantedAccessService.getAccessGrantParams(transaction);
    // THEN
    assertThat(accessRequestResult)
        .isEqualTo(
            AccessGrantParams.builder()
                .accessRequestsUuid(UUID.fromString("05b108f5-1f4c-448b-bf97-8571a2a1e5dc"))
                .cipheredToken(
                    "JuwSSBpkI9pEqZgN9PGFhOEobc8MAiWBn5xJcRUFxr5iZL7MOio5YceniBWqZ4PP+KY6DpP1KQOWODtM9KLgsTjFizKIddY+zPDtDd7LE7XlHApcCV47rVDlYkUbR2rwOxBHKn01q0VONpqUkzPyWBxObea90iNlEPZAT/zLgXKog5n0yG0A3bujV4q6cS3f1BP9izl0JOqeaX+6JxyWOI8rDM1OBcMPAGHODPmD/mr0iZ3YwDmEkM1CP4NbVURDYWSf8CvhwbP8rFflnVJmDt5JQnZDkZD80JqXo22ys2LPZX8JH8EsrRlxZm285ktQ/9lXlgXePsEjribRMSrJaw== TPmyWH1ql7HrMX01JKEfkZZ2cjxUZ9dOazDnfdooBCtbk36WYGLmnMtdngPnLemF9BG1JxE4CkTvCSTsytARHDhSVzhU0AFNiMP2c3x94LHIVSFoa7liPYMgDJKMO4DqkFE4YmOR00Y9w/Rf12gvBH3u751jEQWy1JHzidWIeg38GJNMv3vZ4HtRcp+iiWW4fGa8kXwMA/H8+fiUj/8WqqxeQZ8Yk9QgVwZEWhVPiMcTXdrXKbaGuFM/y6k0gF51a9ror1oOzHSUB9NcaeJPtIqNsMHv10GWOlCAQk9vzYXutGh4e8rkit9KbF/cWYut00pHJeCC072saYkCWt3/TZR5lbwQZySHcgCqv4g1akT9Ys0Vl21AiyDnVz4y1Cucp1QN+Jbn1UWYZWwSF4XXvMOPIJEt69Nlgs4Xv8rRwo4mxCVqw6QH1jF/gQQwVDnsStHpRT7fEIYYLi/n5t6NB+fDEBA9hlPXtJ+q2Q9QTUXl7P5YaKdMwJ31ecdKyAQesx5/JxQ/e/aKzruvBYr7Z/NuWgaKQ3qhyIA14JDpMLSlWkYh+4LQ9mUBCzpCt8CQWBRU008tuGevKv0nIDEZhiyXrd9eDrUL0iQ0UhMSLTaHVFpsxwfLbEOsEl8ZuTN7M/ZdaLgoBoud3hrdu7vDK7x6kQVWKR0ct5LsfYD/LqbGyd0fg4D8bGdDHC012LpOdavks9fKJxb7F2eTpU2pYdr2dmQwES933HdzGULz0Qvh/Zfy1/y+h6PcvOe3B9GTfp01tmYGFBXdqd+nLJezkD99woldk+5OZhHJR5b3cn3fLlnoNM7rRU4hycCzwHeizAsgmwcK7+oH18aap8E/66voJgH+103bkBP1bsRC3voo6pfyIPCduIE+9AD6HB1KKUKNerxl7ucy6rdyCiEsbQ6dd6sgAsJpMWhmNcdfhu94ff2Pl/dm+Emy6PR8yvmMrWRq/1nO3n8bs6zm5P1WVIMwgFOFv+5lDqWjcASN4jOqW1P79AGb7iiDm431n0juJAHvXMmLUppe3e+v0RS3zN2rGnIMpGSBmjd2YlE/IIN+aRfyUe0dzPjZOAi1GHeRvIx+vDzGuyM9zAOBSZc4DI6yIRN23hivaPC9NNNzGKG3uUD8MJHn+WcPHCR1nXmFtbV80aDPRRN0K8GVDjREACJqQ6kYJpOGicq4tNZIfxshKOIUQFU1bDA88Jq+dv6pB4qv1R2P2GuHFBJekfoR3xuZCi/4VAuBdv3IbLMeKOEu06RbDCoJnLbjroGULwGwBsXVutzOUqbW9JJsReb6vX/O8roi0RRnoWYiat353PwC+0rZC7awObHdve6awUfrh/CmS2LkIwevJ3ckkE6uFRfiFauTQvgNz8RM2g8F7f5Q4uNu/xVqya7kVn74zLsj84RItNDMs56vvEMp2k/tSVEvBHvybBzQV/5/NtbYO2w9GXN5Z6h5lEmn3QMd1ZwfYAX+S9ts/10txFCJjakrxYtdf0pAkj3E0rb5")
                .requesterAddress("tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG")
                .build()
        );
  }

}
