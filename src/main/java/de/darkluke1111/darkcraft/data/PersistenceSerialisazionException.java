package de.darkluke1111.darkcraft.data;

public class PersistenceSerialisazionException extends Exception {

  PersistenceSerialisazionException(String message, Throwable cause) {
    super(message, cause);
  }

  PersistenceSerialisazionException(String message) {
    super(message);
  }
}
