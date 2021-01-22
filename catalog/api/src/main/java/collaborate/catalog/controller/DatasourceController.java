package collaborate.catalog.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("datasources")
public class DatasourceController {

    @DeleteMapping("{id}/data")
    public void delete(@PathVariable("id") Long id) {

    }
}
