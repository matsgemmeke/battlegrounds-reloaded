package nl.matsgemmeke.battlegrounds.i18n.message;

import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

/**
 * A message factory that creates message domain objects from message specifications.
 *
 * @param <T> the type of specification the factory takes as input
 */
public interface MessageFactory<T extends MessageSpec> {

    /**
     * Creates a message object from the given specification and tag resolvers.
     *
     * @param spec         the message specification
     * @param replacements the placeholder replacements
     * @return             a message object with applied tag resolvers
     */
    Message create(T spec, Replacement... replacements);
}
