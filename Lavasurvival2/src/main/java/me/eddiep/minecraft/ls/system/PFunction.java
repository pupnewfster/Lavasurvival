package me.eddiep.minecraft.ls.system;

/**
 * Represents a function that takes in a single value and returns another value
 * @param <ARG> The type of the parameter
 * @param <RETURN> The type of the return value
 */
public interface PFunction<ARG, RETURN> {
    RETURN run(ARG val);
}
