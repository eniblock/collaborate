package collaborate.api.tag.model.proxytokencontroller;

/**
 * FIXME Please update this class declaration using (complete if needed)
 *
 * @JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, visible = true, property = "entryPoint")
 * @JsonSubTypes({
 * @JsonSubTypes.Type(value = MultisigBuildCallParamMint.class, name = "mint"), })
 * <p>
 * Check also which field could be move from concrete implementation to this abstract class
 */
public abstract class MultisigBuildCallParam {

}
