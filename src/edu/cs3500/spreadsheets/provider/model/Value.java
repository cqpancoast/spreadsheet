package edu.cs3500.spreadsheets.provider.model;

import java.util.List;

/**
 * Represents a value that a cell can hold.
 */
public interface Value {
  /**
   * Converts this into its String form.
   * @return the content of this value as a String.
   */
  public String toString();

  /**
   * Accept the given function, and call it appropriate method for this value.
   * @param func is the function to apply to this value.
   * @param <R> is the return type of that function.
   * @return The value after applying the function to this value.
   * @throws IllegalStateException if the function cannot be applied to this value.
   */
  <R> R accept(Function<List<Value>, R> func) throws IllegalStateException;

  /**
   * Determines whether this Value is equal to the given object.
   * @param other the object to compare with.
   * @return if the two objects are equal.
   */
  public boolean equals(Object other);
}
