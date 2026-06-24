package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.Country;
import io.github.nanamochi.chio.model.enums.Permissions;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPresence {
  private boolean isIrc;
  private int timezone;
  private int countryIndex;
  private Permissions permissions;
  private double longitude;
  private double latitude;
  private String city;

  public String getCountryName() {
    return Country.getCountryNames().get(countryIndex);
  }

  public String getCountryAcronym() {
    return Country.getCountryAcronyms().get(countryIndex);
  }

  public String getLocation() {
    if (!getCity().isBlank()) return getCountryName() + " / " + getCity();
    return getCountryName();
  }
}
