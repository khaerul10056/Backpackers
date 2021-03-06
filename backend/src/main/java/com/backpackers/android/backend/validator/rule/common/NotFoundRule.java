package com.backpackers.android.backend.validator.rule.common;

import com.google.api.server.spi.response.NotFoundException;

import com.backpackers.android.backend.service.OfyHelper;
import com.googlecode.objectify.Key;
import com.backpackers.android.backend.validator.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class NotFoundRule implements Rule<NotFoundException> {

    private static final Logger logger =
            Logger.getLogger(NotFoundRule.class.getName());

    private List<String> websafeEntityIds = new ArrayList<>(2);

    public NotFoundRule(String websafeEntityId) {
        this.websafeEntityIds.add(websafeEntityId);
    }

    public NotFoundRule(String... websafeEntityIds) {
        this.websafeEntityIds.addAll(Arrays.asList(websafeEntityIds));
    }

    @Override
    public void validate() throws NotFoundException {
        for (String websafeEntityId : websafeEntityIds) {
            Key<?> entityKey = Key.create(websafeEntityId);

            try {
                OfyHelper.ofy().load().kind(entityKey.getKind()).filter("__key__ =", entityKey)
                        .keys().first().safe();
            } catch (com.googlecode.objectify.NotFoundException e) {
                throw new NotFoundException("Could not find " +
                        entityKey.getKind().toLowerCase() + " with ID: " + websafeEntityId);
            }
        }
    }
}
