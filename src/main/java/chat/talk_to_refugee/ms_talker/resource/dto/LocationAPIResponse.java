package chat.talk_to_refugee.ms_talker.resource.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationAPIResponse {

    @JsonProperty(value = "nome")
    private String currentlyCity;

    @JsonProperty(value = "microrregiao")
    private Microregion microregion;

    public String getCurrentlyCity() {
        return currentlyCity;
    }

    public void setCurrentlyCity(String currentlyCity) {
        this.currentlyCity = currentlyCity;
    }

    public Microregion getMicroregion() {
        return microregion;
    }

    public void setMicroregion(Microregion microregion) {
        this.microregion = microregion;
    }

    public static class Microregion {

        @JsonProperty(value = "mesorregiao")
        private Mesoregion mesoregion;

        public Mesoregion getMesoregion() {
            return mesoregion;
        }

        public void setMesoregion(Mesoregion mesoregion) {
            this.mesoregion = mesoregion;
        }

        public static class Mesoregion {

            @JsonProperty(value = "UF")
            private UF uf;

            public UF getUf() {
                return uf;
            }

            public void setUf(UF uf) {
                this.uf = uf;
            }

            public static class UF {

                @JsonProperty(value = "sigla")
                private String currentlyState;

                public String getCurrentlyState() {
                    return currentlyState;
                }

                public void setCurrentlyState(String currentlyState) {
                    this.currentlyState = currentlyState;
                }
            }
        }
    }
}
