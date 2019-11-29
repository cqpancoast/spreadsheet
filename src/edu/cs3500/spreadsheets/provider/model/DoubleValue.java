package edu.cs3500.spreadsheets.provider.model;

import java.util.List;

import java.util.Objects;

/**
 * Represents a double value in a cell.
 */
public class DoubleValue implements Value {

  private Double value;

  /**
   * Constructs a new DoubleValue.
   * @param value the Double to be contained.
   */
  public DoubleValue(Double value) {
    if (value == null) {
      throw new IllegalArgumentException("NULL VALUE IN DOUBLE VALUE");
    }

    this.value = value;
  }

  @Override
  public <R> R accept(Function<List<Value>, R> func) throws IllegalStateException {
    return func.visitDouble(this.value);
  }

  /**
   * Returns this Value as a Double.
   * @return this Value as a Double.
   */
  public Double getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof  DoubleValue) {
      DoubleValue temp = (DoubleValue)other;
      return this.value.equals(temp.value);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return String.format("%f", this.value);
  }
}
