package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.nanamochi.objects.enums.Country;
import pe.nanamochi.objects.enums.Permissions;

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

  public String getLocation() {
    if (!this.getCity().isBlank()) {
      return this.getCountryName() + " / " + this.getCity();
    }
    return this.getCountryName();
  }

  public String getCountryName() {
    return Country.getCountryNames().get(this.countryIndex);
  }

  public String getCountryAcronym() {
    return Country.getCountryAcronyms().get(this.countryIndex);
  }
}
