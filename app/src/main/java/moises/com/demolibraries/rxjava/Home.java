package moises.com.demolibraries.rxjava;


public class Home {
    private String status;
    private String label;

    public Home(String status, String label){
        this.status = status;
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Home{" +
                "status='" + status + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
