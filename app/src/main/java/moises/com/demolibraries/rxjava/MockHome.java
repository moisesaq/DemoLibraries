package moises.com.demolibraries.rxjava;

import java.util.ArrayList;
import java.util.List;

public class MockHome {

    private List<Home> homes;
    private int counter;
    private static MockHome mockHome;

    private MockHome(){
        counter = 0;
        setUpMock();
    }

    public static MockHome getInstance(){
        if (mockHome == null)
            mockHome = new MockHome();
        return mockHome;
    }

    private void setUpMock(){
        homes = new ArrayList<>();
        homes.add(new Home("running", "Moises Home"));
        homes.add(new Home("running", "Francesca Home"));
    }

    public List<Home> getHomes(){
        return homes;
    }

    public void resetCounter(){
        counter = 0;
    }

    public void increaseCounter(){
        counter++;
        if (counter == 3)
            homes.get(0).setStatus("finished");

        if (counter == 6)
            homes.get(1).setStatus("finished");
    }

    public int getCounter() {
        return counter;
    }
}
