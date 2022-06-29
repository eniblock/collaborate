package collaborate.api.tag.config;

import java.util.Set;

public class TagConfigFeature {

  public static final TagConfig TAG_CONFIG = new TagConfig(
      Set.of("https://ithacanet.ecadinfra.com"),
      Set.of(
          new TezosIndexer("tzstats", "https://api.ithaca.tzstats.com/"),
          new TezosIndexer("tzkt", "https://api.ithacanet.tzkt.io/")
      )
  );
}
