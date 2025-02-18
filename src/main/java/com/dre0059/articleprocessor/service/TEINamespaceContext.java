package com.dre0059.articleprocessor.service;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class TEINamespaceContext implements NamespaceContext {
    @Override
    public String getNamespaceURI(String prefix) {
        if ("tei".equals(prefix)) {
            return "http://www.tei-c.org/ns/1.0";
        }
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) { return null; }
    @Override
    public Iterator<String> getPrefixes(String namespaceURI) { return null; }
}
