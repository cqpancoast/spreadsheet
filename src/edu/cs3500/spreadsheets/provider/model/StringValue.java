package edu.cs3500.spreadsheets.provider.model;

import java.util.List;

import java.util.Objects;

/**
 * Represents a string value in a spreadsheet.
 */
public class StringValue implements Value {
  private final String value;

  /**
   * Constructs a new String with the given value.
   * @param value the String contained in this value.
   */
  public StringValue(String value) {
    if (value == null) {
      throw new IllegalArgumentException("NULL VALUE IN DOUBLE VALUE");
    }

    this.value = value;
  }

  @Override
  public <R> R accept(Function<List<Value>, R> func) throws IllegalStateException {
    return func.visitString(this.value);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof StringValue) {
      StringValue s = (StringValue)other;
      return s.value.equals(this.value);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return this.value;
  }
}
