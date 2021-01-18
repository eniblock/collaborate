package collaborate.api.services;

import collaborate.api.domain.Datasource;
import org.springframework.stereotype.Service;

@Service
public class DatasourceService {
    public boolean testConnection(Datasource datasource) {

        return true;
    }
}