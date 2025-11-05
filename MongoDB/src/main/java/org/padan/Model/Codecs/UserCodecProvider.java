package org.padan.Model.Codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.padan.Model.Objects.User;

public class UserCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == User.class) {
            return (Codec<T>) new UserPolymorphicCodec(registry);
        }
        return null;
    }
}
