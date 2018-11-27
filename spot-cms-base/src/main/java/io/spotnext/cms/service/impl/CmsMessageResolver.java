package io.spotnext.cms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import io.spotnext.core.infrastructure.service.L10nService;

@Service
public class CmsMessageResolver implements IMessageResolver {

    @Autowired
    protected L10nService l10nService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Integer getOrder() {
        return 0;
    }

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return l10nService.getMessage(key, null, messageParameters);
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key,
            Object[] messageParameters) {

        return "[" + key + "]";
    }

}