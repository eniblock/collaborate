package collaborate.catalog.service;

import collaborate.catalog.domain.Data;
import collaborate.catalog.repository.DataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataServiceTest {
    @InjectMocks
    DataService dataService;

    @Mock
    private DataRepository dataRepository;

    @Test
    void create() {
        Data data = new Data();

        dataService.create(data);

        verify(dataRepository, times(1)).save(data);
    }
}