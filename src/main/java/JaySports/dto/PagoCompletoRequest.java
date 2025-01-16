package JaySports.dto;


public class PagoCompletoRequest {

    private PagoData pagoData;
    private TarjetaPagoData tarjetaPagoData;

    public PagoCompletoRequest() {
    }

    public PagoCompletoRequest(PagoData pagoData, TarjetaPagoData tarjetaPagoData) {
        this.pagoData = pagoData;
        this.tarjetaPagoData = tarjetaPagoData;
    }

    public PagoData getPagoData() {
        return pagoData;
    }

    public void setPagoData(PagoData pagoData) {
        this.pagoData = pagoData;
    }

    public TarjetaPagoData getTarjetaPagoData() {
        return tarjetaPagoData;
    }

    public void setTarjetaPagoData(TarjetaPagoData tarjetaPagoData) {
        this.tarjetaPagoData = tarjetaPagoData;
    }

}


