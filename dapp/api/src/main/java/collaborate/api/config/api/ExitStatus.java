package collaborate.api.config.api;

import lombok.Getter;

@Getter
public enum ExitStatus {

  INIT_NTF_DATASOURCE_ROOT_FOLDER(1),
  INIT_NTF_METADATA_ROOT_FOLDER(2),
  INIT_ASSET_DATA_CATALOG_ROOT_FOLDER(3);

  private final int code;

  ExitStatus(int code) {
    this.code = code;
  }
}
