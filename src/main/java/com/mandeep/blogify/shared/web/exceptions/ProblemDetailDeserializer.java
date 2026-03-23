package com.mandeep.blogify.shared.web.exceptions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

// Since for problem detail we have two different implementation, while testing jackson will be confused to know which implementation to use, so we will explicitly tell this one to use

public class ProblemDetailDeserializer extends StdDeserializer<ProblemDetailFormat> {

    protected ProblemDetailDeserializer() {
        super(ProblemDetailFormat.class);
    }

    @Override
    public ProblemDetailFormat deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        // check if this is AppValidationProblemDetail, for this will see if there is violations field is present or not
        boolean isValidation = node.has("violations") && !node.get("violations").isNull();

        if (isValidation) {
            return p.getCodec().treeToValue(node, AppValidationProblemDetail.class);
        }

        return p.getCodec().treeToValue(node, AppProblemDetail.class);
    }
}
