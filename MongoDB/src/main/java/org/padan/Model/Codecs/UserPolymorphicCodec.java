package org.padan.Model.Codecs;

import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.padan.Model.Objects.RegularUser;
import org.padan.Model.Objects.StudentUser;
import org.padan.Model.Objects.TrainerUser;
import org.padan.Model.Objects.User;

public class UserPolymorphicCodec implements Codec<User> {
    private final CodecRegistry registry;
    private final Codec<BsonDocument> docCodec = new BsonDocumentCodec();

    public UserPolymorphicCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public User decode(BsonReader reader, DecoderContext ctx) {
        // Read the whole subdocument first
        BsonDocument doc = docCodec.decode(reader, ctx);

        String kind = doc.getString("clazz", new BsonString("")).getValue();
        Class<? extends User> target =
                switch (kind) {
                    case "student" -> StudentUser.class;
                    case "regular" -> RegularUser.class;
                    case "trainer" -> TrainerUser.class;
                    case "user"    -> User.class; // rarely used, but keeps it explicit
                    default -> throw new CodecConfigurationException(
                            "Unknown discriminator 'clazz': " + kind);
                };

        @SuppressWarnings("unchecked")
        Codec<? extends User> delegate = registry.get(target);
        return delegate.decode(new BsonDocumentReader(doc), ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(BsonWriter writer, User value, EncoderContext ctx) {
        // Delegate to the concrete subclass codec
        Codec<User> delegate = (Codec<User>) registry.get(value.getClass());
        delegate.encode(writer, value, ctx);
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}
