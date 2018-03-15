package hu.bme.aut.digikaland.utility.development;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import hu.bme.aut.digikaland.entities.Station;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.ui.client.activities.ClientHelpActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientStationsActivity;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;

public class MockGenerator {
    public static Bundle mockActualMain(){
        Bundle bundle = new Bundle();
        bundle.putString(ClientActualFragment.ARG_LOCATION, "Ez itt egy cím lesz");
        bundle.putString(ClientActualFragment.ARG_SUBLOCATION, "Ez itt a cím pontosítása lesz");
        bundle.putInt(ClientActualFragment.ARG_STATIONS, 10);
        bundle.putInt(ClientActualFragment.ARG_STATION_NUMBER, 7);
        Calendar c = new GregorianCalendar();
        c.set(2018,1,26,19,48);
        Date testTime = c.getTime();
        bundle.putLong(ClientActualFragment.ARG_TIME, testTime.getTime());
        return bundle;
    }

    public static Bundle mockMapData(){
        Bundle locationData = new Bundle();
        double latitudes[] = {47.473372 };
        double longitudes[] = {19.059731};
        locationData.putDoubleArray(MapsActivity.ARGS_LATITUDE, latitudes);
        locationData.putDoubleArray(MapsActivity.ARGS_LONGITUDE, longitudes);
        return locationData;
    }

    public static Bundle mockStationsList(){
        Bundle stationData = new Bundle();
        stationData.putSerializable(ClientStationsActivity.ARGS_STATIONS , stationListGenerator());
        return stationData;
    }

    public static ArrayList<Objective> mockMiniObjectiveList(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new PhysicalObjective("Ez csak egy picit kérdéssorozat."));
        return objectives;
    }

    public static ArrayList<Objective> mockBigObjectiveList(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new TrueFalseObjective("A BME-t 1782-ben alapították. Igaz vagy hamis?"));
        String answers[] = {"6", "7", "8", "9"};
        objectives.add(new MultipleChoiceObjective("Hány kar található a BME-n?", answers));
        objectives.add(new CustomAnswerObjective("Mikor alapították a VIK-et?"));
        objectives.add(new PictureObjective("Készítsetek egy szelfit és egy képet a környezetről!", 2));
        objectives.add(new PhysicalObjective("Fogj kezet a feladat felügyelőjével!"));
        return objectives;
    }

    private static ArrayList<Station> stationListGenerator(){
        ArrayList<Station> list = new ArrayList<>();
        list.add(new Station(0, 0, Station.Status.Started, mockMiniObjectiveList()));
        list.add(new Station(2, 1, Station.Status.Done));
        list.add(new Station(4, 2, Station.Status.Done));
        list.add(new Station(1, 3, Station.Status.Started, mockBigObjectiveList()));
        list.add(new Station(3, 4, Station.Status.NotStarted));
        return list;
    }

    public static Bundle mockStatusData(){
        Bundle bundle = new Bundle();
        bundle.putString(ClientStatusFragment.ARG_RACENAME, "Ez a verseny neve");
        bundle.putString(ClientStatusFragment.ARG_TEAMNAME, "Ez a csapat neve");
        bundle.putString(ClientStatusFragment.ARG_CAPTAIN, "Ez a kapitány neve");
        bundle.putString(ClientStatusFragment.ARG_STATIONS, "Állomás: 5/7");
        bundle.putString(ClientStatusFragment.ARG_PHONE, "+36 30 371 7378");
        return bundle;
    }

    public static Bundle mockHelpData(){
        Bundle i = new Bundle();
        i.putStringArrayList(ClientHelpActivity.ARG_OBJECTADMINS, objectiveAdminNameGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_TOTALADMINS, totalAdminNameGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_OBJECTADMINPHONES, objectiveAdminPhoneGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_TOTALADMINPHONES, totalAdminPhoneGenerator());
        return i;
    }

    private static ArrayList<String> objectiveAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Alice Aladár");
        list.add("Bob Béla");
        return list;
    }

    private static ArrayList<String> totalAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Cinege Cecil");
        list.add("Dínom Dánom");
        list.add("Erik Elemér");
        return list;
    }

    private static ArrayList<String> objectiveAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+01 11 111 1111");
        list.add("+12 22 222 2222");
        return list;
    }

    private static ArrayList<String> totalAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+33 33 333 3333");
        list.add("+44 44 444 4444");
        list.add("+55 55 555 5555");
        return list;
    }

    public static String[] mockResultNames(){
        String[] teams = {"Narancs csapat", "Zöld csapat", "Piros csapat", "Kék csapat", "Sárga csapat", "Hupikék csapat"};
        return teams;
    }

    public static int[] mockResultPoints(){
        int[] points = {64, 23, 18, 12, 6, 2};
        return points;
    }
}
