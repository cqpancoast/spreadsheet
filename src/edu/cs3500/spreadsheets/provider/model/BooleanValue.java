package edu.cs3500.spreadsheets.provider.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a boolean value in a cell.
 */
public class BooleanValue implements Value {

  private final boolean value;

  /**
   * Constructs a new BooleanValue that contains the boolean.
   * @param value the value of this BooleanValue.
   */
  public BooleanValue(boolean value) {
    this.value = value;
  }

  @Override
  public <R> R accept(Function<List<Value>, R> func) throws IllegalStateException {
    return func.visitBoolean(this.value);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof BooleanValue) {
      BooleanValue b = (BooleanValue)other;
      return b.value == this.value;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return Boolean.toString(this.value);
  }
}
