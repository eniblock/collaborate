package collaborate.api.ipfs;

import collaborate.api.ipfs.domain.Link;
import collaborate.api.ipfs.domain.LinkType;
import collaborate.api.ipfs.domain.LsObjectResponse;
import collaborate.api.ipfs.domain.LsResponse;
import collaborate.api.test.TestResources;
import java.util.List;
import lombok.Data;


@Data
public class IpfsFeature {

  public static final String ipfsLsResponseJson =
      TestResources.readPath("/ipfs/ipfs.ls.response.json");

  public static final LsResponse ipfsLsResponse = new LsResponse(
      List.of(
          new LsObjectResponse(
              "QmfW9oprYv4uyQNpocKgpH9G8hzEvA54LbPamabY2khDAn",
              List.of(
                  Link.builder()
                      .name("directory")
                      .hash("QmWX3FmaAsjYEag4kk33pWQ7aPWnz3sKnsscpWbrHEow71")
                      .type(LinkType.DIRECTORY)
                      .target("")
                      .build(),
                  Link.builder()
                      .name("file")
                      .hash("Qma9CyFdG5ffrZCcYSin2uAETygB25cswVwEYYzwfQuhTe")
                      .type(LinkType.FILE)
                      .size(6)
                      .target("")
                      .build()
              )
          )
      )
  );

}
