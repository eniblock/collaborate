package collaborate.api.ipfs.domain;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LinkType {
  DIRECTORY(1),
  FILE(2);

  private final int value;

  LinkType(int value) {
    this.value = value;
  }

  @JsonCreator
  public static LinkType getByValue(int value) {
    for (final LinkType oneLinkType : LinkType.values()) {
      if (oneLinkType.value == value) {
        return oneLinkType;
      }
    }
    throw new IllegalStateException(format("value=%d can't be deserialized", value));
  }
}
