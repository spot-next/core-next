package io.spotnext.instrumentation.internal;

import java.lang.reflect.InvocationTargetException;

// @Immutable
/**
 * <p>DynamicInstrumentationLoadAgentMain class.</p>
 *
 * @since 1.0
 */
public final class DynamicInstrumentationLoadAgentMain {

    private DynamicInstrumentationLoadAgentMain() {}

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(final String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: " + DynamicInstrumentationLoadAgentMain.class.getSimpleName()
                    + " <pid> <agentJarAbsolutePath>");
        }
        final String pid = args[0];
        final String agentJarAbsolutePath = args[1];
        loadAgent(pid, agentJarAbsolutePath);
    }

    /**
     * <p>loadAgent.</p>
     *
     * @param pid a {@link java.lang.String} object.
     * @param agentJarAbsolutePath a {@link java.lang.String} object.
     */
    public static void loadAgent(final String pid, final String agentJarAbsolutePath) {
        //use reflection since tools.jar has been added to the classpath dynamically
        try {
            final Class<?> vmClass = Class.forName("com.sun.tools.attach.VirtualMachine");
            final Object vm = vmClass.getMethod("attach", String.class).invoke(null, pid);
            vmClass.getMethod("loadAgent", String.class).invoke(vm, agentJarAbsolutePath);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
