package edu.cs3500.spreadsheets.provider.model;

/**
 * Temp class created to make up for not receiving model class from providers.
 * @param <U>  in
 * @param <V>  out
 */
public interface Function<U, V> {

  V visitDouble(Double arg);

  V visitString(String value);

  V visitBoolean(boolean value);
}
