package io.spotnext.instrumentation.internal;

// @NotThreadSafe
/**
 * <p>AgentClassLoaderReference class.</p>
 *
 * @since 1.0
 */
public final class AgentClassLoaderReference {

    private static ClassLoader agentClassLoader;

    private AgentClassLoaderReference() {}

    /**
     * <p>Getter for the field <code>agentClassLoader</code>.</p>
     *
     * @return a {@link java.lang.ClassLoader} object.
     */
    public static ClassLoader getAgentClassLoader() {
        final ClassLoader classLoader = agentClassLoader;
        AgentClassLoaderReference.agentClassLoader = null;
        return classLoader;
    }

    /**
     * <p>Setter for the field <code>agentClassLoader</code>.</p>
     *
     * @param agentClassLoader a {@link java.lang.ClassLoader} object.
     */
    public static void setAgentClassLoader(final ClassLoader agentClassLoader) {
        AgentClassLoaderReference.agentClassLoader = agentClassLoader;
    }
}
